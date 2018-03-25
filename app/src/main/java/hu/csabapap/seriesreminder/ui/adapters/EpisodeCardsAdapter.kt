package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
import kotlinx.android.synthetic.main.item_episode_card.view.*

class EpisodeCardsAdapter:  RecyclerView.Adapter<EpisodeCardsAdapter.CardVH>() {

    lateinit var context: Context
    lateinit var episodes: MutableList<SREpisode>

    fun updateItems(newItems: List<SREpisode>) {
        val diffResult = DiffUtil.calculateDiff(PreviewDiffs(newItems, newItems))
        episodes = newItems.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardVH {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_episode_card, parent, false)
        return CardVH(itemView)
    }

    override fun getItemCount() = episodes.size


    override fun onBindViewHolder(holder: CardVH, position: Int) {
        holder.bind(episodes[position])
    }


    inner class CardVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(episode: SREpisode) {
            itemView.title.text = episode.title
            val episodeInfo = String.format(context.getString(R.string.episode_number),
                    episode.season,
                    episode.number)
            itemView.episode_info.text = episodeInfo
            val imagePath = episode.image
            if (imagePath.isEmpty().not()) {
                itemView.episode_image.loadFromTmdbUrl(imagePath, R.color.item_background_dark)
            }
        }
    }

    inner class PreviewDiffs(private val newItems: List<SREpisode>,
                             private val oldItems: List<SREpisode>)
        : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return newItem.traktId == oldItem.traktId
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
