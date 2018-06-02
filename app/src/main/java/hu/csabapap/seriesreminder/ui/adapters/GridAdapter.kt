package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.GridItem
import hu.csabapap.seriesreminder.data.db.entities.Item
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.databinding.GridItemBinding
import hu.csabapap.seriesreminder.ui.main.discover.DiscoverViewModel
import kotlinx.android.synthetic.main.grid_item.view.*

internal class GridAdapter : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    interface GridItemClickListener {
        fun onItemClick(traktId: Int)
    }

    var listener: GridItemClickListener? = null

    lateinit var context: Context
    var shows: List<GridItem<Item>>? = null
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {

        val show = shows!![position].show ?: return

        holder.bind(show)
        holder.setOnClickListener(View.OnClickListener {
            listener?.onItemClick(show.traktId)
        })

    }

    override fun getItemCount(): Int {
        return shows?.size ?: 0
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
}