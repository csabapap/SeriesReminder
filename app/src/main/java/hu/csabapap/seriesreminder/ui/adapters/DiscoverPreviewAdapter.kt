package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.databinding.ItemTrendingShowBinding
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import kotlinx.android.synthetic.main.item_trending_show.view.*


class DiscoverPreviewAdapter : RecyclerView.Adapter<DiscoverPreviewAdapter.DiscoverShowVh>() {

    interface PreviewShowListener{
        fun onItemClick(traktId: Int, inCollection: Boolean)
    }

    var context: Context? = null
    private var showItems: List<ShowItem> = emptyList()

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
        val binding = DataBindingUtil.inflate<ItemTrendingShowBinding>(LayoutInflater.from(context),
                R.layout.item_trending_show, parent, false)
        val showItem = DiscoverShowVh(binding)
        showItem.setOnClickListener(View.OnClickListener {
            val position = showItem.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = showItems[position]
                listener?.onItemClick(item.traktId, item.inCollection)
            }
        })
        return showItem
    }

    override fun onBindViewHolder(holder: DiscoverShowVh, position: Int) {
        val item  = showItems[position]
        holder.bind(item, position)
    }

    inner class DiscoverShowVh(private val binding: ItemTrendingShowBinding)
        : RecyclerView.ViewHolder(binding.root){

        val poster : ImageView? = itemView.poster

        fun bind(show: ShowItem, position: Int){
            binding.show = show
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