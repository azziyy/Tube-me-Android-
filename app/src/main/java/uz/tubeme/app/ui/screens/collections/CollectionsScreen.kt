package uz.tubeme.app.ui.screens.collections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uz.tubeme.app.R
import uz.tubeme.app.data.repository.TubeMeRepository
import uz.tubeme.app.ui.Routes
import uz.tubeme.app.ui.components.CollectionCard
import uz.tubeme.app.ui.components.EmptyState
import uz.tubeme.app.ui.components.Shimmer

@Composable
fun CollectionsScreen(navController: NavController) {
    val list by TubeMeRepository.collections.collectAsState()
    val loading by TubeMeRepository.isLoading.collectAsState()

    if (list.isEmpty() && loading) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(4) { Shimmer(Modifier.fillMaxWidth().aspectRatio(16f / 9f)) }
        }
        return
    }
    if (list.isEmpty()) {
        EmptyState(
            stringResource(R.string.collections_empty),
            stringResource(R.string.collections_empty_sub),
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                stringResource(R.string.collections_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        items(list) { col ->
            CollectionCard(col) { navController.navigate(Routes.collection(col.collectionId)) }
        }
    }
}
