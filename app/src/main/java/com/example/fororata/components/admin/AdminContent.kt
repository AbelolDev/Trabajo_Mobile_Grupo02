package com.example.fororata.components.admin

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fororata.api.dto.UserDTO
import com.example.fororata.ui.screen.admin.AdminPublicationsScreen

@Composable
fun AdminContent(
    innerPadding: PaddingValues,
    isModerador: Boolean,
    selectedTab: Int,
    users: List<UserDTO>,
    isLoading: Boolean,
    onTabSelected: (Int) -> Unit,
    onEditUser: (UserDTO) -> Unit,
    onDeleteUser: (UserDTO) -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        if (!isModerador) {
            AnimatedStatsSection(users = users)
        } else {
            ModeratorMessageCard()
        }

        if (!isModerador) {
            AdminTabs(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )

            TabContent(
                selectedTab = selectedTab,
                users = users,
                isLoading = isLoading,
                onEditUser = onEditUser,
                onDeleteUser = onDeleteUser,
                navController = navController
            )
        } else {
            PublicationsListSection(navController = navController)
        }
    }
}

@Composable
fun AdminTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(selectedTabIndex = selectedTab) {
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = { Text("Usuarios") },
            icon = { Icon(Icons.Filled.People, contentDescription = null) }
        )
        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = { Text("Publicaciones") },
            icon = { Icon(Icons.Filled.Description, contentDescription = null) }
        )
    }
}

@Composable
fun TabContent(
    selectedTab: Int,
    users: List<UserDTO>,
    isLoading: Boolean,
    onEditUser: (UserDTO) -> Unit,
    onDeleteUser: (UserDTO) -> Unit,
    navController: NavController
) {
    AnimatedContent(
        targetState = selectedTab,
        transitionSpec = {
            fadeIn(tween(300)) + slideInHorizontally() togetherWith
                    fadeOut(tween(300)) + slideOutHorizontally()
        },
        label = "tab_content"
    ) { tabIndex ->
        when (tabIndex) {
            0 -> UsersListSection(
                users = users,
                isLoading = isLoading,
                onEditUser = onEditUser,
                onDeleteUser = onDeleteUser
            )
            1 -> PublicationsListSection(navController = navController)
        }
    }
}