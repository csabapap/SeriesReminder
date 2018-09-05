package hu.csabapap.seriesreminder.extensions

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


fun ImageView.loadFromTmdbUrl(tvdbId: Int, placeholder: Int? = null, callback: Callback? = null) {
    Picasso.with(context)
            .load("tvdb://$tvdbId")
            .let { requestCreator ->
                placeholder?.let {
                    requestCreator.placeholder(it)
                }
                requestCreator
            }
            .into(this, callback)
}

fun Context.toPixelFromDip(value: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        value, resources.displayMetrics)

fun View.toPixelFromDip(value: Float) = context.toPixelFromDip(value)