package com.example.debitter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.debitter.model.DocumentKind
import com.example.debitter.model.SavedDocument
import com.example.debitter.model.SavedLetter
import com.example.debitter.model.SavedNote
import com.example.debitter.pdf.SaveLocation
import com.example.debitter.ui.editor.EditorEvent
import com.example.debitter.ui.editor.EditorScreen
import com.example.debitter.ui.editor.EditorViewModel
import com.example.debitter.ui.home.HomeScreen
import com.example.debitter.ui.letter.LetterEvent
import com.example.debitter.ui.letter.LetterScreen
import com.example.debitter.ui.letter.LetterViewModel
import com.example.debitter.ui.preview.PreviewScreen
import com.example.debitter.ui.recent.RecentScreen
import com.example.debitter.ui.recent.RecentViewModel
import com.example.debitter.ui.settings.SettingsScreen
import com.example.debitter.ui.settings.SettingsViewModel

private const val ROUTE_HOME: String = "home"
private const val ROUTE_LETTER: String = "letter"
private const val ROUTE_LETTER_PREVIEW: String = "letter-preview"
private const val ROUTE_LETTER_RECENT: String = "letter-recent"
private const val ROUTE_NOTE: String = "note"
private const val ROUTE_NOTE_PREVIEW: String = "note-preview"
private const val ROUTE_NOTE_RECENT: String = "note-recent"
private const val ROUTE_SETTINGS: String = "settings"

@Composable
fun DebitterApp() {
    val context = LocalContext.current
    val editorViewModel: EditorViewModel = viewModel()
    val letterViewModel: LetterViewModel = viewModel()
    val recentViewModel: RecentViewModel = viewModel(factory = RecentViewModel.factory(context))
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(context))
    val navController = rememberNavController()
    val onEdit: (SavedDocument) -> Unit = { saved ->
        when (saved) {
            is SavedLetter -> {
                letterViewModel.onEvent(LetterEvent.LoadLetter(letter = saved.letter))
                navController.popBackStack(ROUTE_LETTER, false)
            }
            is SavedNote -> {
                editorViewModel.onEvent(EditorEvent.LoadNote(note = saved.note))
                navController.popBackStack(ROUTE_NOTE, false)
            }
        }
    }

    var savedMessage by remember { mutableStateOf<String?>(null) }

    NavHost(navController = navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            HomeScreen(
                onOpen = { kind ->
                    navController.navigate(
                        when (kind) {
                            DocumentKind.DEBIT_NOTE -> ROUTE_NOTE
                            DocumentKind.REFUND_LETTER -> ROUTE_LETTER
                        },
                    )
                },
                onSettings = { navController.navigate(ROUTE_SETTINGS) },
            )
        }
        composable(ROUTE_SETTINGS) {
            val settings by settingsViewModel.state.collectAsStateWithLifecycle()

            SettingsScreen(
                onBack = { navController.popBackStack() },
                onDeleteAgent = settingsViewModel::deleteAgent,
                onDeleteCustomer = settingsViewModel::deleteCustomer,
                onSaveAgent = settingsViewModel::saveAgent,
                onSaveCustomer = settingsViewModel::saveCustomer,
                state = settings,
            )
        }
        composable(ROUTE_NOTE) {
            val note by editorViewModel.state.collectAsStateWithLifecycle()
            val settings by settingsViewModel.state.collectAsStateWithLifecycle()

            EditorScreen(
                customers = settings.noteCustomers,
                note = note,
                onBack = { navController.popBackStack() },
                onEvent = editorViewModel::onEvent,
                onPreview = { navController.navigate(ROUTE_NOTE_PREVIEW) },
                onRecent = {
                    recentViewModel.refresh()
                    navController.navigate(ROUTE_NOTE_RECENT)
                },
            )
        }
        composable(ROUTE_NOTE_PREVIEW) {
            val note by editorViewModel.state.collectAsStateWithLifecycle()

            PreviewScreen(
                document = note,
                onBack = { navController.popBackStack() },
                onSaved = { location ->
                    recentViewModel.save(note)
                    savedMessage = locationMessage(location)
                    navController.navigate(ROUTE_NOTE_RECENT) { popUpTo(ROUTE_NOTE) }
                    editorViewModel.onEvent(EditorEvent.Reset)
                },
            )
        }
        composable(ROUTE_NOTE_RECENT) {
            val recentState by recentViewModel.state.collectAsStateWithLifecycle()

            RecentScreen(
                kind = DocumentKind.DEBIT_NOTE,
                message = savedMessage,
                onBack = { navController.popBackStack() },
                onDelete = recentViewModel::delete,
                onEdit = onEdit,
                onMessageShown = { savedMessage = null },
                state = recentState,
            )
        }
        composable(ROUTE_LETTER) {
            val letter by letterViewModel.state.collectAsStateWithLifecycle()
            val settings by settingsViewModel.state.collectAsStateWithLifecycle()

            LetterScreen(
                agents = settings.shippingAgents,
                letter = letter,
                letterheads = settings.letterheads,
                onBack = { navController.popBackStack() },
                onEvent = letterViewModel::onEvent,
                onPreview = { navController.navigate(ROUTE_LETTER_PREVIEW) },
                onRecent = {
                    recentViewModel.refresh()
                    navController.navigate(ROUTE_LETTER_RECENT)
                },
            )
        }
        composable(ROUTE_LETTER_PREVIEW) {
            val letter by letterViewModel.state.collectAsStateWithLifecycle()

            PreviewScreen(
                document = letter,
                onBack = { navController.popBackStack() },
                onSaved = { location ->
                    recentViewModel.save(letter)
                    savedMessage = locationMessage(location)
                    navController.navigate(ROUTE_LETTER_RECENT) { popUpTo(ROUTE_LETTER) }
                    letterViewModel.onEvent(LetterEvent.Reset)
                },
            )
        }
        composable(ROUTE_LETTER_RECENT) {
            val recentState by recentViewModel.state.collectAsStateWithLifecycle()

            RecentScreen(
                kind = DocumentKind.REFUND_LETTER,
                message = savedMessage,
                onBack = { navController.popBackStack() },
                onDelete = recentViewModel::delete,
                onEdit = onEdit,
                onMessageShown = { savedMessage = null },
                state = recentState,
            )
        }
    }
}

private fun locationMessage(location: SaveLocation): String = if (location.isShared) {
    "Saved to Downloads. Open it from your Files app under Downloads."
} else {
    "Saved to the app's own Downloads folder. This version of Android blocks the shared one, so use Share to send it out."
}
