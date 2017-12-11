package hu.csabapap.seriesreminder.data

import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingShow
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber

class ShowsRepository(val traktApi: TraktApi, val tvdbApi: TvdbApi, val showDao: SRShowDao){

    var cachedTrendingShows: MutableList<SRShow> = mutableListOf()
    var cachedPopularShows: MutableList<SRShow> = mutableListOf()

    fun getTrendingShows() : Single<List<SRTrendingShow>> {
        return getRemoteTrendingShows()
    }

    private fun getRemoteTrendingShows(): Single<List<SRTrendingShow>> {
        return traktApi.trendingShows("full")
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe {
                    getShow(it.show.ids.trakt, it.show)
                            .map { srShow -> mapToSRTrendingShow(srShow.id!!, it.watchers) }
                }
                .toList()
                .doOnSuccess { saveTrendingShows(it) }
    }

    fun popularShows() : Flowable<List<SRShow>> {
        if(!cachedPopularShows.isEmpty()) {
            Timber.d("return popular shows from cache")
            return Flowable.just(cachedPopularShows)
        }
        return traktApi.popularShows()
                .flatMap({ Flowable.fromIterable(it) })
                .flatMap {
                    Flowable.just(mapToSRShow(it))
                }
                .flatMap { it ->
                    tvdbApi.images(it.tvdbId)
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
                                it.apply {
                                    updateProperty(this::poster, popularImage?.fileName!!)
                                    updateProperty(this::posterThumb, popularImage.thumbnail)
                                }
                                Flowable.just(it)
                            }
                }
                .doOnNext({
                    cachedPopularShows.add(it)
                })
                .toList()
                .doOnSuccess({showDao.insertAllShows(it)})
                .toFlowable()
    }

    fun getShow(traktId: Int, show: BaseShow?) : Maybe<SRShow> {
        val showFromDb = showDao.getShow(traktId)

        val fromShow = show?.let { Maybe.just(mapToSRShow(show)) } ?: Maybe.empty<SRShow>()

        val fromWeb = getShowFromWeb(traktId).singleElement()
                .flatMap({
                    Maybe.just(mapToSRShow(it))
                })

        return Maybe.concat(showFromDb, fromShow, fromWeb).firstElement()
    }

    fun getShowFromWeb(traktId: Int) : Flowable<Show>{
        return traktApi.show(traktId)
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
        }
        return srShow
    }

    private fun mapToSRTrendingShow(showId: Long, watchers: Int) : SRTrendingShow {
        return SRTrendingShow(null, showId, watchers)
    }

    private fun saveTrendingShows(trendingShows: List<SRTrendingShow>) {

    }
}