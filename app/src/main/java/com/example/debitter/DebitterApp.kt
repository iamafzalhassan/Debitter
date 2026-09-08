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
    val editorState by editorViewModel.state.collectAsStateWithLifecycle()
    val recentState by recentViewModel.state.collectAsStateWithLifecycle()

    var savedMessage by remember { mutableStateOf<String?>(null) }

    NavHost(navController = navController, startDestination = ROUTE_EDITOR) {
        composable(ROUTE_EDITOR) {
            EditorScreen(
                state = editorState,
                onEvent = editorViewModel::onEvent,
                onPreview = { navController.navigate(ROUTE_PREVIEW) },
                onRecent = {
                    recentViewModel.refresh()
                    navController.navigate(ROUTE_RECENT)
                },
            )
        }
        composable(ROUTE_PREVIEW) {
            PreviewScreen(
                onBack = { navController.popBackStack() },
                onSaved = { location ->
                    recentViewModel.save(editorState.note)
                    savedMessage = "Saved to Downloads. Open it from your Files app under Downloads."
                    navController.navigate(ROUTE_RECENT) { popUpTo(ROUTE_EDITOR) }
                    editorViewModel.onEvent(EditorEvent.Reset)
                },
                note = editorState.note,
            )
        }
        composable(ROUTE_RECENT) {
            RecentScreen(
                onBack = { navController.popBackStack() },
                message = savedMessage,
                onDelete = recentViewModel::delete,
                onEdit = { saved ->
                    editorViewModel.onEvent(EditorEvent.LoadNote(saved.note))
                    navController.popBackStack(ROUTE_EDITOR, false)
                },
                onMessageShown = { savedMessage = null },
                state = recentState,
            )
        }
    }
}
