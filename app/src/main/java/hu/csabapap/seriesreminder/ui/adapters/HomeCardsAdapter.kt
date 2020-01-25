package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.ui.adapters.items.CardItem
import hu.csabapap.seriesreminder.ui.adapters.items.DiscoverCardItem
import hu.csabapap.seriesreminder.ui.adapters.items.UpcomingEpisodeCardItem
import hu.csabapap.seriesreminder.ui.main.discover.DiscoverFragment
import kotlinx.android.synthetic.main.item_discover_card.view.*
import kotlinx.android.synthetic.main.item_episodes.view.*

class HomeCardsAdapter(private val listener: CardClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface CardClickListener{
        fun onMoreButtonClick(type: Int)
    }

    lateinit var context: Context
    var previewShowListener: DiscoverPreviewAdapter.PreviewShowListener? = null
    var episodesClickListener: EpisodeCardsAdapter.EpisodeClickListener? = null
    private var cardItems: MutableList<CardItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        if (viewType == CardItem.POPULAR_CARD_TYPE || viewType == CardItem.TRENDING_CARD_TYPE || viewType == CardItem.MY_SHOWS_TYPE) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_discover_card, parent, false)
            val discoverCardVH = DiscoverCardVH(itemView, viewType)
            discoverCardVH.itemView.rv_shows.adapter = discoverCardVH.previewAdapter
            val layoutManager = discoverCardVH.itemView.rv_shows.layoutManager as LinearLayoutManager
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            val dividerItemDecoration = DividerItemDecoration(context, layoutManager.orientation)
            dividerItemDecoration.setDrawable(context.getDrawable(R.drawable.vertical_separator))
            discoverCardVH.itemView.rv_shows.addItemDecoration(dividerItemDecoration)
            discoverCardVH.itemView.more_btn.setOnClickListener {
                val position = discoverCardVH.layoutPosition
                if (position != -1) {
                    val cardItem = cardItems[position]
                    when (cardItem.type) {
                        CardItem.TRENDING_CARD_TYPE -> listener.onMoreButtonClick(DiscoverFragment.TYPE_TRENDING)
                        CardItem.POPULAR_CARD_TYPE -> listener.onMoreButtonClick(DiscoverFragment.TYPE_POPULAR)
                    }
                }
            }
            return discoverCardVH
        }

        val itemView = LayoutInflater.from(context).inflate(R.layout.item_episodes, parent, false)
        val episodeCardVH = EpisodeCardVH(itemView)
        val episodesRv = episodeCardVH.itemView.episodes_rv
        episodesRv.adapter = episodeCardVH.episodesAdapter
        val layoutManager = episodesRv.layoutManager as LinearLayoutManager
        val decoration = DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL)
        decoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.horizontal_separator)!!)
        episodesRv.addItemDecoration(decoration)
        layoutManager.orientation = RecyclerView.VERTICAL
        episodesRv.setHasFixedSize(true)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(episodesRv)
        return episodeCardVH

    }

    override fun getItemCount() = cardItems.size

    override fun getItemViewType(position: Int) = cardItems[position].type

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DiscoverCardVH -> {
                holder.bind(cardItems[position] as DiscoverCardItem)
            }
            is EpisodeCardVH -> {
                holder.bind(cardItems[position] as UpcomingEpisodeCardItem)
            }
        }
    }

    fun addCard(card: CardItem) {
        if (cardItems.indexOf(card) == -1) {
            cardItems.add(card)
            cardItems = cardItems.sortedDescending().toMutableList()
        } else {
            val position = cardItems.indexOf(card)
            cardItems[position] = card
        }
        notifyDataSetChanged()
    }

    inner class DiscoverCardVH(itemView: View, type: Int) : RecyclerView.ViewHolder(itemView) {

        val previewAdapter = DiscoverPreviewAdapter(type)

        fun bind(discoverCardItem: DiscoverCardItem) {
            itemView.card_title.text = discoverCardItem.title
            if (discoverCardItem.showItems.isEmpty()) {
                itemView.progress.visibility = View.VISIBLE
            } else {
                previewAdapter.updateItems(discoverCardItem.showItems)
                itemView.progress.visibility = View.GONE
            }
            previewShowListener?.let {
                previewAdapter.listener = it
            }

            if (discoverCardItem.type == CardItem.MY_SHOWS_TYPE) {
                itemView.more_btn.visibility = View.GONE
                itemView.rv_shows.layoutManager?.scrollToPosition(0)
            }

        }
    }

    inner class EpisodeCardVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val episodesAdapter = EpisodeCardsAdapter()

        fun bind(cardItem: UpcomingEpisodeCardItem) {
            episodesAdapter.updateItems(cardItem.episodes)

            val clickListener = episodesClickListener
            if (clickListener != null) {
                episodesAdapter.listener = clickListener
            }
        }
    }
}