package hu.csabapap.seriesreminder.ui.views

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.systemService
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.extensions.toPixelFromDip
import timber.log.Timber

class EpisodeCardView: CardView {

    lateinit var episodeTitle: TextView
    lateinit var episodeInfo: TextView
    lateinit var airsInInfo: TextView
    lateinit var image: ImageView
    lateinit var placeholder: ImageView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributes: AttributeSet?):
            this(context, attributes, 0)

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int):
            super(context, attributes, defStyleAttr) {
        initView()
    }


    private fun initView() {
        val view = context.systemService<LayoutInflater>().inflate(R.layout.item_episode_card, this, true)

        episodeTitle = view.findViewById(R.id.title)
        episodeInfo = view.findViewById(R.id.episode_info)
        airsInInfo = view.findViewById(R.id.airs_in_text)
        image = view.findViewById(R.id.episode_image)
        placeholder = view.findViewById(R.id.placeholder)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.marginEnd = toPixelFromDip(8F).toInt()

        val parentWidth = (MeasureSpec.getSize(widthMeasureSpec).toFloat() * 0.96).toInt()

        val newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.getMode(widthMeasureSpec))

        super.onMeasure(newWidthMeasureSpec, heightMeasureSpec)
    }
}