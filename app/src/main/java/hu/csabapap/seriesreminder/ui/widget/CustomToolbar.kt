package hu.csabapap.seriesreminder.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar
import hu.csabapap.seriesreminder.R

class CustomToolbar @JvmOverloads constructor(context: Context,
                    attrs: AttributeSet? = null,
                    defStyleAttr: Int = R.attr.toolbarStyle)
    : Toolbar(context, attrs, defStyleAttr) {


    var backgroundColorAlpha: Int = 255
        set(value) {
            this.background.alpha = value
        }

}