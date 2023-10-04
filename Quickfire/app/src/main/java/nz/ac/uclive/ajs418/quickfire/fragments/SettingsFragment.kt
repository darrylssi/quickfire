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

    private lateinit var editor: Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switch: SwitchCompat = view.findViewById(R.id.themeSwitch)
        val theme: TextView = view.findViewById(R.id.themeText)
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE)
        val nightMode: Boolean = sharedPreferences.getBoolean("nightMode", false)

        if (nightMode) {
            switch.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            theme.text = getString(R.string.dark)
        }

        switch.setOnClickListener {
            if (nightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor = sharedPreferences.edit()
                editor.putBoolean("nightMode", false)
                theme.text = getString(R.string.light)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor = sharedPreferences.edit()
                editor.putBoolean("nightMode", true)
                theme.text = getString(R.string.dark)
            }
            editor.apply()
        }
    }

}