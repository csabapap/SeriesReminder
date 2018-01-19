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
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import kotlinx.android.synthetic.main.item_trending_show.view.*


class DiscoverPreviewAdapter : RecyclerView.Adapter<DiscoverPreviewAdapter.DiscoverShowVh>() {

    interface PreviewShowListener{
        fun onItemClick(traktId: Int)
    }

    var context: Context? = null
    var showItems: List<ShowItem> = emptyList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    var listener: PreviewShowListener? = null

    override fun getItemCount(): Int {
        return showItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DiscoverShowVh {
        context = parent?.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_trending_show, parent, false)
        val trendingShowVh = DiscoverShowVh(itemView)
        trendingShowVh.setOnClickListener(View.OnClickListener {
            val position = trendingShowVh.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = showItems[position]
                listener?.onItemClick(item.traktId)
            }
        })
        return trendingShowVh
    }

    override fun onBindViewHolder(holder: DiscoverShowVh?, position: Int) {
        val item  = showItems[position]
        holder?.bind(item)
    }

    inner class DiscoverShowVh(itemView: View) : RecyclerView.ViewHolder(itemView){

        val poster : ImageView? = itemView.poster

        fun bind(show: ShowItem){
            Picasso.with(context)
                    .load("https://thetvdb.com/banners/${show.poster}")
                    .into(poster)
        }

        fun setOnClickListener(listener : View.OnClickListener){
            itemView.setOnClickListener(listener)
        }
    }

}