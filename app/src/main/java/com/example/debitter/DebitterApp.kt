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
import com.example.debitter.pdf.SaveLocation
import com.example.debitter.ui.editor.EditorEvent
import com.example.debitter.ui.editor.EditorScreen
import com.example.debitter.ui.editor.EditorViewModel
import com.example.debitter.ui.preview.PreviewScreen
import com.example.debitter.ui.recent.RecentScreen
import com.example.debitter.ui.recent.RecentViewModel

private const val ROUTE_EDITOR: String = "editor"
private const val ROUTE_PREVIEW: String = "preview"
private const val ROUTE_RECENT: String = "recent"

@Composable
fun DebitterApp() {
    val context = LocalContext.current
    val editorViewModel: EditorViewModel = viewModel()
    val recentViewModel: RecentViewModel = viewModel(factory = RecentViewModel.factory(context))
    val navController = rememberNavController()

    var savedMessage by remember { mutableStateOf<String?>(null) }

    NavHost(navController = navController, startDestination = ROUTE_EDITOR) {
        composable(ROUTE_EDITOR) {
            val editorState by editorViewModel.state.collectAsStateWithLifecycle()

            EditorScreen(
                onEvent = editorViewModel::onEvent,
                onPreview = { navController.navigate(ROUTE_PREVIEW) },
                onRecent = {
                    recentViewModel.refresh()
                    navController.navigate(ROUTE_RECENT)
                },
                state = editorState,
            )
        }
        composable(ROUTE_PREVIEW) {
            val editorState by editorViewModel.state.collectAsStateWithLifecycle()

            PreviewScreen(
                note = editorState.note,
                onBack = { navController.popBackStack() },
                onSaved = { location ->
                    recentViewModel.save(editorState.note, editorState.shipmentType)
                    savedMessage = locationMessage(location)
                    navController.navigate(ROUTE_RECENT) { popUpTo(ROUTE_EDITOR) }
                    editorViewModel.onEvent(EditorEvent.Reset)
                },
            )
        }
        composable(ROUTE_RECENT) {
            val recentState by recentViewModel.state.collectAsStateWithLifecycle()

            RecentScreen(
                message = savedMessage,
                onBack = { navController.popBackStack() },
                onDelete = recentViewModel::delete,
                onEdit = { saved ->
                    editorViewModel.onEvent(EditorEvent.LoadNote(note = saved.note, shipmentType = saved.shipmentType))
                    navController.popBackStack(ROUTE_EDITOR, false)
                },
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
