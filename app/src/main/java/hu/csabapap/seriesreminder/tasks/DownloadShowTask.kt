package hu.csabapap.seriesreminder.tasks

import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.domain.AddShowToCollectionUseCase
import javax.inject.Inject

class DownloadShowTask(private val showId: Int): Task {

    @Inject
    lateinit var addShowToCollectionUseCase: AddShowToCollectionUseCase

    override suspend fun execute() {
        addShowToCollectionUseCase.addShow(showId)
    }

    fun setEpisodeAbsNumberIfNotExists(seasons: List<SRSeason>): List<SRSeason> {
        var absNumber = 0
        return seasons.sortedBy { season -> season.number }
                .map {season ->
                    if (season.number == 0) return@map season

                    season.episodes = season.episodes.sortedBy { episode -> episode.number }
                            .map episodeMap@{ episode ->
                                absNumber += 1
                                return@episodeMap if (episode.absNumber == 0) {
                                    episode.copy(absNumber = absNumber)
                                } else {
                                    episode
                                }
                            }
                    season
                }
    }
}