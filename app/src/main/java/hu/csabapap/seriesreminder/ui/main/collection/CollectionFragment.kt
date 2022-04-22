package hu.csabapap.seriesreminder.ui.main.collection

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.ui.adapters.CollectionAdapter
import hu.csabapap.seriesreminder.utils.Collectible
import kotlinx.android.synthetic.main.fragment_collection.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class CollectionFragment : DaggerFragment(), CollectionAdapter.CollectionItemClickListener {

    @field:[Inject Named("Main")]
    lateinit var mainViewModelProvider: ViewModelProvider.Factory
    private lateinit var collectionViewModel: CollectionViewModel

    private var mListener: CollectionItemClickListener? = null
    private val adapter = CollectionAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectionViewModel = ViewModelProviders.of(this, mainViewModelProvider)
                .get(CollectionViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collection_toolbar.setTitle(R.string.title_my_shows)

        collection_toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        adapter.listener = this
        rv_collection.layoutManager = LinearLayoutManager(activity)
        rv_collection.adapter = adapter

        collectionViewModel.collectionsLiveData.observe(viewLifecycleOwner, Observer {
            it?.apply {
                Timber.d("nmb of shows in collection: ${it.size}")
                adapter.submitList(it)
            }
        })
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
