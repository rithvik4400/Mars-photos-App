/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.marsphotos.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.marsphotos.R
import com.example.marsphotos.ui.screens.DetailScreen
import com.example.marsphotos.ui.screens.HomeScreen
import com.example.marsphotos.ui.screens.MarsViewModel

enum class MarsScreen(val title: Int) {
    Start(title = R.string.app_name),
    Detail(title = R.string.photo_detail)
}

@Composable
fun MarsPhotosApp() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val navController: NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: MarsScreen.Start.name
    
    val currentScreen = when {
        currentRoute == MarsScreen.Start.name -> MarsScreen.Start
        currentRoute.startsWith(MarsScreen.Detail.name) -> MarsScreen.Detail
        else -> MarsScreen.Start
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MarsTopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val marsViewModel: MarsViewModel = viewModel()
            NavHost(
                navController = navController,
                startDestination = MarsScreen.Start.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = MarsScreen.Start.name) {
                    HomeScreen(
                        marsUiState = marsViewModel.marsUiState,
                        onPhotoClicked = { photo ->
                            navController.navigate("${MarsScreen.Detail.name}/${photo.id}/${photo.getEncodedUrl()}")
                        }
                    )
                }
                composable(
                    route = "${MarsScreen.Detail.name}/{id}/{url}",
                    arguments = listOf(
                        navArgument("id") { type = NavType.StringType },
                        navArgument("url") { type = NavType.StringType }
                    )
                ) { navBackStackEntry ->
                    val id = navBackStackEntry.arguments?.getString("id") ?: ""
                    val url = navBackStackEntry.arguments?.getString("url") ?: ""
                    DetailScreen(id = id, imgSrc = url)
                }
            }
        }
    }
}

@Composable
fun MarsTopAppBar(
    currentScreen: MarsScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(currentScreen.title),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}
