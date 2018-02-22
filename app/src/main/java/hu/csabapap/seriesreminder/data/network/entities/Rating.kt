package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Rating (var average: Float, var count : Int) : Comparable<Rating> {
    override fun compareTo(other: Rating)  = when {
            average < other.average -> -1
            average > other.average -> 1
            else -> 0
    }
}