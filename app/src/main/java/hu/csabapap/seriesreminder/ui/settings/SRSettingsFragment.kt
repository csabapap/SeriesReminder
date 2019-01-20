package hu.csabapap.seriesreminder.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import hu.csabapap.seriesreminder.R

class SRSettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_general, rootKey)
    }
}