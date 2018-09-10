package hu.csabapap.seriesreminder.data.models

import hu.csabapap.seriesreminder.data.network.entities.BaseShow

data class SrSearchResult(val show: BaseShow, val inCollection: Boolean)