package nz.ac.uclive.ajs418.quickfire

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import nz.ac.uclive.ajs418.quickfire.databinding.ActivityMainBinding
import nz.ac.uclive.ajs418.quickfire.fragments.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    // Initializes the four fragments
    private val homeFragment: Fragment = HomeFragment()
    private val connectFragment: Fragment = ConnectFragment()
    private val playFragment: Fragment = PlayFragment()
    private val matchesFragment: Fragment = MatchesFragment()
    private val partyDetailsFragment: Fragment = PartyDetailsFragment()
    private val settingsFragment: Fragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean("isNightMode", false)

        // Set the theme based on the saved preference
        if (isDarkTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Inflate the layout using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = binding.bottomNavigation // Initialize the BottomNavigationView

        replaceFragment(homeFragment)    // Set the initial fragment to ConnectFragment

        // Handle navigation item selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.connectFragment -> replaceFragment(homeFragment)
                R.id.playFragment -> replaceFragment(playFragment)
                R.id.matchesFragment -> replaceFragment(matchesFragment)
                R.id.settingsFragment -> replaceFragment(settingsFragment)
            }
            true
        }

    }

    // Function to replace the fragment in the fragment container
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

}