package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeItem
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.extensions.diffInDays
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
import kotlinx.android.synthetic.main.item_episode_card.view.*

class EpisodeCardsAdapter:  RecyclerView.Adapter<EpisodeCardsAdapter.CardVH>() {

    lateinit var context: Context
    var episodes: List<NextEpisodeItem> = emptyList()

    fun updateItems(newItems: List<NextEpisodeItem>) {
        val diffResult = DiffUtil.calculateDiff(PreviewDiffs(newItems, episodes))
        episodes = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardVH {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_episode_card,
                parent, false)
        return CardVH(itemView)
    }

    override fun getItemCount() = episodes.size


    override fun onBindViewHolder(holder: CardVH, position: Int) {
        holder.bind(episodes[position])
    }


    inner class CardVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(nextEpisode: NextEpisodeItem) {
            val episode = nextEpisode.episode!!
            itemView.title.text = episode.title
            val episodeInfo = String.format(context.getString(R.string.episode_number),
                    episode.season,
                    episode.number)
            itemView.episode_info.text = episodeInfo
            itemView.airs_in_text.text = "in ${episode.firstAired?.diffInDays().toString()} days"
            val thumb = nextEpisode.show?.posterThumb
            thumb?.let {
                itemView.episode_image.loadFromTmdbUrl(it)
            }
        }
    }

    inner class PreviewDiffs(private val newItems: List<NextEpisodeItem>,
                             private val oldItems: List<NextEpisodeItem>)
        : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return newItem.episode?.traktId == oldItem.episode?.traktId
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
