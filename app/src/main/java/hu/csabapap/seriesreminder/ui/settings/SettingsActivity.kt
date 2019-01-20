package hu.csabapap.seriesreminder.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.csabapap.seriesreminder.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        setupActionBar()

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SRSettingsFragment())
                .commit()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        toolbar.setNavigationOnClickListener {finish()}
        toolbar.title = "Settings"
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
