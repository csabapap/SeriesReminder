package hu.csabapap.seriesreminder.ui.seasons

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.extensions.bindView

class EpisodesAdapter(val episodes: List<SREpisode>): RecyclerView.Adapter<EpisodesAdapter.EpisodeVH>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeVH {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_episode, parent, false)
        return EpisodeVH(itemView)
    }

    override fun onBindViewHolder(holder: EpisodeVH, position: Int) {
        val episode = episodes[position]

        holder.title.text = context.getString(R.string.episode_title_with_numbers)
                .format(episode.season, episode.number, episode.title)
    }

    override fun getItemCount() = episodes.size

    inner class EpisodeVH(itemView: View): RecyclerView.ViewHolder(itemView) {

        val title: TextView by bindView(R.id.episode_title)

    }
}