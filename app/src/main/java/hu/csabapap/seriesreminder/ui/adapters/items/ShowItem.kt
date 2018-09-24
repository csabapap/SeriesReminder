package hu.csabapap.seriesreminder.ui.adapters.items

data class ShowItem(
        val traktId: Int,
        val tvdbId: Int,
        val title: String,
        val poster: String,
        val inCollection: Boolean = false)