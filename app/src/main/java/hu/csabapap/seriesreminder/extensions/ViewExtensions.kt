package hu.csabapap.seriesreminder.extensions

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


fun ImageView.loadFromTmdbUrl(url: String, placeholder: Int? = null, callback: Callback? = null) {
    Picasso.with(context)
            .load("https://thetvdb.com/banners/$url")
            .let { requestCreator ->
                placeholder?.let {
                    requestCreator.placeholder(it)
                }
                requestCreator
            }
            .into(this, callback)
}