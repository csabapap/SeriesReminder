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

class EpisodesAdapter(val episodes: List<SREpisode>): RecyclerView.Adapter<EpisodesAdapter.EpisodeVH>() {

    interface EpisodeItemClickListener {
        fun onItemClick(episode: SREpisode)
        fun setEpisodeAsWatched(episode: SREpisode)
    }

    lateinit var context: Context
    lateinit var listener: EpisodeItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeVH {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_episode, parent, false)
        val episodeVH = EpisodeVH(itemView)
        episodeVH.itemView.setOnClickListener {
            val position = episodeVH.adapterPosition
            val episode = episodes[position]
            listener.onItemClick(episode)
        }
        episodeVH.setAsWatchedIcon.setOnClickListener {
            val position = episodeVH.adapterPosition
            val episode = episodes[position]
            listener.setEpisodeAsWatched(episode)
        }

        return episodeVH
    }

    override fun onBindViewHolder(holder: EpisodeVH, position: Int) {
        val episode = episodes[position]

        holder.title.text = context.getString(R.string.episode_title_with_numbers)
                .format(episode.season, episode.number, episode.title)

        Picasso.with(context)
                .load(getEpisodeUrl(episode.tvdbId))
                .into(holder.episodeArt)
    }

    override fun getItemCount() = episodes.size

    inner class EpisodeVH(itemView: View): RecyclerView.ViewHolder(itemView) {

        val title: TextView by bindView(R.id.episode_title)
        val episodeArt: ImageView by bindView(R.id.episode_art)
        val setAsWatchedIcon: ImageView by bindView(R.id.set_watched)
    }
}