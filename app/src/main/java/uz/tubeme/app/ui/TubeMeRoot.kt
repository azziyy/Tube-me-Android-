package uz.tubeme.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import uz.tubeme.app.data.repository.TubeMeRepository
import uz.tubeme.app.ui.components.AppTopBar
import uz.tubeme.app.ui.components.BottomBar
import uz.tubeme.app.ui.screens.collection_detail.CollectionDetailScreen
import uz.tubeme.app.ui.screens.collections.CollectionsScreen
import uz.tubeme.app.ui.screens.favorites.FavoritesScreen
import uz.tubeme.app.ui.screens.home.HomeScreen
import uz.tubeme.app.ui.screens.profile.ProfileScreen
import uz.tubeme.app.ui.screens.search.SearchScreen
import uz.tubeme.app.ui.screens.section.SectionScreen
import uz.tubeme.app.ui.screens.video.VideoScreen

object Routes {
    const val HOME = "home"
    const val COLLECTIONS = "collections"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
    const val VIDEO = "video/{videoId}"
    const val SECTION = "section/{sectionName}"
    const val COLLECTION_DETAIL = "collection/{collectionId}"

    fun video(id: String) = "video/$id"
    fun section(name: String) = "section/${java.net.URLEncoder.encode(name, "UTF-8")}"
    fun collection(id: String) = "collection/$id"
}

@Composable
fun TubeMeRoot() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route

    val rootTabs = setOf(Routes.HOME, Routes.COLLECTIONS, Routes.SEARCH, Routes.FAVORITES, Routes.PROFILE)
    val showBars = route in rootTabs

    // Bootstrap data once
    LaunchedEffect(Unit) { TubeMeRepository.refreshAll() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { if (showBars) AppTopBar(navController = nav) },
        bottomBar = { if (showBars) BottomBar(navController = nav, currentRoute = route) }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            NavHost(navController = nav, startDestination = Routes.HOME) {
                composable(Routes.HOME) { HomeScreen(navController = nav) }
                composable(Routes.COLLECTIONS) { CollectionsScreen(navController = nav) }
                composable(Routes.SEARCH) { SearchScreen(navController = nav) }
                composable(Routes.FAVORITES) { FavoritesScreen(navController = nav) }
                composable(Routes.PROFILE) { ProfileScreen(navController = nav) }

                composable(
                    Routes.VIDEO,
                    arguments = listOf(navArgument("videoId") { type = NavType.StringType })
                ) {
                    val id = it.arguments?.getString("videoId").orEmpty()
                    VideoScreen(navController = nav, videoId = id)
                }
                composable(
                    Routes.SECTION,
                    arguments = listOf(navArgument("sectionName") { type = NavType.StringType })
                ) {
                    val name = java.net.URLDecoder.decode(it.arguments?.getString("sectionName").orEmpty(), "UTF-8")
                    SectionScreen(navController = nav, sectionName = name)
                }
                composable(
                    Routes.COLLECTION_DETAIL,
                    arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
                ) {
                    val id = it.arguments?.getString("collectionId").orEmpty()
                    CollectionDetailScreen(navController = nav, collectionId = id)
                }
            }
        }
    }
}
