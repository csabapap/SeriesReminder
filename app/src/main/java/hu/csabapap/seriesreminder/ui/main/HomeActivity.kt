package hu.csabapap.seriesreminder.ui.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.ui.main.discover.DiscoverFragment
import hu.csabapap.seriesreminder.ui.main.home.HomeFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : DaggerAppCompatActivity(), HomeFragment.HomeFragmentListener {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        changeFragment(item.itemId)
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar.let {
            it.title = "Series Reminder"
        }
        setSupportActionBar(toolbar)

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
            R.id.navigation_dashboard -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, BlankFragment())
                        .commit()
            }
            R.id.navigation_notifications -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, BlankFragment())
                        .commit()
            }
        }
    }

    override fun onMoreTrendingClick() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, DiscoverFragment.newInstance(DiscoverFragment.TYPE_TRENDING))
                .addToBackStack("main")
                .commit()
    }

    override fun onMorePopularClick() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, DiscoverFragment.newInstance(DiscoverFragment.TYPE_POPLAR))
                .addToBackStack("main")
                .commit()
    }
}
