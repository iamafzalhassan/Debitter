package com.example.debitter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.debitter.ui.editor.EditorScreen
import com.example.debitter.ui.editor.EditorViewModel
import com.example.debitter.ui.preview.PreviewScreen

private const val ROUTE_EDITOR: String = "editor"

private const val ROUTE_PREVIEW: String = "preview"

@Composable
fun DebitterApp() {
    val viewModel: EditorViewModel = viewModel()
    val navController = rememberNavController()
    val state by viewModel.state.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = ROUTE_EDITOR) {
        composable(ROUTE_EDITOR) {
            EditorScreen(state = state, onEvent = viewModel::onEvent, onPreview = { navController.navigate(ROUTE_PREVIEW) })
        }
        composable(ROUTE_PREVIEW) {
            PreviewScreen(onBack = { navController.popBackStack() }, note = state.note)
        }
    }
}
