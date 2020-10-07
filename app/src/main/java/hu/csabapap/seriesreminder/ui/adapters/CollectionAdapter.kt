package hu.csabapap.seriesreminder.ui.adapters

import androidx.paging.PagedListAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
import kotlinx.android.synthetic.main.item_collection.view.*
import timber.log.Timber

class CollectionAdapter :
        PagedListAdapter<CollectionItem, CollectionAdapter.CollectionVH>(diffCallback) {

    interface CollectionItemClickListener {
        fun onCollectionItemClick(item: CollectionItem)
    }

    var context: Context? = null
    var listener: CollectionItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionVH {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_collection, parent, false)
        val collectionVH = CollectionVH(itemView)
        collectionVH.itemView.setOnClickListener {
            val position = collectionVH.adapterPosition
            if (position == RecyclerView.NO_POSITION) return@setOnClickListener
            val item = getItem(position) ?: return@setOnClickListener
            listener?.apply {
                onCollectionItemClick(item)
            }
        }
        return collectionVH
    }

    override fun onBindViewHolder(holder: CollectionVH, position: Int) {
        val show = getItem(position)?.show!!
        holder.bind(show)
    }

    inner class CollectionVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(show: SRShow) {
            Timber.d("$show")
            itemView.poster.loadFromTmdbUrl(show.tvdbId)
            itemView.show_title.text = show.title
        }

    }

    companion object {
        val diffCallback = (object: DiffUtil.ItemCallback<CollectionItem>() {
            override fun areItemsTheSame(oldItem: CollectionItem, newItem: CollectionItem): Boolean {
                return newItem == oldItem
            }

            override fun areContentsTheSame(oldItem: CollectionItem, newItem: CollectionItem): Boolean {
                return newItem == oldItem
            }
        })
    }
}