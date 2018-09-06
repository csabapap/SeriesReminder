package hu.csabapap.seriesreminder.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.network.entities.BaseShow
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
import kotlinx.android.synthetic.main.item_search_result.view.*

class SearchResultAdapter: RecyclerView.Adapter<SearchResultAdapter.ResultVH>() {

    interface SearchItemClickListener{
        fun onAddClick(showId: Int)
    }

    lateinit var listener: SearchItemClickListener
    var searchResult: List<BaseShow>? = null
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return ResultVH(itemView)
    }

    override fun getItemCount(): Int {
        return searchResult?.size ?: 0
    }

    override fun onBindViewHolder(holder: ResultVH, position: Int) {
        searchResult?.apply {
            val show = this[position]
            holder.bind(show)
        }
    }

    inner class ResultVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(show: BaseShow) {
            itemView.poster.loadFromTmdbUrl(show.ids.tvdb)
            itemView.show_title.text = show.title
            itemView.overview.text = show.overview
            itemView.add_show_btn.setOnClickListener { listener.onAddClick(show.ids.trakt) }
        }
    }

}