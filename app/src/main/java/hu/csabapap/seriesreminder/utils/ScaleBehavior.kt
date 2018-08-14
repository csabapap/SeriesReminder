package hu.csabapap.seriesreminder.utils

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import hu.csabapap.seriesreminder.R

class ScaleBehavior(context: Context, attributeSet: AttributeSet)
    : CoordinatorLayout.Behavior<ImageView>(context, attributeSet) {
    private val finalImageSize: Int = context.resources.getDimensionPixelSize(R.dimen.title_container_size)
    val TOOLBAR_SIZE = 150
    var originalDistance = -1
    var maxScaleFactor = -1f
    var startPosition = -1

    override fun layoutDependsOn(parent: CoordinatorLayout, child: ImageView, dependency: View): Boolean {
        return dependency.id == R.id.title_container
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ImageView, dependency: View): Boolean {
        initIfNecessary(child)
        var scaleCorrection = 0f
        if (child.scaleY != 1f && child.scaleY < 1.0f) {
            scaleCorrection = (child.height - child.height * child.scaleY) / 2f
        }
        val distance = child.y + scaleCorrection  - TOOLBAR_SIZE

        var scale = distance / originalDistance
//        if (maxScaleFactor < scale) {
//            if (scale > 1f) scale = 1f
//            Log.d("ScaleBehavior", "max scale factor " + maxScaleFactor)
//            Log.d("ScaleBehavior", "scale factor " + scale)
//            child.scaleY = scale
//            child.scaleX = scale
//
//            if (scale != 1f) {
//                val correction =  (child.height - child.height * scale) / 2f
//                Log.d("ScaleBehavior", "correction y: " + correction)
//                child.translationY = correction
//            }
//        }
        return true
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: ImageView, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
    }

    private fun initIfNecessary(view: View) {
        if (originalDistance == -1) {
            originalDistance = (view.y - TOOLBAR_SIZE).toInt()
        }

        if (maxScaleFactor == -1f) {
            maxScaleFactor = finalImageSize.toFloat() / view.height
        }

        if (startPosition == -1) {
            startPosition = view.y.toInt() + view.height
        }
    }
}