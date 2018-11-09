package hu.csabapap.seriesreminder.ui.adapters.items

import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeItem
import hu.csabapap.seriesreminder.data.db.entities.SREpisode


open class CardItem(val type: Int, private val priority: Int) : Comparable<CardItem> {
    override fun compareTo(other: CardItem): Int {
        if (priority < other.priority) return -1
        if (priority > other.priority) return 1
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CardItem

        if (type != other.type) return false
        if (priority != other.priority) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + priority
        return result
    }


    companion object {
        const val TRENDING_CARD_TYPE = 0
        const val POPULAR_CARD_TYPE = 1
        const val UPCOMING_EPISODE_TYPE = 2
        const val MY_SHOWS_TYPE = 3

        const val PRIORITY_POPULAR = 0
        const val PRIORITY_TRENDING = 1
        const val PRIORITY_MEDIUM = 2
        const val PRIORITY_HIGH = 3
    }
}

class DiscoverCardItem(
        val title: String,
        val showItems: List<ShowItem>,
        type: Int,
        priority: Int)
    : CardItem(type, priority)

class UpcomingEpisodeCardItem(
        val episodes: List<NextEpisodeItem>,
        type: Int,
        priority: Int = CardItem.PRIORITY_HIGH)
    : CardItem(type, priority)

enum class CardType{
    TRENDING, POPULAR, UPCOMING_EPISODES, MY_SHOWS
}