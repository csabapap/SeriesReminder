package hu.csabapap.seriesreminder.utils

import android.net.Uri
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import timber.log.Timber
import javax.inject.Inject


class TvdbRequestHandler @Inject constructor(private val tvdbApi: TvdbApi,
                                             private val downloader: OkHttp3Downloader)
    : RequestHandler() {

    override fun canHandleRequest(data: Request?): Boolean {
        return schema == data?.uri?.scheme
    }

    override fun load(request: Request?, networkPolicy: Int): Result? {
        val tvdbId = request?.uri?.host?.toInt() ?: throw IllegalArgumentException("invalid tvdb id")
        val response = tvdbApi.images(tvdbId).execute().body()
        response?.let {
            val popularImage = it.data.maxBy { image ->
                image.ratingsInfo.average
            }

            val posterUrl = getThumbnailUrl(popularImage?.thumbnail)
            if (posterUrl.isEmpty()) {
                return null
            }
            return downloadImage(
                    Uri.parse(posterUrl),
                    networkPolicy)
        }

        return null
    }

    private fun downloadImage(uri: Uri, networkPolicy: Int): Result? {
        val response = downloader.load(uri, networkPolicy) ?: return null
        val inputStream = response.inputStream ?: return null
        return RequestHandler.Result(inputStream, Picasso.LoadedFrom.NETWORK)
    }

    companion object {
        private const val schema = "tvdb"
    }
}