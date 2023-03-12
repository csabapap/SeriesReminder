package hu.csabapap.seriesreminder.ui.main.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
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
import hu.csabapap.seriesreminder.data.db.entities.SRNextEpisode
import hu.csabapap.seriesreminder.data.network.TVDB_BANNER_URL
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem

@Composable
fun HomeScreenUi(
    viewModel: HomeViewModel,
    onShowItemClick: (item: ShowItem) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    when (val newState = state) {
        is ContentLoaded -> {
            Column(modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
            ) {
                if (newState.nextEpisodes.isNotEmpty()) {
                    NextEpisodesSection(sectionTitle = stringResource(id = R.string.overview_next_episode), items = newState.nextEpisodes)
                }
                if (newState.myShows.isNotEmpty()) {
                    ShowsSection(stringResource(id = R.string.title_my_shows), newState.myShows, onShowItemClick)
                }
                if (newState.trendingShows.isNotEmpty()) {
                    ShowsSection(
                        stringResource(id = R.string.title_trending),
                        newState.trendingShows,
                        onShowItemClick
                    )
                }
                if (newState.popularShows.isNotEmpty()) {
                    ShowsSection(
                        stringResource(id = R.string.title_popular),
                        newState.popularShows,
                        onShowItemClick
                    )
                }
            }
        }
        else -> { }// NO-OP
    }
}

@Composable
fun NextEpisodesSection(
    sectionTitle: String,
    items: List<SRNextEpisode>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        NextEpisodesList(items = items)
    }
}

@Composable
fun ShowsSection(
    sectionTitle: String,
    items: List<ShowItem>,
    onShowItemClick: (item: ShowItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        CollectionList(items = items, onItemClick = onShowItemClick)
    }
}

@Composable
fun CollectionList(items: List<ShowItem>, onItemClick: (item: ShowItem) -> Unit) {
    LazyRow(
        modifier = Modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = items,
            key = {item -> item.traktId},
        ) {
            ShowListItem(
                item = it,
                onItemClick
            )
        }
    }
}

@Composable
fun ShowListItem(
    item: ShowItem,
    onShowItemClick: (item: ShowItem) -> Unit
) {
    AsyncImage(
        model = getThumbnailUrl(item.poster),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(100.dp)
            .height(150.dp)
            .clickable {
                onShowItemClick(item)
            }
    )
}

@Composable
fun NextEpisodesList(
    items: List<SRNextEpisode>
) {
    LazyRow(
        modifier = Modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = items,
            key = {item -> item.episodeId},
        ) {
            EpisodeListItem(
                item = it,
            )
        }
    }
}

@Composable
fun EpisodeListItem(
    item: SRNextEpisode
) {
    Card {
        Column(modifier = Modifier.width(272.dp).height(196.dp)) {
            AsyncImage(
                model = "$TVDB_BANNER_URL${item.episodeImage}",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(120.dp)
            )
            Text(
                text = item.episodeTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = item.showTitle,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
        }
    }
}