package hu.csabapap.seriesreminder.data

import hu.csabapap.seriesreminder.data.db.daos.PopularDao
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
import hu.csabapap.seriesreminder.data.db.entities.*
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber
import java.util.*

class ShowsRepository(val traktApi: TraktApi, val tvdbApi: TvdbApi,
                      val showDao: SRShowDao, val trendingDao: TrendingDao,
                      val popularDao: PopularDao){

    var cachedTrendingShows: MutableList<SRShow> = mutableListOf()
    var cachedPopularShows: MutableList<SRShow> = mutableListOf()

    fun getTrendingShows(limit: Int = 10) : Flowable<List<TrendingGridItem>> {
        return trendingDao.getTrendingShows(limit)
    }

    fun getRemoteTrendingShows(): Single<List<SRTrendingItem>> {
        return traktApi.trendingShows("full")
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe {
                    getShow(it.show.ids.trakt, it.show)
                            .map { showDao.insertOrUpdateShow(it) }
                            .map { showDao.getShow(it.traktId)}
                            .map { srShow -> mapToSRTrendingShow(srShow.traktId, it.watchers) }
                }
                .toList()
                .doOnSuccess { saveTrendingShows(it) }
    }

    fun popularShows(limit: Int = 10) : Flowable<List<PopularGridItem>> {
        return popularDao.getPopularShows(limit)
    }

    fun getPopularShowsFromWeb(limit: Int = 20) : Single<List<SRPopularItem>> {
        return traktApi.popularShows(limit)
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe {
                    getShow(it.ids.trakt, it)
                            .map { showDao.insertOrUpdateShow(it) }
                            .map { showDao.getShow(it.traktId)}
                            .map { srShow -> mapToSRPopularItem(srShow.traktId) }
                }
                .toList()
                .doOnSuccess { savePopularItem(it) }

    }

    fun getShow(traktId: Int, show: BaseShow? = null) : Maybe<SRShow> {
        val showFromDb = showDao.getShowMaybe(traktId)

//        val fromShow = show?.let { Maybe.just(mapToSRShow(show)) } ?: Maybe.empty<SRShow>()

        val fromWeb = getShowFromWeb(traktId).singleElement()
                .flatMap({
                    Maybe.just(mapToSRShow(it))
                })

        return Maybe.concat(showFromDb, fromWeb).firstElement()
    }

    fun getShowFromWeb(traktId: Int) : Flowable<Show>{
        return traktApi.show(traktId)
                .flatMap {
                    show ->tvdbApi.images(show.ids.tvdb)
                            .flatMap { (data) ->
                                var popularImage : Image? = null
                                for (image in data) {
                                    if (popularImage == null) {
                                        popularImage = image
                                        continue
                                    }

                                    if (image.ratings.average > popularImage.ratings.average){
                                        popularImage = image
                                    }
                                }
                                show.apply {
                                    image = popularImage?.fileName!!
                                    thumb = popularImage.thumbnail
                                }
                                Flowable.just(show)
                            }
                }
    }

    fun images(tvdbId : Int, type: String = "poster") :Flowable<Images>{
        return tvdbApi.images(tvdbId, type)
    }

    private fun mapToSRShow(show : BaseShow) : SRShow {
        val srShow = SRShow()
        srShow.apply {
            updateProperty(this::traktId, show.ids.trakt)
            updateProperty(this::tvdbId, show.ids.tvdb)
            updateProperty(this::title, show.title)
            updateProperty(this::overview, show.overview)
            updateProperty(this::rating, show.rating)
            updateProperty(this::votes, show.votes)
            updateProperty(this::poster, show.image)
            updateProperty(this::posterThumb, show.thumb)
        }
        return srShow
    }

    private fun mapToSRTrendingShow(showId: Int, watchers: Int) : SRTrendingItem =
            SRTrendingItem(null, showId, watchers)

    private fun mapToSRPopularItem(showId: Int) : SRPopularItem = SRPopularItem(showId = showId)


    private fun saveTrendingShows(trendingItems: List<SRTrendingItem>) {
        Timber.d("insert trending shows")
        trendingItems.forEach { trendingShow ->
            trendingDao.insert(trendingShow)
        }
    }

    private fun savePopularItem(popularItems: List<SRPopularItem>) {
        popularItems.forEach {
            popularDao.insert(it)
        }
    }
}