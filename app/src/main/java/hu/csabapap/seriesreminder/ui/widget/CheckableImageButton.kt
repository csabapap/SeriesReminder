package hu.csabapap.seriesreminder.ui.widget

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton

class CheckableImageButton @JvmOverloads constructor(context: Context,
                                                     attributeSet: AttributeSet? = null,
                                                     defStyleAttr: Int = 0)
    : AppCompatImageButton(context, attributeSet, defStyleAttr), Checkable {

    companion object {
        val DRAWABLE_CHECKED_STATE = arrayOf(android.R.attr.state_checked)
    }

    private var _checked: Boolean = false

    override fun setChecked(checked: Boolean) {
        _checked = checked
    }

    override fun isChecked() = _checked

    override fun toggle() {
        isChecked = !isChecked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, DRAWABLE_CHECKED_STATE.toIntArray())
        }
        return drawableState
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putBoolean("checked", isChecked)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null) return
        if (state is Bundle) {
            _checked = state.getBoolean("checked")
            val superState = state.getParcelable("superState") as Parcelable
            super.onRestoreInstanceState(superState)
            return
        }
        super.onRestoreInstanceState(state)
    }
}