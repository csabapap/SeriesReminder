package hu.csabapap.seriesreminder.ui.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.ui.adapters.items.CardItem
import hu.csabapap.seriesreminder.ui.adapters.items.DiscoverCardItem
import hu.csabapap.seriesreminder.ui.main.discover.DiscoverFragment
import kotlinx.android.synthetic.main.item_discover_card.view.*

class HomeCardsAdapter(private val listener: CardClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface CardClickListener{
        fun onMoreButtonClick(type: Int)
    }

    private var cardItems: MutableList<CardItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.item_discover_card, parent, false)
        val discoverCardVH = DiscoverCardVH(itemView)
        discoverCardVH.itemView.rv_shows.adapter = discoverCardVH.previewAdapter
        val layoutManager = discoverCardVH.itemView.rv_shows.layoutManager as LinearLayoutManager
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
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

    override fun getItemCount() = cardItems.size

    override fun getItemViewType(position: Int) = cardItems[position].type

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            CardItem.TRENDING_CARD_TYPE -> {
                if (holder is DiscoverCardVH) {
                    holder.bind(cardItems[position] as DiscoverCardItem)
                }
            }
            CardItem.POPULAR_CARD_TYPE -> {
                if (holder is DiscoverCardVH) {
                    holder.bind(cardItems[position] as DiscoverCardItem)
                }
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

    inner class DiscoverCardVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val previewAdapter = DiscoverPreviewAdapter()

        fun bind(discoverCardItem: DiscoverCardItem) {
            itemView.card_title.text = discoverCardItem.title
            previewAdapter.updateItems(discoverCardItem.showItems)
        }
    }
}