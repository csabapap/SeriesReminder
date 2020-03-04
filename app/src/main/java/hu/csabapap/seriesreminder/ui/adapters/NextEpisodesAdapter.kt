package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeItem
import hu.csabapap.seriesreminder.data.db.entities.SRNextEpisode
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import kotlinx.android.synthetic.main.item_episode_card.view.*

class NextEpisodesAdapter: RecyclerView.Adapter<NextEpisodesAdapter.NextEpisodeVH>() {

    interface NextEpisodeClickListener {
        fun onItemClick(nextEpisode: SRNextEpisode)
        fun onSetAsWatchedButtonClick(nextEpisode: NextEpisodeItem)
    }

    lateinit var context: Context
    var listener: NextEpisodeClickListener? = null
    var episodes: MutableList<SRNextEpisode> = mutableListOf()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NextEpisodeVH {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_episode_card,
                parent, false)
        val nextEpisodeVH = NextEpisodeVH(itemView)
        nextEpisodeVH.itemView.setOnClickListener {
            val position = nextEpisodeVH.adapterPosition
            val nextEpisode = episodes.getOrNull(position)
            if (nextEpisode != null) {
                listener?.onItemClick(nextEpisode)
            }
        }
        return nextEpisodeVH
    }

    override fun getItemCount() = episodes.size

    override fun onBindViewHolder(holder: NextEpisodeVH, position: Int) {
        holder.bind(episodes[position])
    }

    inner class NextEpisodeVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(nextEpisode: SRNextEpisode) {
            itemView.title.text = nextEpisode.episodeTitle

            val episodeInfo = String.format(context.getString(R.string.episode_number),
                    nextEpisode.season,
                    nextEpisode.number)
            itemView.episode_info.text = episodeInfo
            Picasso.with(context)
                    .load(getThumbnailUrl(nextEpisode.poster))
                    .into(itemView.show_poster)
        }
    }
}