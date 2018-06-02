package hu.csabapap.seriesreminder.ui.adapters

import android.databinding.BindingAdapter
import android.view.View

@BindingAdapter("goneIf")
fun goneIf(view: View, isGone: Boolean) {
    view.visibility = if (isGone) View.GONE else View.VISIBLE
}