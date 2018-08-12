package hu.csabapap.seriesreminder.ui.main.discover

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.ui.adapters.GridAdapter
import hu.csabapap.seriesreminder.ui.addshow.AddShowActivity
import kotlinx.android.synthetic.main.fragment_discover.*

class DiscoverFragment : Fragment(), GridAdapter.GridItemClickListener {

    private var listener: DiscoverFragmentInteractionListener? = null

    private var type = TYPE_TRENDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            type = getInt(ARG_DISCOVER_TYPE, TYPE_TRENDING)
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
            title = "Discover"
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { listener?.onNavigateBack() }
        }

        tab_layout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

        })
        tab_layout.setupWithViewPager(view_pager)
        view_pager.adapter = DiscoverPagerAdapter(childFragmentManager)

        if (type == TYPE_POPULAR) {
            view_pager.currentItem = 1
        }

    }

    override fun onStart() {
        super.onStart()
//        discoverViewModel.getItems(listType!!)
//        discoverViewModel.loadTrendingShows()

    }

    override fun onItemClick(traktId: Int) {
        val intent = Intent(activity, AddShowActivity::class.java)
        intent.putExtra("show_id", traktId)
        activity?.startActivity(intent)
    }

    interface DiscoverFragmentInteractionListener {
        fun onNavigateBack()
    }

    class DiscoverPagerAdapter(fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GridFragment.newInstance(GridFragment.TYPE_TRENDING)
                1 -> GridFragment.newInstance(GridFragment.TYPE_POPULAR)
                else -> throw IllegalArgumentException("invalid view pager position")
            }
        }

        override fun getCount() = 2

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Trending"
                1 -> "Popular"
                else -> ""
            }
        }
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
