package hu.csabapap.seriesreminder.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.ui.adapters.items.CardItem
import hu.csabapap.seriesreminder.ui.adapters.items.DiscoverCardItem
import hu.csabapap.seriesreminder.ui.adapters.items.NextEpisodesCardItem
import hu.csabapap.seriesreminder.ui.adapters.items.UpcomingEpisodeCardItem
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
    var nextEpisodesClickListener: NextEpisodesAdapter.NextEpisodeClickListener? = null
    private var cardItems: MutableList<CardItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        if (viewType == CardItem.HEADER) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_home_header, parent, false)
            return HeaderVH(itemView)
        }

        if (viewType == CardItem.FOOTER) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_home_footer, parent, false)
            return HeaderVH(itemView)
        }

        if (viewType == CardItem.POPULAR_CARD_TYPE || viewType == CardItem.TRENDING_CARD_TYPE || viewType == CardItem.MY_SHOWS_TYPE) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_discover_card, parent, false)
            val discoverCardVH = DiscoverCardVH(itemView, viewType)
            discoverCardVH.itemView.rv_shows.adapter = discoverCardVH.previewAdapter
            val layoutManager = discoverCardVH.itemView.rv_shows.layoutManager as LinearLayoutManager
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            val dividerItemDecoration = DividerItemDecoration(context, layoutManager.orientation)
            ContextCompat.getDrawable(context, R.drawable.vertical_separator)?.let {
                dividerItemDecoration.setDrawable(it)
            }
            discoverCardVH.itemView.rv_shows.addItemDecoration(dividerItemDecoration)
            discoverCardVH.itemView.more_btn.setOnClickListener {
                val position = discoverCardVH.layoutPosition
                if (position != -1) {
                    val cardItem = cardItems[position-1]
                    listener.onMoreButtonClick(cardItem.type)
                }
            }
            return discoverCardVH
        } else if (viewType == CardItem.NEXT_EPISODES_TYPE) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_episodes, parent, false)
            val episodeCardVH = NextEpisodesListVH(itemView)
            val episodesRv = episodeCardVH.itemView.episodes_rv
            episodesRv.adapter = episodeCardVH.episodesAdapter
            val layoutManager = episodesRv.layoutManager as LinearLayoutManager
            val decoration = DividerItemDecoration(context, layoutManager.orientation)
            decoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.horizontal_separator)!!)
            episodesRv.addItemDecoration(decoration)
            layoutManager.orientation = RecyclerView.VERTICAL
            episodesRv.setHasFixedSize(true)
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(episodesRv)
            return episodeCardVH
        }

        val itemView = LayoutInflater.from(context).inflate(R.layout.item_episodes, parent, false)
        val episodeCardVH = EpisodeCardVH(itemView)
        val episodesRv = episodeCardVH.itemView.episodes_rv
        episodesRv.adapter = episodeCardVH.episodesAdapter
        val layoutManager = episodesRv.layoutManager as LinearLayoutManager
        val decoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        decoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.horizontal_separator)!!)
        episodesRv.addItemDecoration(decoration)
        layoutManager.orientation = RecyclerView.VERTICAL
        episodesRv.setHasFixedSize(true)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(episodesRv)
        return episodeCardVH
    }

    override fun getItemCount() = cardItems.size + 2

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return CardItem.HEADER
        }
        if (position == cardItems.size + 1) {
            return CardItem.FOOTER
        }
        return cardItems[position-1].type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) return
        if (position == cardItems.size + 1) return
        val card = cardItems[position-1]
        when (holder) {
            is DiscoverCardVH -> {
                holder.bind(card as DiscoverCardItem)
            }
            is EpisodeCardVH -> {
                holder.bind(card as UpcomingEpisodeCardItem)
            }
            is NextEpisodesListVH -> {
                holder.bind(card as NextEpisodesCardItem)
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

    fun removeCard(cardType: Int) {
        val trendingCard = cardItems.find { it.type == cardType }
        cardItems.remove(trendingCard)
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
                itemView.more_btn.text = context.getString(R.string.show_all)
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
   
    inner class NextEpisodesListVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val episodesAdapter = NextEpisodesAdapter()

        fun bind(cardItem: NextEpisodesCardItem) {
            itemView.label.text = "Next Episodes"
            episodesAdapter.episodes = cardItem.episodes.toMutableList()

            val clickListener = nextEpisodesClickListener
            if (clickListener != null) {
                episodesAdapter.listener = clickListener
            }
        }
    }

    inner class HeaderVH(itemView: View) : RecyclerView.ViewHolder(itemView) { }
}