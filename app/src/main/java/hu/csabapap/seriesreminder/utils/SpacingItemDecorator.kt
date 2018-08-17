package hu.csabapap.seriesreminder.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecorator(private val sizeGridSpacingPx: Int, private val gridSize: Int)
    : RecyclerView.ItemDecoration() {

    private var needLeftSpacing = false

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val width = parent.width ?: 0
        val frameWidth = ((width - sizeGridSpacingPx * (gridSize - 1)) / gridSize)
        val padding = width / gridSize - frameWidth
        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        if (itemPosition < gridSize) {
            outRect.top = 0
        } else {
            outRect.top = sizeGridSpacingPx
        }
        if (itemPosition % gridSize == 0) {
            outRect.left = padding
            outRect.right = padding
            needLeftSpacing = true
        } else if ((itemPosition + 1) % gridSize == 0) {
            needLeftSpacing = false
            outRect.right = 0
            outRect.left = padding
        } else if (needLeftSpacing) {
            needLeftSpacing = false
            outRect.left = sizeGridSpacingPx - padding
            if ((itemPosition + 2) % gridSize == 0) {
                outRect.right = sizeGridSpacingPx - padding
            } else {
                outRect.right = sizeGridSpacingPx / 2
            }
        } else if ((itemPosition + 2) % gridSize == 0) {
            needLeftSpacing = false
            outRect.left = sizeGridSpacingPx / 2
            outRect.right = sizeGridSpacingPx - padding
        } else {
            needLeftSpacing = false
            outRect.left = sizeGridSpacingPx / 2
            outRect.right = sizeGridSpacingPx / 2
        }

        if (itemPosition % gridSize == gridSize-1) {
            outRect.right = padding
        }
        outRect.bottom = 0
    }

}
