package hu.csabapap.seriesreminder.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.squareup.picasso.Picasso
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.network.entities.Show
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_show_details.*
import timber.log.Timber
import javax.inject.Inject

class ShowDetailsActivity : AppCompatActivity() {

    val TAG = "ShowDetailsActivity"

    lateinit var showsRepository: ShowsRepository
    var traktId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener { finish() }

        traktId = intent.extras.getInt("trakt_id")

        Timber.d("trakt id: $traktId")
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        if (traktId != -1) {
            loadShow()
        }
    }

    private fun loadShow() {
//        showsRepository.getShowFromWeb(traktId)
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext {
//                    displayData(it)
//                }
//                .observeOn(Schedulers.io())
//                .flatMap { it : Show ->
//                    showsRepository.images(it.ids.tvdb, "fanart")
//                            .flatMap { (data) ->
//                                it.cover = data[0].fileName
//                                Log.d(TAG, "cover: ${it.cover}")
//                                Flowable.just(it)
//                            }
//                }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ show ->
//                    Log.d(TAG, "show from trakt: $show")
//                    displayData(show)
//                },
//                        { Log.e(TAG, it.message, it) })
    }

    private fun displayData(show: Show) {
        show_title.text = show.title
        overview?.text = show.overview
//        Picasso.with(this)
//                .load(show.cover)
//                .into(cover)
    }
}
