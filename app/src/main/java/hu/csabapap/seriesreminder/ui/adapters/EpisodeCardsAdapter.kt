package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow
import hu.csabapap.seriesreminder.extensions.diffInDays
import hu.csabapap.seriesreminder.extensions.diffInHours
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
import hu.csabapap.seriesreminder.utils.getAirDateTimeInCurrentTimeZone
import kotlinx.android.synthetic.main.item_episode_card.view.*
import org.threeten.bp.LocalDateTime

class EpisodeCardsAdapter: RecyclerView.Adapter<EpisodeCardsAdapter.CardVH>() {

    interface EpisodeClickListener {
        fun onItemClick(nextEpisode: EpisodeWithShow)
    }

    lateinit var context: Context
    lateinit var listener: EpisodeClickListener
    var episodes: List<EpisodeWithShow> = emptyList()

    fun updateItems(newItems: List<EpisodeWithShow>) {
        val diffResult = DiffUtil.calculateDiff(PreviewDiffs(newItems, episodes))
        episodes = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardVH {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_episode_card,
                parent, false)
        val cardVH = CardVH(itemView)
        cardVH.itemView.setOnClickListener {
            val position = cardVH.adapterPosition
            val nextEpisode = episodes.getOrNull(position)
            if (nextEpisode != null) {
                listener.onItemClick(nextEpisode)
            }
        }
        return cardVH
    }

    override fun getItemCount() = episodes.size


    override fun onBindViewHolder(holder: CardVH, position: Int) {
        holder.bind(episodes[position])
    }


    inner class CardVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(nextEpisode: EpisodeWithShow) {
            val episode = nextEpisode.episode
            val show = nextEpisode.show ?: return
            itemView.show_title.text = show.title
            itemView.title.text = episode.title
            if (episode.title.trim().isEmpty()) {
                itemView.title.text = show.title
            }
            val episodeInfo = String.format(context.getString(R.string.episode_number),
                    episode.season,
                    episode.number)
            itemView.episode_info.text = episodeInfo
            show.apply {
                val nextAirDateTime = getAirDateTimeInCurrentTimeZone(LocalDateTime.now(),airingTime)
                        .toOffsetDateTime()
                val diffInDays = nextAirDateTime.diffInDays()
                if (diffInDays > 0) {
                    itemView.airs_in_text.text = "in $diffInDays days"
                } else {
                    val diffInHours = nextAirDateTime.diffInHours()
                    if (diffInHours > 0) {
                        itemView.airs_in_text.text = "in $diffInHours hours"
                    } else {
                        itemView.airs_in_text.text = "in less then an hour"
                    }
                }
                itemView.show_poster.loadFromTmdbUrl(tvdbId)
            }
        }
    }

    inner class PreviewDiffs(private val newItems: List<EpisodeWithShow>,
                             private val oldItems: List<EpisodeWithShow>)
        : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return newItem.episode.traktId == oldItem.episode.traktId
        }

        override fun getOldListSize() = oldItems.size


        override fun getNewListSize() = newItems.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return oldItem == newItem
        }

    }
}
