package io.github.sgosiaco.playlistgenerator

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import java.util.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val directoryPref : EditTextPreference? = findPreference("directory")
            val filenamePref : EditTextPreference? = findPreference("filename")
            val datePref : EditTextPreference? = findPreference("date")

            if(TextUtils.isEmpty(directoryPref?.text)) {
                directoryPref?.text = "/PlaylistGenerator"
            }

            if(TextUtils.isEmpty(filenamePref?.text)) {
                filenamePref?.text = "list.m3u8"
            }

            if(TextUtils.isEmpty(datePref?.text)) {
                val cal = Calendar.getInstance()
                datePref?.text = cal.formatString()
            }
        }
    }
}