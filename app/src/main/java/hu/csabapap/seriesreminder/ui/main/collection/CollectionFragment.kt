package hu.csabapap.seriesreminder.ui.main.collection

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.network.getFullSizeUrl
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.ui.adapters.CollectionAdapter
import hu.csabapap.seriesreminder.utils.Collectible
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class CollectionFragment : DaggerFragment(), CollectionAdapter.CollectionItemClickListener {

    @field:[Inject Named("Main")]
    lateinit var mainViewModelProvider: ViewModelProvider.Factory
    private val collectionViewModel: CollectionViewModel by viewModels { mainViewModelProvider }

    private var mListener: CollectionItemClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return ComposeView(requireContext()).apply {
            setContent {
                CollectionScreen(viewModel = collectionViewModel)
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CollectionItemClickListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface CollectionItemClickListener {
        fun onCollectionItemClick(show: SRShow)
    }

    override fun onCollectionItemClick(item: CollectionItem) {
        val show = item.show ?: return
        val activity = activity ?: return
        Collectible.start(activity, show.traktId, true)
    }
}

@Composable
fun CollectionScreen(
    modifier: Modifier = Modifier,
    viewModel: CollectionViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = modifier) {
        when (state) {
            is CollectionUiState.Collection -> CollectionList(modifier = modifier, list = (state as CollectionUiState.Collection).items)
        }
    }
}

@Composable
fun CollectionList(
    modifier: Modifier,
    list: List<CollectionItem>
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = list,
            key = {item -> item.show?.id!!}
        ) {
            CollectionItem(
                modifier = modifier,
                item = it
            )
        }
    }
}

@Composable
fun CollectionItem(
    modifier: Modifier,
    item: CollectionItem
) {
    Row {
        AsyncImage(
            model = getThumbnailUrl(item.show?.poster),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.width(64.dp)
        )
        Text(text = item.show?.title ?: "Unknown title")
    }
}
