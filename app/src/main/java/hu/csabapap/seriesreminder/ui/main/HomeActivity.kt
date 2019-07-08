package hu.csabapap.seriesreminder.ui.main

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.ui.main.collection.CollectionFragment
import hu.csabapap.seriesreminder.ui.main.discover.DiscoverFragment
import hu.csabapap.seriesreminder.ui.main.home.HomeFragment
import timber.log.Timber

class HomeActivity : DaggerAppCompatActivity(), HomeFragment.HomeFragmentListener,
        DiscoverFragment.DiscoverFragmentInteractionListener,
        CollectionFragment.CollectionItemClickListener
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportFragmentManager.beginTransaction()
                .replace(R.id.content, HomeFragment())
                .commit()
    }

    override fun onMoreButtonClick(type: Int) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, DiscoverFragment.newInstance(type))
                .addToBackStack("main")
                .commit()
    }

    override fun onNavigateBack() {
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStackImmediate()
    }

    override fun onCollectionItemClick(show: SRShow) {
        Timber.d("item clicked")
    }
}
