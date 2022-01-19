package hu.csabapap.seriesreminder.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.data.network.getPosterUrl
import hu.csabapap.seriesreminder.extensions.toPixelFromDip
import hu.csabapap.seriesreminder.utils.RoundedTransformation
import kotlinx.android.synthetic.main.item_search_result.view.*

class SearchResultAdapter: RecyclerView.Adapter<SearchResultAdapter.ResultVH>() {

    interface SearchItemClickListener {
        fun onItemClick(showId: Int, inCollection: Boolean)
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

    fun clear() {
        searchResult = null
        notifyDataSetChanged()
    }

    inner class ResultVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(searchResult: SrSearchResult, position: Int) {
            searchResult.show.ids?.tvdb?.let { tvdbId ->
                Picasso.with(context)
                    .load(getPosterUrl(tvdbId))
                    .transform(RoundedTransformation(itemView.toPixelFromDip(2f)))
                    .into(itemView.poster)
            }
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
            itemView.setOnClickListener {
                listener.onItemClick(searchResult.show.ids.trakt, searchResult.inCollection)
            }
        }
    }

    fun itemAddedToCollection(showId: Int) {
        searchResult?.forEachIndexed { i, result ->
            if (result.show.ids.trakt == showId) {
                result.inCollection = true
                notifyItemChanged(i)
                return
            }
        }
    }

}