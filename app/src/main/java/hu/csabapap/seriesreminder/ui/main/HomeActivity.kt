package hu.csabapap.seriesreminder.ui.main

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.ui.adapters.items.CardItem
import hu.csabapap.seriesreminder.ui.main.collection.CollectionFragment
import hu.csabapap.seriesreminder.ui.main.home.HomeFragment
import hu.csabapap.seriesreminder.ui.search.SearchFragment
import timber.log.Timber

class HomeActivity : DaggerAppCompatActivity(), HomeFragment.HomeFragmentListener,
        CollectionFragment.CollectionItemClickListener
{

    lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }

    override fun onMoreButtonClick(type: Int) {
        if (type == CardItem.MY_SHOWS_TYPE) {
//            startActivity(Intent(this, CollecAc))
        }
        val args = bundleOf(SearchFragment.ARG_DISCOVER_TYPE to type)
        findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_menu_search, args)
        navView.menu.findItem(R.id.navigation_menu_search).isChecked = true
    }

    override fun onCollectionItemClick(show: SRShow) {
        Timber.d("item clicked")
    }
}
