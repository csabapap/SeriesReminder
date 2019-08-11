package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.extensions.bindView
import java.util.*

class SeasonsAdapter(val seasons: List<SRSeason>): RecyclerView.Adapter<SeasonsAdapter.SeasonsVH>() {

    interface SeasonClickListener {
        fun onItemClick(season: SRSeason)
    }

    lateinit var context: Context
    var listener: SeasonClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonsVH {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_season,
                parent, false)

        val seasonsVH = SeasonsVH(itemView)
        seasonsVH.itemView.setOnClickListener {
            val position = seasonsVH.adapterPosition
            if (position != -1) {
                val season = seasons[position]
                listener?.onItemClick(season)
            }
        }
        return seasonsVH
    }

    override fun getItemCount() = seasons.size

    override fun onBindViewHolder(holder: SeasonsVH, position: Int) {
        val season = seasons[position]

        holder.seasonTitle.text = String.format(Locale.ENGLISH,
                context.getString(R.string.seasons_title), season.number)
        val seasonProgressText = String.format(Locale.ENGLISH,
                context.getString(R.string.seasons_progress),
                season.nmbOfWatchedEpisodes, season.airedEpisodeCount)
        holder.readableProgress.text = seasonProgressText
        holder.progress.apply {
            max = season.airedEpisodeCount
            progress = season.nmbOfWatchedEpisodes
        }

        val posterUrl = getThumbnailUrl(season.thumbnail)

        Picasso.with(context)
                .load(posterUrl)
                .into(holder.poster)
    }


    inner class SeasonsVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        val seasonTitle: TextView by bindView(R.id.season_title)
        val readableProgress: TextView by bindView(R.id.readable_progress)
        val progress: ProgressBar by bindView(R.id.season_progress)
        val poster: ImageView by bindView(R.id.poster)
    }
}