package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import hu.csabapap.seriesreminder.R
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
        val showItem = DiscoverShowVh(itemView)
        showItem.setOnClickListener(View.OnClickListener {
            val position = showItem.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = showItems[position]
                listener?.onItemClick(item.traktId)
            }
        })
        return showItem
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
            if (show.extraDataIcon != -1) {
                itemView.extra_icon.setImageDrawable(
                        ContextCompat.getDrawable(context!!, show.extraDataIcon))
                itemView.extra_icon.visibility = View.VISIBLE
            } else {
                itemView.extra_icon.visibility = View.GONE
            }

            if (show.extraDataValue.isEmpty().not()) {
                itemView.extra_value.text = show.extraDataValue
                itemView.extra_value.visibility = View.VISIBLE
            } else {
                itemView.extra_value.visibility = View.GONE
            }
        }

        fun setOnClickListener(listener : View.OnClickListener){
            itemView.setOnClickListener(listener)
        }
    }

}