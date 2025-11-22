package com.leafy.features.note.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.note.screen.NoteScreen
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.noteNavGraph(
    navController: NavController
){
    composable<MainNavigationRoute.NoteTab>{
        NoteScreen()
    }
}