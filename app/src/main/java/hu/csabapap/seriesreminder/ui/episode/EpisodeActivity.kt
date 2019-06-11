package hu.csabapap.seriesreminder.ui.episode

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.utils.Episode
import kotlinx.android.synthetic.main.activity_episode.*

class EpisodeActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode)
        setSupportActionBar(toolbar)

        supportFragmentManager.beginTransaction()
                .add(R.id.fragment, Episode.createFragment(intent.extras))
                .commit()
    }

}
