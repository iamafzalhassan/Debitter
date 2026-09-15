# Debitter

![Kotlin](https://img.shields.io/badge/Kotlin-2.2-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?logo=jetpackcompose&logoColor=white)
![PDF](https://img.shields.io/badge/PDF-native_Canvas-C0392B)
![Platform](https://img.shields.io/badge/platform-Android_8.0%2B-3DDC84?logo=android&logoColor=white)

A native Android app, built with Kotlin and Jetpack Compose, that produces precisely aligned debit notes and container deposit refund letters for a freight forwarding and customs clearing business in Colombo. It replaces a manual Word-document workflow with a fast, form-driven flow that ends in a print-ready PDF.

Every piece of text on a generated document is editable in the app: the company header, document title, field labels, section headings, totals labels and signature caption. None of it is hardcoded in the renderer.

## Features

- **Debit notes** with a shipment header (date, bill-to customer, vessel or flight, customs entry, container, BL or AWB, voyage and consignment) and two charge sections, Statutory and Other, prefilled from built-in presets.
- **Charge rows** that can be added, renamed and edited freely. A row left at zero is simply omitted from the printed note.
- **Live totals**: sub total, advance received and final total, always computed and never typed. The advance is re-validated whenever the charges change, so the total can never go negative.
- **Container deposit refund letters** with a letterhead chosen from the customer directory, a shipping agent, and reference fields for container, BL, vessel, voyage and receipt numbers.
- **True-to-page preview** rendered from the actual PDF, then save to `Downloads/Debitter/` or share through the system share sheet.
- **Recent documents**: every saved debit note and refund letter can be reopened, edited, re-exported or shared.
- **Customer and shipping agent directory** managed from Settings and seeded once from presets on first install.
- **No permissions and no network.** Everything stays on the device.

## Architecture

- **MVVM with unidirectional data flow.** `EditorViewModel` and `LetterViewModel` expose an immutable document as a `StateFlow`; screens send sealed events back through a single entry point.
- **State survives the process.** In-progress documents are kept in `SavedStateHandle`, so rotation or backgrounding never loses a half-written note.
- **A pure rendering layer.** `pdf/` depends only on `Canvas`, `Paint`, `PdfDocument` and `Typeface`; it imports no Compose, no ViewModel and no UI type.
- **Layering rules.** Composables never touch presets or defaults directly, and ViewModels never import a Compose UI type or a `Context`.
- **No code generation.** No KAPT, no KSP and no serialization plugin; JSON is written by hand with `org.json`.

## How the PDF works

Both documents are laid out by a custom engine drawn directly on `Canvas` with Android's `PdfDocument`, with no third-party PDF library.

- **Debit notes are A5 portrait** (420 × 595 pt, 24 pt margins); **refund letters are A4 portrait** (595 × 842 pt, 56 pt margins). Each has its own layout object, so one can never disturb the other.
- **One vertical rhythm.** Every band on the page is separated by the same gap, and a blank block collapses together with its gap.
- **Decimal-aligned money.** Amounts are right-aligned on a fixed x-position with tabular figures on every paint that draws digits, so every `.00` lands on the same vertical line.
- **One continuous table.** The charge sections and totals share a single opening rule, a filled band per section heading and one hairline between rows, so no divider is ever drawn twice.
- **A fixed label column** shared by both charge sections, so neither section can have a ragged label edge.
- **Top-aligned grid cells.** A one-line value beside a wrapped one sits on the wrapped value's first line.
- **Crisp hairlines** drawn on half-pixel-snapped coordinates.
- **Mixed-weight wrapping.** The refund letter body wraps runs of regular and bold text, keeping a bold word and its punctuation together.
- **Automatic overflow** onto a new page when content runs past the bottom margin.
- **Bundled Inter and Inter Display** typefaces, resolved once and verified at load time rather than silently falling back to a system font.

## How the money works

- **Money is `BigDecimal`**, never `Double` or `Float`, because customs duties run into the millions of rupees.
- **One formatter for UI and PDF.** A single `#,##0.00` formatter is shared by the editor and the renderer, so the two can never disagree. It is held in a `ThreadLocal`, because the PDF renders off the main thread while the totals recompose on it.

## Storage

- **SQLite through `SQLiteOpenHelper`**, with tables for recent notes, recent letters, customers and shipping agents.
- **Non-destructive migrations.** Each schema upgrade only creates what is missing and never drops a saved document.
- **Tolerant decoding.** Saved documents are stored as hand-encoded JSON; a missing or malformed field falls back to its default, so an older record never crashes the list.
- **Saving without permissions.** Files go to the public Downloads folder through `MediaStore` on Android 10 and later, and to the app's own Downloads folder on older versions.
- **A self-pruning share cache.** Shared files are served through a `FileProvider`, and anything older than 24 hours is removed.

## Tech stack

| Area | Choice |
|---|---|
| Language | Kotlin 2.2, JDK 17 |
| UI | Jetpack Compose, Material 3 |
| Navigation | Navigation Compose |
| Lifecycle | AndroidX Lifecycle, ViewModel Compose, SavedStateHandle |
| PDF | `android.graphics.pdf.PdfDocument`, `PdfRenderer` |
| Persistence | SQLite (`SQLiteOpenHelper`), `org.json` |
| Files | MediaStore, FileProvider |
| Platform | Android 8.0 (API 26) and later, compile SDK 37 |

## Design system

Debitter shares one design system with two other apps of mine: warm paper surfaces, navy ink, and a dotted divider as the signature motif. Colours, spacing and typography are tokens in `ui/theme`, with Inter for all text and tabular figures on every amount. Shared components (`AppTextField`, `AmountField`, `AppButtons`, `AppSnackbar`, `SheetFrame`, `PresetSheet`, `PresetTile`, `SectionHeader`, `ExpandablePanel`, `DottedDivider`) live in `ui/components`. Screens never use a raw colour or a bare measurement.

## Code conventions

- A strict member ordering convention for every Kotlin file: properties sorted by type tier, then type, then name; functions ordered by call order.
- No comments in source. Names, types and ordering carry the meaning.
- ktlint formatting at a 240-column line width.

## Project structure

```
app/src/main/java/com/example/debitter/
    DebitterApp.kt, MainActivity.kt
    model/          DebitNote, RefundLetter, ChargeLine, ChargeSection, Letterhead, ShippingAgent, DocumentKind
    data/           Presets, defaults, hand-written JSON, directory and recent-document repositories
    data/sources/   NoteDatabase
    pdf/            PdfLayout, LetterLayout, DebitNotePdfGenerator, RefundLetterPdfGenerator, PdfTypefaces, PdfExporter
    ui/theme/       AppColors, AppSpacing, AppTextStyles, Theme
    ui/components/  Shared design-system components
    ui/home/        Document type chooser
    ui/editor/      Debit note editor
    ui/letter/      Refund letter editor
    ui/preview/     Page preview, save and share
    ui/recent/      Saved documents
    ui/settings/    Customer and shipping agent directory
    util/           MoneyFormat, DateFormat, RecentDateFormat
docs/spec.md        Original specification
```

## Building

**Requirements:** Android Studio with the Android SDK (compile SDK 37) and JDK 17.

- Open the project in Android Studio and run the `app` configuration, or run `./gradlew :app:assembleDebug`.
- For a signed release, add a `keystore.properties` file at the project root with `storeFile`, `storePassword`, `keyAlias` and `keyPassword`, then run `./gradlew :app:assembleRelease`.

## Roadmap

- Enable R8 shrinking for release builds
- An editable document date
