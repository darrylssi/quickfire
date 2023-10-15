package nz.ac.uclive.ajs418.quickfire.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import nz.ac.uclive.ajs418.quickfire.R


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val themeSwitch: SwitchCompat = view.findViewById(R.id.themeSwitch)
        val mediaSwitch: SwitchCompat = view.findViewById(R.id.mediaSwitch)
        val theme: TextView = view.findViewById(R.id.themeText)
        val media: TextView = view.findViewById(R.id.mediaText)

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE)
        val nightMode: Boolean = sharedPreferences.getBoolean("isNightMode", false)
        val movieMode: Boolean = sharedPreferences.getBoolean("isMovieMode", true)

        if (nightMode) {
            themeSwitch.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            theme.text = getString(R.string.dark)
        }

        if (movieMode) {
            mediaSwitch.isChecked = true
            media.text = getString(R.string.movie)
        }

        themeSwitch.setOnClickListener {
            sharedPreferences.edit().putBoolean("isThemeChanged", true).apply()
            if (nightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferences.edit().putBoolean("isNightMode", false).apply()
                theme.text = getString(R.string.light)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferences.edit().putBoolean("isNightMode", true).apply()
                theme.text = getString(R.string.dark)
            }
        }

        val changed: Boolean = sharedPreferences.getBoolean("isThemeChanged", false)
        if (changed) {
            sharedPreferences.edit().putBoolean("isThemeChanged", false).apply()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SettingsFragment())
                .commit()
        }

        mediaSwitch.setOnClickListener {
            if (mediaSwitch.isChecked) {
                sharedPreferences.edit().putBoolean("isMovieMode", true).apply()
                media.text = getString(R.string.movie)
            } else {
                sharedPreferences.edit().putBoolean("isMovieMode", false).apply()
                media.text = getString(R.string.tv_show)
            }
        }
    }

}