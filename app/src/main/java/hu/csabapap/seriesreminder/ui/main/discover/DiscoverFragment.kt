package hu.csabapap.seriesreminder.ui.main.discover

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import kotlinx.android.synthetic.main.fragment_discover.*
import javax.inject.Inject

class DiscoverFragment : DaggerFragment() {

    @Inject
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
    }

    private fun setToolbarTitle() =
            when (listType) {
                TYPE_TRENDING -> "Trending Shows"
                TYPE_POPLAR -> "Popular Shows"
                else -> ""
            }

    interface DiscoverFragmentInteractionListener {
        fun onNavigateBack()
    }

    companion object {

        val TYPE_TRENDING = 1
        val TYPE_POPLAR = 2

        private val ARG_DISCOVER_TYPE = "discover_type"

        fun newInstance(type: Int): DiscoverFragment {
            val fragment = DiscoverFragment()
            val args = Bundle()
            args.putInt(ARG_DISCOVER_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
