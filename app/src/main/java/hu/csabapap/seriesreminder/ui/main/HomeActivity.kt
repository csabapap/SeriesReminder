package hu.csabapap.seriesreminder.ui.main

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.ui.main.collection.CollectionFragment
import hu.csabapap.seriesreminder.ui.main.discover.DiscoverFragment
import hu.csabapap.seriesreminder.ui.main.home.HomeFragment
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber

class HomeActivity : DaggerAppCompatActivity(), HomeFragment.HomeFragmentListener,
        DiscoverFragment.DiscoverFragmentInteractionListener,
        CollectionFragment.CollectionItemClickListener
{

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        changeFragment(item.itemId)
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_home
    }

    private fun changeFragment(id: Int) {
        when (id) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, HomeFragment())
                        .commit()
            }
            R.id.navigation_my_shows -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, CollectionFragment())
                        .commit()
            }
            R.id.navigation_notifications -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, BlankFragment())
                        .commit()
            }
        }
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
