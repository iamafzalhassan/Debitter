# Debitter

A native Android application built with Kotlin and Jetpack Compose that generates precisely aligned debit notes and container deposit refund letters for a freight forwarding and customs clearing business in Colombo, replacing a manual Word-document workflow.

## Project Overview

Customs clearing agents send debit notes to bill statutory and service charges, and refund letters to reclaim container deposits from shipping agents. Debitter turns both into a fast, form-driven flow that produces print-ready PDFs. Every piece of text on the generated document is editable in the app, including the company header, document title, field labels, section headings, totals labels and signature caption. None of it is hardcoded in the renderer.

## Key Features

**Debit Notes**
- Shipment header: date, bill-to customer, vessel/flight, customs entry, container, BL/AWB, voyage and consignment
- Two charge sections (Statutory and Other), prefilled from built-in presets
- Add, edit, rename and delete charge rows freely
- Live totals: sub total, advance received and final total, all computed and never hand-editable
- Empty charge rows are omitted from the printed output
- Customer selection from a saved directory

**Container Deposit Refund Letters**
- Letterhead selection from a saved customer directory
- Shipping agent selection with name and address
- Reference fields for container, BL, vessel, voyage and receipt numbers
- Editable letter text and labels

**PDF Generation**
- Custom layout engine drawn directly on `Canvas` with Android's native `PdfDocument`, with no third-party PDF library
- Debit notes on A5 portrait, refund letters on A4
- Right-aligned amount column with tabular figures so every decimal point lines up
- Shared label width across both charge sections, fixed colon column and a consistent baseline rhythm
- Hairline rules snapped to half-pixels for crisp output
- Automatic overflow onto additional pages
- Bundled Inter and Inter Display typefaces, verified at load time

**Preview, Save & Share**
- True-to-page bitmap preview rendered with `PdfRenderer`
- Save to `Downloads/Debitter/` through MediaStore, with an app-storage fallback
- Share through the system share sheet using a FileProvider
- Self-pruning share cache with 24-hour retention

**Recent Documents**
- Saved debit notes and refund letters persisted in SQLite
- Reopen any saved document to edit, re-export or share it
- Tolerant JSON decoding: missing or malformed fields fall back to defaults, so older records never crash the list

**Directory Management**
- Customers (letterheads) and shipping agents stored in SQLite and editable from Settings
- Seeded once from presets on first install
- Non-destructive schema migrations that never drop saved documents

**Financial Accuracy**
- All money handled as `BigDecimal`, never `Double` or `Float`
- One thread-safe money formatter shared by the UI and the PDF, so the two can never disagree

## Architecture Highlights

- MVVM with unidirectional data flow
- Immutable data classes as the single source of truth
- `StateFlow` state exposed to Compose, with sealed-class events travelling up
- `SavedStateHandle` so rotation or backgrounding never loses an in-progress document
- A pure rendering layer: `pdf/` depends only on `Canvas`, `Paint`, `PdfDocument` and `Typeface`
- A centralized design system (`AppSpacing`, `AppColors`, `AppTextStyles`) with no magic numbers in composables
- No code generation: no KAPT, no KSP, hand-written JSON with `org.json`

## Technical Stack

- **Language:** Kotlin 2.2, JDK 17
- **UI:** Jetpack Compose, Material 3
- **Navigation:** Navigation Compose
- **Lifecycle:** AndroidX Lifecycle, ViewModel Compose
- **PDF:** `android.graphics.pdf.PdfDocument`, `PdfRenderer`
- **Persistence:** SQLite (`SQLiteOpenHelper`)
- **File Sharing:** MediaStore, FileProvider
- **Typography:** Inter and Inter Display (bundled)
- **Platform:** Android, minSdk 26

## Core Screens

1. **Home** - Choose between Debit Note and Refund Letter
2. **Debit Note Editor** - Header fields, charge sections, totals and document text
3. **Refund Letter Editor** - Letterhead, agent, references and letter text
4. **Preview** - Rendered page preview with save and share actions
5. **Recent** - Saved notes and letters with reopen, save and share actions
6. **Settings** - Customer and shipping agent directory management
