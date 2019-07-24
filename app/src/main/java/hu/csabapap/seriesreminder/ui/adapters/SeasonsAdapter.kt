package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.extensions.bindView

class SeasonsAdapter(val seasons: List<SRSeason>): RecyclerView.Adapter<SeasonsAdapter.SeasonsVH>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonsVH {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_season,
                parent, false)

        return SeasonsVH(itemView)
    }

    override fun getItemCount() = seasons.size

    override fun onBindViewHolder(holder: SeasonsVH, position: Int) {
        val season = seasons[position]

        holder.seasonTitle.text = "Season ${season.number}"

        val posterUrl = getThumbnailUrl(season.thumbnail)

        Picasso.with(context)
                .load(posterUrl)
                .into(holder.poster)
    }


    inner class SeasonsVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        val seasonTitle: TextView by bindView(R.id.season_title)
        val poster: ImageView by bindView(R.id.poster)
    }
}