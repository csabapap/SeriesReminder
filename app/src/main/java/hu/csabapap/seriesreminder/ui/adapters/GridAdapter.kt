package hu.csabapap.seriesreminder.ui.adapters

import androidx.paging.PagedListAdapter
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.GridItem
import hu.csabapap.seriesreminder.data.db.entities.Item
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.databinding.GridItemBinding

internal class GridAdapter : PagedListAdapter<GridItem<Item>, GridAdapter.GridViewHolder>(GRID_ITEM_COMPARATOR) {

    interface GridItemClickListener {
        fun onItemClick(traktId: Int, inCollection: Boolean)
    }

    var listener: GridItemClickListener? = null

    lateinit var context: Context
    var shows: List<GridItem<Item>>? = null
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val gridItem = getItem(position) ?: return
        val show = gridItem.show ?: return
        show.inCollection = gridItem.inCollection
        holder.bind(show)
        holder.setOnClickListener(View.OnClickListener {
            listener?.onItemClick(show.traktId, show.inCollection)
        })

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        context = parent.context!!
        val binding = DataBindingUtil.inflate<GridItemBinding>(LayoutInflater.from(context),
                R.layout.grid_item, parent, false)
        return GridViewHolder(binding)
    }


    internal class GridViewHolder(private val layoutBinding: GridItemBinding) : RecyclerView.ViewHolder(layoutBinding.root) {

        fun bind(show: SRShow) {
            layoutBinding.show = show
            layoutBinding.executePendingBindings()
        }

        fun setOnClickListener(listener: View.OnClickListener) {
            itemView.setOnClickListener(listener)
        }
    }

    companion object {
        private val GRID_ITEM_COMPARATOR = object : DiffUtil.ItemCallback<GridItem<Item>>() {
            override fun areItemsTheSame(oldItem: GridItem<Item>, newItem: GridItem<Item>): Boolean {
                return oldItem.show?.traktId == newItem.show?.traktId
            }

            override fun areContentsTheSame(oldItem: GridItem<Item>, newItem: GridItem<Item>): Boolean {
                return oldItem.show == newItem.show
            }

        }
    }
}