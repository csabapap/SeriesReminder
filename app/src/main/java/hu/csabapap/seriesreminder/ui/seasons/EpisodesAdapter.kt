package hu.csabapap.seriesreminder.ui.seasons

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.network.getEpisodeUrl
import hu.csabapap.seriesreminder.extensions.bindView
import hu.csabapap.seriesreminder.ui.widget.CheckableImageButton
import org.threeten.bp.OffsetDateTime

class EpisodesAdapter(val episodes: MutableList<EpisodeItem>): RecyclerView.Adapter<EpisodesAdapter.EpisodeVH>() {

    interface EpisodeItemClickListener {
        fun onItemClick(episode: SREpisode)
        fun setEpisodeAsWatched(episode: SREpisode, position: Int)
        fun removeEpisodeFromWatched(episode: SREpisode, position: Int)
    }

    lateinit var context: Context
    lateinit var listener: EpisodeItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeVH {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_episode, parent, false)
        val episodeVH = EpisodeVH(itemView)
        episodeVH.itemView.setOnClickListener {
            val position = episodeVH.adapterPosition
            val episodeWithWatchedInfo = episodes[position]
            listener.onItemClick(episodeWithWatchedInfo.episode)
        }
        episodeVH.setAsWatchedIcon.setOnClickListener {
            val position = episodeVH.adapterPosition
            val episodeWithWatchedInfo = episodes[position]
            if (!episodeWithWatchedInfo.watched) {
                listener.setEpisodeAsWatched(episodeWithWatchedInfo.episode, position)
            } else {
                listener.removeEpisodeFromWatched(episodeWithWatchedInfo.episode, position)
            }
        }

        return episodeVH
    }

    override fun onBindViewHolder(holder: EpisodeVH, position: Int) {
        val episodeItem = episodes[position]
        val episode = episodeItem.episode
        val isWatched = episodeItem.watched

        holder.title.text = context.getString(R.string.episode_title_with_numbers)
                .format(episode.season, episode.number, episode.title)

        holder.setAsWatchedIcon.isChecked = isWatched

        val firstAired = episode.firstAired
        if (firstAired == null) {
            holder.setAsWatchedIcon.visibility = View.GONE
        } else {
            holder.setAsWatchedIcon.visibility = if (firstAired <= OffsetDateTime.now()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        Picasso.get()
                .load(getEpisodeUrl(episode.tvdbId))
                .into(holder.episodeArt)
    }

    override fun getItemCount() = episodes.size

    fun updateItem(position: Int, episodeItem: EpisodeItem) {
        episodes[position] = episodeItem
        notifyItemChanged(position)
    }

    inner class EpisodeVH(itemView: View): RecyclerView.ViewHolder(itemView) {

        val title: TextView by bindView(R.id.episode_title)
        val episodeArt: ImageView by bindView(R.id.episode_art)
        val setAsWatchedIcon: CheckableImageButton by bindView(R.id.set_watched)
    }
}