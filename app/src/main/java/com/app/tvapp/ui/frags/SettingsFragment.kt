package com.app.tvapp.ui.frags

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.app.tvapp.R
import com.app.tvapp.viewmodels.MainViewModel
import java.util.*


class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        findPreference<Preference>("clear_key")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                viewModel.deleteAllData()
                Toast.makeText(requireContext(), "Data deleted!", Toast.LENGTH_SHORT).show()
                true
            }

        findPreference<ListPreference>("themes_key")?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, theme ->

                AppCompatDelegate.setDefaultNightMode(
                    when (theme.toString().toInt()) {
                        0 -> AppCompatDelegate.MODE_NIGHT_YES

                        1 -> AppCompatDelegate.MODE_NIGHT_NO

                        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                )
                true
            }
        findPreference<ListPreference>("lang_key")?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, lang ->
                //TODO: change the language of the application
                true
            }

    }
}