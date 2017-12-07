package hu.csabapap.seriesreminder.data

import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Image
import hu.csabapap.seriesreminder.data.network.entities.Images
import hu.csabapap.seriesreminder.data.network.entities.Show
import io.reactivex.Flowable
import timber.log.Timber

class ShowsRepository(val traktApi: TraktApi, val tvdbApi: TvdbApi, val showDao: SRShowDao){

    var cachedTrendingShows: MutableList<SRShow> = mutableListOf()
    var cachedPopularShows: MutableList<SRShow> = mutableListOf()
    var cachedShows: MutableMap<Int, SRShow> = mutableMapOf()

    fun getTrendingShows() : Flowable<List<SRShow>> {
        if(!cachedTrendingShows.isEmpty()){
            Timber.d("return trending shows from cache")
            return Flowable.just(cachedTrendingShows)
        }
        return getRemoteTrendingShows()
    }

    private fun getRemoteTrendingShows(): Flowable<List<SRShow>> {
        return traktApi.trendingShows("full")
                .flatMap({ Flowable.fromIterable(it) })
                .flatMap {
                    val srShow = SRShow()
                    srShow.apply {
                        updateProperty(this::traktId, it.show.ids.trakt)
                        updateProperty(this::tvdbId, it.show.ids.tvdb)
                        updateProperty(this::title, it.show.title)
                        updateProperty(this::overview, it.show.overview)
                        updateProperty(this::rating, it.show.rating)
                        updateProperty(this::votes, it.show.votes)
                    }
                    Flowable.just(srShow)
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
                                    updateProperty(this::posterThumb, popularImage?.thumbnail!!)
                                }
                                Flowable.just(it)
                            }
                }
                .doOnNext({
                    cachedTrendingShows.add(it)
                })
                .toList()
                .doOnSuccess {
                    showDao.insertAllShows(it)
                }
                .toFlowable()
    }

    fun popularShows() : Flowable<List<SRShow>> {
        if(!cachedPopularShows.isEmpty()) {
            Timber.d("return popular shows from cache")
            return Flowable.just(cachedPopularShows)
        }
        return traktApi.popularShows()
                .flatMap({ Flowable.fromIterable(it) })
                .flatMap {
                    val srShow = SRShow()
                    srShow.apply {
                        updateProperty(this::traktId, it.ids.trakt)
                        updateProperty(this::tvdbId, it.ids.tvdb)
                        updateProperty(this::title, it.title)
                        updateProperty(this::overview, it.overview)
                        updateProperty(this::rating, it.rating)
                        updateProperty(this::votes, it.votes)
                    }
                    Flowable.just(srShow)
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
                                    updateProperty(this::posterThumb, popularImage?.thumbnail!!)
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

    fun getShow(traktId: Int) : Flowable<SRShow> {
        if (cachedShows.contains(traktId)) {
            return Flowable.just(cachedShows[traktId])
        }

        return getShowFromWeb(traktId)
                .flatMap({
                    val srShow = SRShow()
                    srShow.apply {
                        updateProperty(this::traktId, it.ids.trakt)
                        updateProperty(this::tvdbId, it.ids.tvdb)
                        updateProperty(this::title, it.title)
                        updateProperty(this::overview, it.overview)
                        updateProperty(this::rating, it.rating)
                        updateProperty(this::votes, it.votes)
                    }
                    Flowable.just(srShow)
                })
    }

    fun getShowFromWeb(traktId: Int) : Flowable<Show>{
        return traktApi.show(traktId)
    }

    fun images(tvdbId : Int, type: String = "poster") :Flowable<Images>{
        return tvdbApi.images(tvdbId, type)
    }
}