package hu.csabapap.seriesreminder.ui.main

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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
