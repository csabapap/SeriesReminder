package hu.csabapap.seriesreminder.ui.adapters

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import kotlinx.android.synthetic.main.item_collection.view.*

class CollectionAdapter :
        PagedListAdapter<CollectionItem, CollectionAdapter.CollectionVH>(diffCallback) {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CollectionVH {
        context = parent?.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_collection, parent, false)
        return CollectionVH(itemView)
    }

    override fun onBindViewHolder(holder: CollectionVH?, position: Int) {
        val show = getItem(position)?.show!!
        holder?.bind(show)
    }

    inner class CollectionVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(show: SRShow) {
            itemView.show_title.text = show.title
        }

    }

    companion object {
        val diffCallback = (object: DiffCallback<CollectionItem>() {
            override fun areItemsTheSame(oldItem: CollectionItem, newItem: CollectionItem): Boolean {
                return newItem == oldItem
            }

            override fun areContentsTheSame(oldItem: CollectionItem, newItem: CollectionItem): Boolean {
                return newItem == oldItem
            }
        })
    }
}