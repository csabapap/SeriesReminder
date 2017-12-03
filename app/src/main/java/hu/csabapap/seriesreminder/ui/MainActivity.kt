package hu.csabapap.seriesreminder.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.ui.adapters.TrendingShowsAdapter
import hu.csabapap.seriesreminder.ui.add_show.AddShowActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(), TrendingShowsAdapter.TrendingShowListener {

    val TAG : String = "MainActivity"

    val layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    private val trendingShowsAdapter = TrendingShowsAdapter()
    private val popularShowsAdapter = TrendingShowsAdapter()
    @Inject lateinit var showsRepository : ShowsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.title = "Series Reminder"

        trendingShowsAdapter.listener = this
        val itemDecorator = DividerItemDecoration(this, layoutManager.orientation)
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.horizontal_divider))
        shows_grid.layoutManager = layoutManager
        shows_grid.adapter = trendingShowsAdapter
        shows_grid.addItemDecoration(itemDecorator)

        popular_shows_grid.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        popular_shows_grid.adapter = popularShowsAdapter
        popular_shows_grid.addItemDecoration(itemDecorator)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(shows_grid)

        trendingShows()
        popularShows()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun trendingShows() {
        trending_shows_progress_bar.visibility = View.VISIBLE
        showsRepository.getTrendingShows()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ trendingShows ->
                    Log.d(TAG, "onNext() called")
                    trendingShowsAdapter.shows = trendingShows },
                        { t: Throwable ->
                            Log.e(TAG, t.message, t)
                            trending_shows_progress_bar.visibility = View.GONE
                        },
                        {trending_shows_progress_bar.visibility = View.GONE})
    }

    private fun popularShows() {
        showsRepository.popularShows()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    Log.d(TAG, "nmb of popular shows: ${it.size}")
                    popularShowsAdapter.shows = it
                    popular_shows_progress_bar.visibility = View.GONE
                },
                        {t: Throwable? -> Log.e(TAG, t?.message, t) })
    }

    override fun onItemClick(show: SRShow) {
        val intent = Intent(this, AddShowActivity::class.java)
        intent.putExtra("show_id", show.traktId)
        startActivity(intent)
    }
}
