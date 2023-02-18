package hu.csabapap.seriesreminder.ui.main.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
        is ContentLoaded -> {
            Column(modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
            ) {
                if (newState.myShows.isNotEmpty()) {
                    MyShowsCard(newState.myShows)
                }
                if (newState.trendingShows.isNotEmpty()) {
                    TrendingShows(newState.trendingShows)
                }
                if (newState.popularShows.isNotEmpty()) {
                    TrendingShows(newState.popularShows)
                }
            }
        }
        else -> { }// NO-OP
    }
}

@Composable
fun MyShowsCard(items: List<ShowItem>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.title_my_shows),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        CollectionList(items = items)
    }
}

@Composable
fun TrendingShows(items: List<ShowItem>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.title_trending),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        CollectionList(items = items)
    }
}

@Composable
fun PopularShowsShows(items: List<ShowItem>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.title_popular),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        CollectionList(items = items)
    }
}

@Composable
fun CollectionList(items: List<ShowItem>) {
    LazyRow(
        modifier = Modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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

@Composable
fun ShowListItem(
    item: ShowItem
) {
    AsyncImage(
        model = getThumbnailUrl(item.poster),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(100.dp)
            .height(150.dp)
    )
}