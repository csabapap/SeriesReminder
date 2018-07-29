package hu.csabapap.seriesreminder.ui.main.discover

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.ui.adapters.GridAdapter
import hu.csabapap.seriesreminder.ui.addshow.AddShowActivity
import hu.csabapap.seriesreminder.utils.SpacingItemDecorator
import kotlinx.android.synthetic.main.fragment_discover.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class DiscoverFragment : DaggerFragment(), GridAdapter.GridItemClickListener {

    @field:[Inject Named("Main")]
    lateinit var mainViewModelProvider: ViewModelProvider.Factory
    private lateinit var discoverViewModel: DiscoverViewModel

    private var listType: Int? = null

    private var listener: DiscoverFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        discoverViewModel = ViewModelProviders.of(this, mainViewModelProvider)
                .get(DiscoverViewModel::class.java)

        if (arguments != null) {
            listType = arguments!!.getInt(ARG_DISCOVER_TYPE)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is DiscoverFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException((context!!.toString() + " must implement DiscoverFragmentInteractionListener"))
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                     savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        discover_toolbar.apply {
            title = setToolbarTitle()
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { listener?.onNavigateBack() }
        }

        val adapter = GridAdapter()
        val gridLayoutManager = GridLayoutManager(activity, 4) as RecyclerView.LayoutManager
        rv_grid.layoutManager = gridLayoutManager
        rv_grid.addItemDecoration(SpacingItemDecorator(4, 4))
        rv_grid.adapter = adapter
        adapter.listener = this

        discoverViewModel.trendingShows.observe(this, Observer {
            Timber.d("observe")
            Timber.d("null? ${it == null}")
            it?.apply {
                Timber.d("submit list")
                adapter.submitList(it)
            }
        })

        discoverViewModel.collectionLiveData.observe(this, Observer {
            it?.apply {
                Timber.d("nmb of shows in collection: ${it.size}")
            }
        })
    }

    override fun onStart() {
        super.onStart()
//        discoverViewModel.getItems(listType!!)
        discoverViewModel.loadTrendingShows()

    }

    private fun setToolbarTitle() =
            when (listType) {
                TYPE_TRENDING -> "Trending Shows"
                TYPE_POPULAR -> "Popular Shows"
                else -> ""
            }

    override fun onItemClick(traktId: Int) {
        val intent = Intent(activity, AddShowActivity::class.java)
        intent.putExtra("show_id", traktId)
        activity?.startActivity(intent)
    }

    interface DiscoverFragmentInteractionListener {
        fun onNavigateBack()
    }

    companion object {

        const val TYPE_TRENDING = 1
        const val TYPE_POPULAR = 2

        private const val ARG_DISCOVER_TYPE = "discover_type"

        fun newInstance(type: Int): DiscoverFragment {
            val fragment = DiscoverFragment()
            val args = Bundle()
            args.putInt(ARG_DISCOVER_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }
}
