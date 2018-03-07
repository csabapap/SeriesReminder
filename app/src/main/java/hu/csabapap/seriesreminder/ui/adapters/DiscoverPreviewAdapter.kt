package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Callback
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import kotlinx.android.synthetic.main.item_trending_show.view.*


class DiscoverPreviewAdapter : RecyclerView.Adapter<DiscoverPreviewAdapter.DiscoverShowVh>() {

    interface PreviewShowListener{
        fun onItemClick(traktId: Int)
    }

    var context: Context? = null
    var showItems: List<ShowItem> = emptyList()

    var listener: PreviewShowListener? = null

    fun updateItems(newItems: List<ShowItem>) {
        val diffResult = DiffUtil.calculateDiff(PreviewDiffs(newItems, showItems))
        showItems = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return showItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverShowVh {
        context = parent.context
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

    override fun onBindViewHolder(holder: DiscoverShowVh, position: Int) {
        val item  = showItems[position]
        holder.bind(item, position)
    }

    inner class DiscoverShowVh(itemView: View) : RecyclerView.ViewHolder(itemView){

        val poster : ImageView? = itemView.poster

        fun bind(show: ShowItem, position: Int){
            if (show.poster.isEmpty()) {
                itemView.show_title.text = show.title
                itemView.show_title.visibility = View.VISIBLE
                poster?.visibility = View.INVISIBLE
            } else {
                poster?.loadFromTmdbUrl(show.poster, (object: Callback {
                    override fun onSuccess() {
                        poster.visibility = View.VISIBLE
                        itemView.show_title.visibility = View.GONE
                    }

                    override fun onError() {

                    }
                }))

            }
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

            if (position % 2 == 0) {
                itemView.setBackgroundColor(
                        ContextCompat.getColor(context!!, R.color.item_background_light))
            } else {
                itemView.setBackgroundColor(
                        ContextCompat.getColor(context!!, R.color.item_background_dark))
            }
        }

        fun setOnClickListener(listener : View.OnClickListener){
            itemView.setOnClickListener(listener)
        }
    }

    inner class PreviewDiffs(private val newItems: List<ShowItem>,
                             private val oldItems: List<ShowItem>)
        : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return newItem.traktId == oldItem.traktId
        }

        override fun getOldListSize() = oldItems.size


        override fun getNewListSize() = newItems.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return oldItem == newItem
        }

    }

}