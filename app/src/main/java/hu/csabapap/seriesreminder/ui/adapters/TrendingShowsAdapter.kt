package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
import kotlinx.android.synthetic.main.item_trending_show.view.*


class TrendingShowsAdapter : RecyclerView.Adapter<TrendingShowsAdapter.TrendingShowVh>() {

    // TODO trending and popular shows use the same adapter !!!

    var context: Context? = null
    var shows: List<TrendingGridItem> = emptyList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return shows.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TrendingShowVh {
        context = parent?.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_trending_show, parent, false)
        return TrendingShowVh(itemView)
    }

    override fun onBindViewHolder(holder: TrendingShowVh?, position: Int) {
        val trendingShow  = shows[position]
        holder?.bind(trendingShow.show!!)
    }

    inner class TrendingShowVh(itemView: View) : RecyclerView.ViewHolder(itemView){

        val poster : ImageView? = itemView.poster

        fun bind(show: SRShow){
            Picasso.with(context)
                    .load("https://thetvdb.com/banners/${show.posterThumb}")
                    .into(poster, object: Callback{
                        override fun onSuccess() {
//                            val bitmapDrawable = poster?.drawable as BitmapDrawable
//                            val bitmap = bitmapDrawable.bitmap
//                            Palette
//                                    .from(bitmap)
//                                    .clearFilters()
//                                    .generate({palette = it})
                        }

                        override fun onError() {

                        }

                    })
        }
    }

}