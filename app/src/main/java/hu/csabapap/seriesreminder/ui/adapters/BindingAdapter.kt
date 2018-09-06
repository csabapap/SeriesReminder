package hu.csabapap.seriesreminder.ui.adapters

import androidx.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso

@BindingAdapter("goneIf")
fun goneIf(view: View, isGone: Boolean) {
    view.visibility = if (isGone) View.GONE else View.VISIBLE
}

@BindingAdapter("app:remoteSrc")
fun setImageUri(view: ImageView, tvdbId: Int) {
    Picasso.with(view.context)
            .load("tvdb://$tvdbId")
            .into(view)

}