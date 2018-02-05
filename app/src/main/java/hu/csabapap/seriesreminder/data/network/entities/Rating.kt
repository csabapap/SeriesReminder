package hu.csabapap.seriesreminder.data.network.entities

import timber.log.Timber

data class Rating (val average: Float, val count : Int) : Comparable<Rating> {
    override fun compareTo(other: Rating): Int {
        Timber.d("${this}; other: $other")
        var result = when {
            average < other.average -> -1
            average > other.average -> 1
            else -> 0
        }
        Timber.d("result: $result")
        return result
    }
}