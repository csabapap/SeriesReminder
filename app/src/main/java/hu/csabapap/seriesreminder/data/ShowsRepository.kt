package hu.csabapap.seriesreminder.data

import hu.csabapap.seriesreminder.data.db.daos.PopularDao
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
import hu.csabapap.seriesreminder.data.db.entities.*
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.BaseShow
import hu.csabapap.seriesreminder.data.network.entities.Images
import hu.csabapap.seriesreminder.data.network.entities.Show
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber

class ShowsRepository(val traktApi: TraktApi, val tvdbApi: TvdbApi,
                      val showDao: SRShowDao, val trendingDao: TrendingDao,
                      val popularDao: PopularDao){

    var cachedTrendingShows: MutableList<SRShow> = mutableListOf()
    var cachedPopularShows: MutableList<SRShow> = mutableListOf()

    fun getTrendingShows(limit: Int = 10) : Flowable<List<TrendingGridItem>> {
        Timber.tag("DBCheck").d("get trending shows from db")
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
                .doOnSuccess {
                    trendingDao.deleteAll()
                    saveTrendingShows(it)
                }
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
                .doOnSuccess {
                    popularDao.deleteAll()
                    savePopularItem(it)
                }

    }

    fun getShow(traktId: Int, show: BaseShow? = null) : Maybe<SRShow> {
        val showFromDb = showDao.getShowMaybe(traktId)

//        val fromShow = show?.let { Maybe.just(mapToSRShow(show)) } ?: Maybe.empty<SRShow>()

        val fromWeb = getShowFromWeb(traktId).singleElement()

        return Maybe.concat(showFromDb, fromWeb).firstElement()
    }

    fun getShowFromWeb(traktId: Int) : Flowable<SRShow>{
        return traktApi.show(traktId)
                .flatMap({
                    Flowable.just(mapToSRShow(it))
                })
    }

    fun images(tvdbId : Int, type: String = "poster") :Flowable<Images>{
        return tvdbApi.images(tvdbId, type)
    }

    fun updateShowWithImages(show: SRShow) : Maybe<SRShow> {
        return tvdbApi.images(show.tvdbId)
                .flatMap { (data) ->
                    val popularImage = data.maxBy { it.ratings.average }
                    show.apply {
                        poster = popularImage?.fileName!!
                        posterThumb = popularImage.thumbnail
                    }
                    Flowable.just(show)
                }
                .singleElement()
    }

    private fun mapToSRShow(show : Show) : SRShow {
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
            updateProperty(this::genres, show.genres.joinToString())
        }
        return srShow
    }

    private fun mapToSRTrendingShow(showId: Int, watchers: Int) : SRTrendingItem =
            SRTrendingItem(null, showId, watchers)

    private fun mapToSRPopularItem(showId: Int) : SRPopularItem = SRPopularItem(showId = showId)


    private fun saveTrendingShows(trendingItems: List<SRTrendingItem>) {
        trendingItems.forEach { trendingShow ->
            trendingDao.insert(trendingShow)
            Timber.tag("DBCheck").d("inserted")
        }
    }

    private fun savePopularItem(popularItems: List<SRPopularItem>) {
        popularItems.forEach {
            popularDao.insert(it)
        }
    }

    fun updateShow(show: SRShow) {
        showDao.updateShow(show)
    }
}