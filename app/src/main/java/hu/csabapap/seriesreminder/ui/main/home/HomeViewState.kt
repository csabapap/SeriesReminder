package hu.csabapap.seriesreminder.ui.main.home

data class HomeViewState(
        val displayProgressBar: Boolean = false,
        val displayTrendingCard: Boolean = false,
        val displayPopularCard: Boolean = false
)