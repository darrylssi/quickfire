package nz.ac.uclive.ajs418.quickfire

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nz.ac.uclive.ajs418.quickfire.databinding.ActivityMainBinding
import nz.ac.uclive.ajs418.quickfire.entity.User
import nz.ac.uclive.ajs418.quickfire.fragments.*
import nz.ac.uclive.ajs418.quickfire.viewmodel.LikeViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.LikeViewModelFactory
import nz.ac.uclive.ajs418.quickfire.viewmodel.MediaViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.MediaViewModelFactory
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModelFactory
import nz.ac.uclive.ajs418.quickfire.viewmodel.UserViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.UserViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    // Initializes the four fragments
    private val homeFragment: Fragment = HomeFragment()
    private val clientConnectFragment: Fragment = ClientConnectFragment()
    private val playFragment: Fragment = PlayFragment()
    private val matchesFragment: Fragment = MatchesFragment()
    private val partyDetailsFragment: Fragment = PartyDetailsFragment()
    private val settingsFragment: Fragment = SettingsFragment()

    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this, UserViewModelFactory((application as QuickfireApplication).userRepository))
            .get(UserViewModel::class.java)
    }
    private val partyViewModel: PartyViewModel by lazy {
        ViewModelProvider(this, PartyViewModelFactory((application as QuickfireApplication).partyRepository))
            .get(PartyViewModel::class.java)
    }
    private val likeViewModel: LikeViewModel by lazy {
        ViewModelProvider(this, LikeViewModelFactory((application as QuickfireApplication).likeRepository))
            .get(LikeViewModel::class.java)
    }
    private val mediaViewModel: MediaViewModel by lazy {
        ViewModelProvider(this, MediaViewModelFactory((application as QuickfireApplication).mediaRepository))
            .get(MediaViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean("isNightMode", false)

        // Set the theme based on the saved preference
        if (isDarkTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        userViewModel.users.observe(this, Observer { users ->
            users?.let {
                Log.d("MainActivity", "Users: $users")

//                if (!users.isNullOrEmpty()) {
//                    val length = users.size
//                    Log.d("MainActivity", "Array Length: $length")
//                    val firstUserName = users[0].name
//                    val firstType = users[0].bluetoothType
//                    Log.d("MainActivity", "First User Name: $firstUserName")
//                    Log.d("MainActivity", "First Type: $firstType")
//                    val secUserName = users[1].name
//                    val secType = users[1].bluetoothType
//                    Log.d("MainActivity", "Second User Name: $secUserName")
//                    Log.d("MainActivity", "Second User Type: $secType")
//                }
            }
        })

        partyViewModel.parties.observe(this, Observer { parties ->
            parties?.let {
                Log.d("MainActivity", "Parties: $parties")
            }
        })

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

    fun getUserViewModelInstance(): UserViewModel {
        return userViewModel
    }

    fun getPartyViewModelInstance(): PartyViewModel {
        return partyViewModel
    }

    fun getLikeViewModelInstance(): LikeViewModel {
        return likeViewModel
    }

    fun getMediaViewModelInstance(): MediaViewModel {
        return mediaViewModel
    }
}