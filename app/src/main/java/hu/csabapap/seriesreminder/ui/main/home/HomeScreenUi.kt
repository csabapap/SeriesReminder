package hu.csabapap.seriesreminder.ui.main.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem

@Composable
fun HomeScreenUi(
    viewModel: HomeViewModel
) {
    val state by viewModel.uiState.collectAsState()
    when (val newState = state) {
        is MyShowsState -> CollectionList(newState.items)
        else -> { }// NO-OP
    }
}

@Composable
fun CollectionList(items: List<ShowItem>) {
    Column {
        Text(text = stringResource(id = R.string.title_my_shows), style = MaterialTheme.typography.titleMedium)
        LazyRow(
            modifier = Modifier
        ) {
            items(
                items = items,
                key = {item -> item.traktId}
            ) {
                ShowListItem(
                    item = it
                )
            }
        }
    }
}

@Composable
fun ShowListItem(
    item: ShowItem
) {
    AsyncImage(
        model = getThumbnailUrl(item.poster),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.width(64.dp)
    )
}