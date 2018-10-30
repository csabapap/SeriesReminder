package hu.csabapap.seriesreminder.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
import kotlinx.android.synthetic.main.item_search_result.view.*

class SearchResultAdapter: RecyclerView.Adapter<SearchResultAdapter.ResultVH>() {

    interface SearchItemClickListener {
        fun onItemClick(showId: Int)
        fun onAddClick(showId: Int)
    }

    lateinit var context: Context
    lateinit var listener: SearchItemClickListener
    var searchResult: List<SrSearchResult>? = null
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultVH {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false)
        return ResultVH(itemView)
    }

    override fun getItemCount(): Int {
        return searchResult?.size ?: 0
    }

    override fun onBindViewHolder(holder: ResultVH, position: Int) {
        searchResult?.apply {
            val show = this[position]
            holder.bind(show, position)
        }
    }

    inner class ResultVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(searchResult: SrSearchResult, position: Int) {
            itemView.poster.loadFromTmdbUrl(searchResult.show.ids.tvdb)
            itemView.show_title.text = searchResult.show.title
            itemView.overview.text = searchResult.show.overview
            if (searchResult.inCollection) {
                itemView.add_show_btn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_box_24dp))
            } else {
                itemView.add_show_btn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_24dp))
            }
            itemView.add_show_btn.setOnClickListener {
                if (searchResult.inCollection.not()) {
                    listener.onAddClick(searchResult.show.ids.trakt)
                    searchResult.inCollection = true
                    notifyItemChanged(position)
                }
            }
            itemView.setOnClickListener { v ->
                if (searchResult.inCollection) {
                    listener.onItemClick(searchResult.show.ids.trakt)
                }
            }
        }
    }

}