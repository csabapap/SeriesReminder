package hu.csabapap.seriesreminder.utils

import android.net.Uri
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.getFullSizeUrl
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import javax.inject.Inject


class TvdbRequestHandler @Inject constructor(private val tvdbApi: TvdbApi,
                                             private val episodesRepository: EpisodesRepository,
                                             private val downloader: OkHttp3Downloader
)
    : RequestHandler() {

    override fun canHandleRequest(data: Request?): Boolean {
        return schema == data?.uri?.scheme
    }

    override fun load(request: Request?, networkPolicy: Int): Result? {
        val uri = request?.uri ?: return null
        val type = uri.host ?: return null
        val tvdbId = uri.getQueryParameter("id")?.toInt() ?: return null
        if (type == "screen") {
            return downloadScreen(tvdbId, networkPolicy)
        }
        val response = tvdbApi.imagesCall(tvdbId, type).execute()
        val responseBody = response.body()
        responseBody?.let {
            val popularImage = it.data.maxByOrNull { image ->
                image.ratingsInfo.average
            }

            val imageUrl = if(type == "poster") {
                getThumbnailUrl(popularImage?.thumbnail)
            } else {
                getFullSizeUrl(popularImage?.fileName)
            }
            if (imageUrl.isEmpty()) {
                return null
            }
            return downloadImage(
                    Uri.parse(imageUrl),
                    networkPolicy)
        }
        return null
    }

    private fun downloadScreen(tvdbId: Int, networkPolicy: Int): Result? {
        val response = tvdbApi.episodeCall(tvdbId).execute().body()
        response.let {
            val imageUrl = getFullSizeUrl(it?.data?.filename)

            if (imageUrl.isEmpty()) {
                return null
            }
            episodesRepository.saveImage(tvdbId, imageUrl)
            return downloadImage(
                    Uri.parse(imageUrl),
                    networkPolicy)
        }
    }

    private fun downloadImage(uri: Uri, networkPolicy: Int): Result? {
        val reuqest = okhttp3.Request.Builder()
            .url(uri.toString())
            .build()
        val response = downloader.load(reuqest)
        val source = response.body?.source() ?: return null
        return Result(source, Picasso.LoadedFrom.NETWORK)
    }

    companion object {
        private const val schema = "tvdb"
    }
}