package hu.csabapap.seriesreminder.data.network

const val TVDB_BANNER_URL = "https://www.thetvdb.com/banners/"
const val TVDB_BANNER_CACHE_URL = "${TVDB_BANNER_URL}_cache/"

fun getThumbnailUrl(thumbnail: String?) = thumbnail?.apply {
    return "$TVDB_BANNER_URL$thumbnail"
} ?: ""

fun getFullSizeUrl(fileName: String?) = fileName.apply {
    return "$TVDB_BANNER_URL$fileName"
} ?: ""