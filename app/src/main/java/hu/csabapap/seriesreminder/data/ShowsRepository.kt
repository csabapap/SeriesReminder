package hu.csabapap.seriesreminder.data

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.db.PopularShowsResult
import hu.csabapap.seriesreminder.data.db.TrendingShowsResult
import hu.csabapap.seriesreminder.data.db.daos.PopularDao
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
import hu.csabapap.seriesreminder.data.db.entities.*
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.NextEpisode
import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.data.states.NextEpisodeError
import hu.csabapap.seriesreminder.data.states.NextEpisodeState
import hu.csabapap.seriesreminder.data.states.NextEpisodeSuccess
import hu.csabapap.seriesreminder.data.states.NoNextEpisode
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.threeten.bp.OffsetDateTime
import timber.log.Timber

class ShowsRepository(private val traktApi: TraktApi, private val tvdbApi: TvdbApi,
                      private val showDao: SRShowDao, private val trendingDao: TrendingDao,
                      private val popularDao: PopularDao,
                      private val seasonsRepository: SeasonsRepository,
                      private val episodesRepository: EpisodesRepository,
                      private val collectionRepository: CollectionRepository){

    var cachedTrendingShows: MutableList<SRTrendingItem> = mutableListOf()
    var cachedPopularShows: MutableList<SRPopularItem> = mutableListOf()

    fun getTrendingShows(limit: Int = 10) : Flowable<List<TrendingGridItem>> {
        return trendingDao.getTrendingShows(limit)
    }

    fun getLiveTrendingShows(limit: Int = 10): LiveData<List<TrendingGridItem>> {
        return trendingDao.getLiveTrendingShows(limit)
    }

    fun getTrendingShowsLiveData(): TrendingShowsResult {
        Timber.d("getTrendingShowsLiveData")
        val dataSourceFactory = trendingDao.getTrendingShowsFactory()
        val data: LiveData<PagedList<TrendingGridItem>> =
                LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                        .build()
        return TrendingShowsResult(data)
    }

    fun getPopularShowsLiveData(): PopularShowsResult {
        val dataSourceFactory = popularDao.getPopularShowsLiveFactory()
        val data: LiveData<PagedList<PopularGridItem>> =
                LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE).build()
        return PopularShowsResult(data)
    }

    fun getRemoteTrendingShows(): Single<List<SRTrendingItem>> {
        if (cachedTrendingShows.isEmpty().not()) {
            return Single.just(cachedTrendingShows.toList())
        }
        return traktApi.trendingShows("full")
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe {
                    getShow(it.show.ids.trakt)
                            .map { showDao.insertOrUpdateShow(it) }
                            .map { showDao.getShow(it.traktId)}
                            .map { srShow -> mapToSRTrendingShow(srShow.traktId, it.watchers) }
                }
                .toList()
                .doOnSuccess {
                    trendingDao.deleteAll()
                    saveTrendingShows(it)
                    cachedTrendingShows = it
                }
    }

    fun popularShows(limit: Int = 10) : Flowable<List<PopularGridItem>> {
        return popularDao.getPopularShows(limit)
    }

    fun getPopularShowsFromWeb(limit: Int = 20) : Single<List<SRPopularItem>> {
        if (cachedPopularShows.isEmpty().not()) {
            return Single.just(cachedPopularShows.toList())
        }
        return traktApi.popularShows(limit)
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe {
                    getShow(it.ids.trakt)
                            .map { showDao.insertOrUpdateShow(it) }
                            .map { showDao.getShow(it.traktId)}
                            .map { srShow -> mapToSRPopularItem(srShow.traktId) }
                }
                .toList()
                .doOnSuccess {
                    popularDao.deleteAll()
                    savePopularItem(it)
                    cachedPopularShows = it
                }
    }

    fun getShow(traktId: Int) : Maybe<SRShow> {
        val showFromDb = showDao.getShowMaybe(traktId)

        val fromWeb = getShowFromWeb(traktId).singleElement()

        return Maybe.concat(showFromDb, fromWeb).firstElement()
    }

    fun insertShow(show: SRShow) {
        showDao.insertOrUpdateShow(show)
    }

    private fun getShowFromWeb(traktId: Int) : Flowable<SRShow>{
        return traktApi.show(traktId)
                .flatMap {
                    Flowable.just(mapToSRShow(it))
                }
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
            updateProperty(this::genres, show.genres.joinToString())
            updateProperty(this::runtime, show.runtime)
            updateProperty(this::airedEpisodes, show.aired_episodes)
            updateProperty(this::status, show.status)
            updateProperty(this::network, show.network)
            updateProperty(this::trailer, show.trailer ?: "")
            updateProperty(this::homepage, show.homepage ?: "")
            updateProperty(this::updatedAt, OffsetDateTime.parse(show.updated_at))
            show.airs?.let {
                updateProperty(this::airingTime, AiringTime(it.day,
                        it.time,it.timezone))
            }
        }
        return srShow
    }

    private fun mapToSRTrendingShow(showId: Int, watchers: Int) : SRTrendingItem =
            SRTrendingItem(null, showId, watchers)

    private fun mapToSRPopularItem(showId: Int) : SRPopularItem = SRPopularItem(showId = showId)


    private fun saveTrendingShows(trendingItems: List<SRTrendingItem>) {
        trendingItems.forEach { trendingShow ->
            trendingDao.insert(trendingShow)
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

    fun fetchNextEpisode(showId: Int): Single<NextEpisodeState> {
        return traktApi.nextEpisode(showId)
                .flatMap {
                    when (it.code()) {
                        200 -> Single.just(NextEpisodeSuccess(mapToNextEpisodeEntry(it.body()!!, showId)))
                        204 -> Single.just(NoNextEpisode)
                        else -> Single.just(NextEpisodeError("error during next episode fetching"))
                    }
                }
                .doAfterSuccess({
                    if (it is NextEpisodeSuccess) {
                        saveNextEpisode(it.nextEpisode)
                    }
                })
    }

    private fun mapToNextEpisodeEntry(nextEpisode: NextEpisode, showId: Int): NextEpisodeEntry {
        return NextEpisodeEntry(null,
                nextEpisode.season,
                nextEpisode.number,
                nextEpisode.title,
                nextEpisode.ids.trakt,
                nextEpisode.ids.tvdb,
                showId)
    }

    private fun saveNextEpisode(nextEpisodeEntry: NextEpisodeEntry) {
        episodesRepository.insertNextEpisode(nextEpisodeEntry)
    }

    fun getSeasons(showId: Int): Single<List<SRSeason>> {
        return seasonsRepository.getSeasons(showId)
    }

    fun syncShows(): Single<MutableList<SRShow>> {
        return collectionRepository.getCollectionsSingle()
                .flattenAsFlowable { it }
                .filter { it.show != null }
                .flatMap { Flowable.just(it.show) }
                .flatMap {
                    traktApi.show(it.traktId)
                            .flatMap { show -> Flowable.just(mapToSRShow(show)) }
                }
                .flatMap {
                    Timber.d("update show: %s", it.title)
                    showDao.updateShow(it)
                    Flowable.just(it)
                }
                .toList()
    }

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }
}