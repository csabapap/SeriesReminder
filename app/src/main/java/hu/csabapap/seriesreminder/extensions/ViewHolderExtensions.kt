package hu.csabapap.seriesreminder.extensions

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

fun <T> lazyUnSynchronized(initializer: () -> T): Lazy<T> =
        lazy(LazyThreadSafetyMode.NONE, initializer)

fun <T: View> RecyclerView.ViewHolder.bindView(@IdRes idRes: Int): Lazy<T> {
    return lazyUnSynchronized {
        itemView.findViewById<T>(idRes)
    }
}