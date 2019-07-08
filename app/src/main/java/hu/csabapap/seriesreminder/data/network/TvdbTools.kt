package hu.csabapap.seriesreminder.data.network

const val TVDB_BANNER_URL = "https://www.thetvdb.com/banners/"
const val TVDB_BANNER_CACHE_URL = "${TVDB_BANNER_URL}_cache/"

fun getThumbnailUrl(thumbnail: String?) = thumbnail?.apply {
    return "$TVDB_BANNER_URL$thumbnail"
} ?: ""

fun getFullSizeUrl(fileName: String?) = fileName?.apply {
    return "$TVDB_BANNER_URL$fileName"
} ?: ""

fun getPosterUrl(tvdbId: Int) = "tvdb://poster?id=$tvdbId"

fun getCoverUrl(tvdbId: Int) = "tvdb://fanart?id=$tvdbId"

fun getEpisodeUrl(tvdbId: Int) = "tvdb://screen?id=$tvdbId"