package hu.csabapap.seriesreminder.ui.adapters.items

data class ShowItem(
        val traktId: Int,
        val title: String,
        val poster: String,
        val extraDataIcon: Int = -1,
        val extraDataValue: String = "")