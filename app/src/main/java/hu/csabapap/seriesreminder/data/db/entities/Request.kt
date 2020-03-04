package hu.csabapap.seriesreminder.data.db.entities

enum class Request(val tag: String) {
    SHOW_DETAILS("show_details"),
    SYNC_SHOWS("sync_shows")
}