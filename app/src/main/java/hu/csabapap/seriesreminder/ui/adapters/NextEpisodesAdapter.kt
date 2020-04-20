package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRNextEpisode
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import kotlinx.android.synthetic.main.item_episode_card.view.episode_info
import kotlinx.android.synthetic.main.item_episode_card.view.show_poster
import kotlinx.android.synthetic.main.item_episode_card.view.title
import kotlinx.android.synthetic.main.item_next_episode.view.*
import timber.log.Timber

class NextEpisodesAdapter: RecyclerView.Adapter<NextEpisodesAdapter.NextEpisodeVH>() {

    interface NextEpisodeClickListener {
        fun onItemClick(nextEpisode: SRNextEpisode)
        fun onSetAsWatchedButtonClick(nextEpisode: SRNextEpisode)
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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_next_episode,
                parent, false)
        val nextEpisodeVH = NextEpisodeVH(itemView)
        nextEpisodeVH.itemView.setOnClickListener {
            val position = nextEpisodeVH.adapterPosition
            val nextEpisode = episodes.getOrNull(position)
            if (nextEpisode != null) {
                listener?.onItemClick(nextEpisode)
            }
        }
        nextEpisodeVH.itemView.set_watched.setOnClickListener {
            itemView.set_watched.isChecked = true
            val position = nextEpisodeVH.adapterPosition
            val nextEpisode = episodes.getOrNull(position)
            if (nextEpisode != null) {
                listener?.onSetAsWatchedButtonClick(nextEpisode)
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
            itemView.set_watched.isChecked = false
            itemView.show_title.text = nextEpisode.showTitle
            Picasso.with(context)
                    .load(getThumbnailUrl(nextEpisode.poster))
                    .into(itemView.show_poster, object: Callback {
                        override fun onSuccess() {
                            Timber.d("success")
                        }

                        override fun onError() {
                            Timber.d("error")
                        }
                    })
        }
    }
}