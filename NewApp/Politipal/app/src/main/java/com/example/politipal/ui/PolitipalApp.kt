

package com.example.politipal.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.example.politipal.ui.navigation.ModalNavigationDrawerContent
import com.example.politipal.ui.navigation.PermanentNavigationDrawerContent
import com.example.politipal.ui.navigation.ReplyBottomNavigationBar
import com.example.politipal.ui.navigation.ReplyNavigationActions
import com.example.politipal.ui.navigation.ReplyNavigationRail
import com.example.politipal.ui.navigation.PolitipalRoute
import com.example.politipal.ui.navigation.ReplyTopLevelDestination
import com.example.politipal.ui.utils.DevicePosture
import com.example.politipal.ui.utils.ReplyContentType
import com.example.politipal.ui.utils.ReplyNavigationContentPosition
import com.example.politipal.ui.utils.ReplyNavigationType
import kotlinx.coroutines.launch

@Composable
fun PolitipalApp(
    // Parameters
    windowSize: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    homeUIState: homeUIState,
    closeDetailScreen: () -> Unit = {},
    navigateToDetail: (Long, ReplyContentType) -> Unit = { _, _ -> },
    toggleSelectedEmail: (Long) -> Unit = { }
) {
    val navigationType: ReplyNavigationType = ReplyNavigationType.BOTTOM_NAVIGATION
    val contentType: ReplyContentType = ReplyContentType.SINGLE_PANE
    val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
    val foldingDevicePosture = DevicePosture.NormalPosture
    val navigationContentPosition = ReplyNavigationContentPosition.TOP

    ReplyNavigationWrapper(
        navigationType = navigationType,
        contentType = contentType,
        displayFeatures = displayFeatures,
        navigationContentPosition = navigationContentPosition,
        homeUIState = homeUIState,
        closeDetailScreen = closeDetailScreen,
        navigateToDetail = navigateToDetail,
        toggleSelectedEmail = toggleSelectedEmail
    )
}

@Composable
private fun ReplyNavigationWrapper(
    navigationType: ReplyNavigationType,
    contentType: ReplyContentType,
    displayFeatures: List<DisplayFeature>,
    navigationContentPosition: ReplyNavigationContentPosition,
    homeUIState: homeUIState,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long, ReplyContentType) -> Unit,
    toggleSelectedEmail: (Long) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        ReplyNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination =
        navBackStackEntry?.destination?.route ?: PolitipalRoute.SETTINGS

    if (navigationType == ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER) {
        // TODO check on custom width of PermanentNavigationDrawer: b/232495216
        PermanentNavigationDrawer(drawerContent = {
            PermanentNavigationDrawerContent(
                selectedDestination = selectedDestination,
                navigationContentPosition = navigationContentPosition,
                navigateToTopLevelDestination = navigationActions::navigateTo,
            )
        }) {
            ReplyAppContent(
                navigationType = navigationType,
                contentType = contentType,
                displayFeatures = displayFeatures,
                navigationContentPosition = navigationContentPosition,
                homeUIState = homeUIState,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationActions::navigateTo,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                toggleSelectedEmail = toggleSelectedEmail
            )
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                ModalNavigationDrawerContent(
                    selectedDestination = selectedDestination,
                    navigationContentPosition = navigationContentPosition,
                    navigateToTopLevelDestination = navigationActions::navigateTo,
                    onDrawerClicked = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            },
            drawerState = drawerState
        ) {
            ReplyAppContent(
                navigationType = navigationType,
                contentType = contentType,
                displayFeatures = displayFeatures,
                navigationContentPosition = navigationContentPosition,
                homeUIState = homeUIState,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationActions::navigateTo,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                toggleSelectedEmail = toggleSelectedEmail
            ) {
                scope.launch {
                    drawerState.open()
                }
            }
        }
    }
}

@Composable
fun ReplyAppContent(
    modifier: Modifier = Modifier,
    navigationType: ReplyNavigationType,
    contentType: ReplyContentType,
    displayFeatures: List<DisplayFeature>,
    navigationContentPosition: ReplyNavigationContentPosition,
    homeUIState: homeUIState,
    navController: NavHostController,
    selectedDestination: String,
    navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long, ReplyContentType) -> Unit,
    toggleSelectedEmail: (Long) -> Unit,
    onDrawerClicked: () -> Unit = {}
) {
    Row(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == ReplyNavigationType.NAVIGATION_RAIL) {
            ReplyNavigationRail(
                selectedDestination = selectedDestination,
                navigationContentPosition = navigationContentPosition,
                navigateToTopLevelDestination = navigateToTopLevelDestination,
                onDrawerClicked = onDrawerClicked,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            ReplyNavHost(
                navController = navController,
                contentType = contentType,
                displayFeatures = displayFeatures,
                homeUIState = homeUIState,
                navigationType = navigationType,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                toggleSelectedEmail = toggleSelectedEmail,
                modifier = Modifier.weight(1f),
            )
            AnimatedVisibility(visible = navigationType == ReplyNavigationType.BOTTOM_NAVIGATION) {
                ReplyBottomNavigationBar(
                    selectedDestination = selectedDestination,
                    navigateToTopLevelDestination = navigateToTopLevelDestination
                )
            }
        }
    }
}

@Composable
private fun ReplyNavHost(
    navController: NavHostController,
    contentType: ReplyContentType,
    displayFeatures: List<DisplayFeature>,
    homeUIState: homeUIState,
    navigationType: ReplyNavigationType,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long, ReplyContentType) -> Unit,
    toggleSelectedEmail: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = PolitipalRoute.HOME,
    ) {
        composable(PolitipalRoute.SETTINGS) {
            EmptyComingSoon()
        }
        composable(PolitipalRoute.PROFILE) {
            EmptyComingSoon()
        }
        composable(PolitipalRoute.BILLS) {
            EmptyComingSoon()
        }
        composable(PolitipalRoute.REPS) {
            EmptyComingSoon()
        }
        composable(PolitipalRoute.HOME) {
            HomeScreen(
                contentType = contentType,
                homeUIState = homeUIState,
                navigationType = navigationType,
                displayFeatures = displayFeatures,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                toggleSelectedEmail = toggleSelectedEmail
            )
        }
    }
}
