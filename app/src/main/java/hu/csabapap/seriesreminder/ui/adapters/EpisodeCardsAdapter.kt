package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.extensions.diffInDays
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
import hu.csabapap.seriesreminder.ui.views.EpisodeCardView
import kotlinx.android.synthetic.main.item_episode_card.view.*

class EpisodeCardsAdapter:  RecyclerView.Adapter<EpisodeCardsAdapter.CardVH>() {

    lateinit var context: Context
    var episodes: List<SREpisode> = emptyList()

    fun updateItems(newItems: List<SREpisode>) {
        val diffResult = DiffUtil.calculateDiff(PreviewDiffs(newItems, episodes))
        episodes = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardVH {
        context = parent.context
        val itemView = EpisodeCardView(context)
        itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        return CardVH(itemView)
    }

    override fun getItemCount() = episodes.size


    override fun onBindViewHolder(holder: CardVH, position: Int) {
        holder.bind(episodes[position])
    }


    inner class CardVH(itemView: EpisodeCardView): RecyclerView.ViewHolder(itemView) {
        fun bind(episode: SREpisode) {
            itemView as EpisodeCardView
            itemView.episodeTitle.text = episode.title
            val episodeInfo = String.format(context.getString(R.string.episode_number),
                    episode.season,
                    episode.number)
            itemView.episodeInfo.text = episodeInfo
            itemView.airsInInfo.text = "in ${episode.firstAired?.diffInDays().toString()} days"
            val imagePath = episode.image
            if (imagePath.isEmpty().not()) {
                itemView.image.loadFromTmdbUrl(imagePath, R.color.item_background_dark)
                itemView.image.visibility = View.VISIBLE
                itemView.placeholder.visibility = View.GONE
            } else {
                itemView.placeholder.visibility = View.VISIBLE
                itemView.image.visibility = View.GONE
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
