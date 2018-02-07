package hu.csabapap.seriesreminder.extensions

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


fun ImageView.loadFromUrl(url: String, callback: Callback? = null) {
    Picasso.with(context)
            .load(url)
            .into(this, callback)
}