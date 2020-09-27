package hu.csabapap.seriesreminder.data.models

import com.uwetrottmann.trakt5.entities.Show

data class SrSearchResult(val show: Show, var inCollection: Boolean)