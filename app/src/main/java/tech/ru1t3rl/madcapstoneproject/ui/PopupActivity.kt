package tech.ru1t3rl.madcapstoneproject.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import tech.ru1t3rl.madcapstoneproject.R
import tech.ru1t3rl.madcapstoneproject.databinding.ActivityPopupBinding

class PopupActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPopupBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)
        //setNavigation()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

//         Handle action bar item clicks here. The action bar will
//         automatically handle clicks on the Home/Up button, so long
//         as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
                R.id.miFreinds -> {
                    navController.navigate(R.id.findFriendsFragments)
                    return true
                }
                R.id.miLeaderboard -> {
                    navController.navigate(R.id.leaderboardFragment)
                    return true
                }
                R.id.miSettings -> {
                    navController.navigate(R.id.settingsFragment)
                    return true
                }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setNavigation() {
        val navView: BottomNavigationView = binding.bottomNav

        navView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.miFreinds -> {
                    navController.navigate(R.id.findFriendsFragments)
                }
                R.id.miLeaderboard -> {
                    navController.navigate(R.id.leaderboardFragment)
                }
                R.id.miSettings -> {
                    navController.navigate(R.id.settingsFragment)
                }
            }
            true
        }
    }
}