# Debit Note Generator — Build Spec

Android app for a freight forwarding / customs clearing business (Cargo World, Colombo).
Replaces a manual Word-document workflow. Single user, sideloaded APK, no Play Store.

## Stack

- Kotlin, Jetpack Compose, Material 3
- minSdk 26, targetSdk 35, JDK 17
- Navigation Compose, lifecycle-viewmodel-compose
- `android.graphics.pdf.PdfDocument` for output — no third-party PDF library
- Inter + Inter Display bundled as app fonts — see Typography below
- No database, no network, no permissions, no persistence

## Typography

Inter and Inter Display, bundled in the APK. Licensed SIL OFL, so redistribution is fine.
Download the static `.ttf` files from Google Fonts or rsms.me/inter — use the static
weights, not the variable font, since `PdfDocument` handles variable axes poorly.

Place in `app/src/main/res/font/`, lowercase filenames with underscores only:

```
inter_regular.ttf
inter_medium.ttf
inter_semibold.ttf
inter_bold.ttf
inter_display_semibold.ttf
inter_display_bold.ttf
```

Inter Display for the company name and the document title. Inter for everything else.

The two layers load fonts differently and both must be wired up:

**Compose** (`ui/theme/Type.kt`) — declare a `FontFamily` from the `R.font` resources and
build a `Typography` from it. For any numeric field in the editor, apply
`FontFeatureSettings("tnum")` on the `TextStyle` so amounts don't jitter as the user types.

**PDF renderer** — Compose typography is unavailable here. Load typefaces directly:

```kotlin
val inter = ResourcesCompat.getFont(context, R.font.inter_regular)
paint.typeface = inter
paint.fontFeatureSettings = "'tnum'"
```

Load each typeface once and hold it in `PdfLayout` rather than re-resolving per draw call.
Apply `'tnum'` to every `Paint` that draws digits — amounts, totals, dates, entry numbers.
Missing it on even one paint object breaks column alignment.

Verify the font actually resolved before rendering. If `getFont` returns null the renderer
silently falls back to the system default and the output will look subtly wrong rather
than failing loudly.

## Core requirement

Every piece of text that appears on the generated document must be editable in the app.
Nothing is a hardcoded string in the renderer. This includes the company header, the
document title, all field labels, both section headings, the totals labels, and the
signature caption — not just the values.

## Data model

```kotlin
data class DebitNote(
    val company: CompanyBlock,
    val labels: NoteLabels,
    val header: NoteHeader,
    val statutory: List<ChargeLine>,
    val other: List<ChargeLine>,
    val advanceReceived: BigDecimal?
)

data class CompanyBlock(
    val name: String,
    val addressLine: String,
    val contactLine: String
)

data class NoteLabels(
    val title: String,
    val date: String,
    val billTo: String,
    val vesselFlight: String,
    val customsEntry: String,
    val containerNo: String,
    val blAwbNo: String,
    val voyageNoDate: String,
    val consignment: String,
    val statutorySection: String,
    val otherSection: String,
    val subTotal: String,
    val advanceReceived: String,
    val total: String,
    val signature: String
)

data class NoteHeader(
    val date: LocalDate?,
    val billTo: String,
    val vesselFlight: String,
    val customsEntry: String,
    val containerNo: String,
    val blAwbNo: String,
    val voyageNoDate: String,
    val consignment: String
)

data class ChargeLine(
    val id: UUID,
    val label: String,
    val amount: BigDecimal?
)

enum class ShipmentType { CONTAINER, LCL, AIR_FREIGHT, PERSONAL, BLANK }
```

Use `BigDecimal` for all money. No `Double`, no `Float` — these are customs duties in
the millions of LKR and floating-point drift is unacceptable.

## Defaults

Company block:

```
CARGO WORLD
59/2, DEMATAGODA ROAD, MARADANA, COLOMBO, SRI LANKA.
TEL: +94 (77) 754 0094 | EMAIL: WORLDOFCARGO@OUTLOOK.COM
```

Label defaults: `D E B I T   N O T E`, `Date`, `To`, `Vessel/Flight`, `Customs Entry`,
`Container No`, `BL/AWB No`, `Voyage No/Date`, `Consignment`, `Statutory`, `Other`,
`Sub Total`, `Advanced Received`, `Total`, `Proprietor's Signature.`

## Charge presets

Hardcoded in a `ChargePresets` object, keyed by `ShipmentType`. These are starting
points, not constraints — the user adds, renames, reorders and deletes rows freely.

**Statutory (common to all types):**
Agency Fee / Service, Agent DO charges, Animal Quarantine charges,
Container Demurrage charges, Container OT charges, Container Weight charges,
Customs Duty, Customs OT, Grayline charges, Import Control charges,
SLPA charges, SLSI charges

**Statutory additions by type:**
- `AIR_FREIGHT`: Air Lanka charges, Air Line DO charges, Freight charges
- `CONTAINER`: Freight charges
- `LCL`: Freight charges
- `PERSONAL`: (none)
- `BLANK`: (none)

**Other (common to all types):**
Documentation charges, Examination expenses, Entry Passing expenses, Handling charges,
Missalation expenses, Screening Unit expenses, Transport charges, Transport Detention,
Valuation expenses

**Other additions by type:**
- `PERSONAL`: Clearance expenses, Freight charges, Unloading expenses

Charges seen in real documents but deliberately NOT in the presets — the user adds these
ad-hoc when needed, to keep the default list from bloating:
Central Environment Authority charges (Official), Central Environment Authority charges
(Unofficial), Fork Lift expenses, Registration expenses, Customs Penalty, RCT charges,
Wrong Nil Mark Application expenses

## Screens

### Editor (single scrolling screen)

- Shipment type selector at top. Switching swaps the preset roster but preserves any
  labels/amounts the user has already entered for rows that exist in both rosters.
- Collapsed "Document text" panel containing the company block fields and all label
  fields. Collapsed by default so the common path stays fast.
- Header fields: Date (picker, displays `dd-MM-yyyy`), To, Vessel/Flight, Customs Entry,
  Container No, BL/AWB No, Voyage No/Date, Consignment.
- Two charge sections. Each row: editable label + amount input. Per-row edit and delete.
  Per-section "add row". Long-press drag to reorder within a section.
- Sticky totals footer: Sub Total (computed), Advanced Received (input), Total (computed).
  Sub Total and Total are never hand-editable.
- Actions: Preview, Reset.

### Preview

Renders the generated PDF to a bitmap so the user sees the true A5 page before exporting.
Actions: Save to Downloads, Share (via FileProvider), Print.

## PDF output

- A5 portrait: 420 × 595 pt at 72dpi
- 28pt margins
- **Empty charge rows are omitted from the output.** A row with a blank or null amount
  does not print. This is the main visual departure from the source Word documents.
- Overflow beyond one page continues onto a second A5 page, repeating nothing but
  continuing the charge table.
- Amounts formatted `#,##0.00`

### Alignment — treat this as a primary requirement, not polish

The source Word documents are visually ragged and the user has explicitly asked for a
"very neat, perfectly aligned" result. Specifically:

- Amount column is right-aligned at a fixed x-position, so every `.00` falls on the same
  vertical line. Set `paint.fontFeatureSettings = "'tnum'"` on all numeric text so digits
  are equal-width.
- Measure the widest label across *both* charge sections once, and use that single width
  for both. Sections must not have independently ragged label columns.
- The colon column in the header grid sits at a fixed x-position, not derived from the
  preceding text width.
- Fixed row height with text drawn on a consistent baseline. No cumulative drift down
  the page.
- Hairline rules and table borders drawn at half-pixel-snapped coordinates so they render
  crisp rather than fat or blurred.
- Long values (consignment descriptions, custom charge names) wrap within their column
  rather than overrunning into the next.
- The totals block right edge aligns with the amount column above it.

Render the output and inspect it. Iterate on spacing before considering it done.

## Architecture

MVVM with unidirectional data flow.

- Single `EditorViewModel` holding immutable `DebitNote` state
- Expose `StateFlow<DebitNote>` to Compose
- Events travel up as sealed-class intents
- Use `SavedStateHandle` so rotation or backgrounding does not lose an in-progress note
- The PDF layer stays pure Kotlin, depending only on `Canvas`/`Paint` — no Compose, no
  ViewModel, no Android UI types. It must be unit-testable in isolation.
- Immutable data classes throughout. Single source of truth. No state duplicated between
  Compose and the ViewModel.

## Package structure

```
model/       DebitNote, CompanyBlock, NoteLabels, NoteHeader, ChargeLine, ShipmentType
data/        ChargePresets, Defaults
pdf/         DebitNotePdfGenerator, PdfLayout, PdfExporter
ui/editor/   EditorScreen, EditorViewModel, EditorEvent, components/
ui/preview/  PreviewScreen
ui/theme/    Color, Type, Theme
util/        MoneyFormat, DateFormat
```

## Code style

Consistent member ordering within classes throughout the codebase — properties, then
init, then public functions, then private functions. Apply the same ordering convention
everywhere; do not vary it file to file.

## Explicitly out of scope

Persistence, saved customer list, note history, custom charge catalogs, login, cloud
sync, Play Store packaging. The app starts fresh on every launch by design.
