package hu.csabapap.seriesreminder.ui.adapters

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.network.getPosterUrl
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem

@BindingAdapter("goneIf")
fun goneIf(view: View, isGone: Boolean) {
    view.visibility = if (isGone) View.GONE else View.VISIBLE
}

@BindingAdapter("app:remoteSrc")
fun setImageUri(view: ImageView, showItem: ShowItem) {
    val url = if (showItem.poster.isEmpty()) {
        getPosterUrl(showItem.tvdbId)
    } else {
        getThumbnailUrl(showItem.poster)
    }

    Picasso.with(view.context)
            .load(url)
            .placeholder(R.color.light_grey)
            .into(view)
}

@BindingAdapter("app:remoteSrc")
fun setImageUri(view: ImageView, showItem: SRShow) {
    val url = if (showItem.posterThumb.isEmpty()) {
        getPosterUrl(showItem.tvdbId)
    } else {
        getThumbnailUrl(showItem.posterThumb)
    }

    Picasso.with(view.context)
            .load(url)
            .placeholder(R.color.light_grey)
            .into(view)
}