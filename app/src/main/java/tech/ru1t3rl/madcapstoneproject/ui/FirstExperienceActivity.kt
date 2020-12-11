package tech.ru1t3rl.madcapstoneproject.ui

import android.content.Context
import tech.ru1t3rl.madcapstoneproject.model.User
import tech.ru1t3rl.madcapstoneproject.R
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tech.ru1t3rl.madcapstoneproject.databinding.ActivityFirstExperienceBinding
import tech.ru1t3rl.madcapstoneproject.viewmodel.UserModel

var ARG_USER_ID = "ARG_USER_ID"

class FirstExperienceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstExperienceBinding
    private lateinit var mPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefs= application.getSharedPreferences(getString(R.string.user_id), Context.MODE_PRIVATE)

        // Check if the user already has an account
        if (!mPrefs.getString(getString(R.string.user_id), "").isNullOrEmpty()){
            ARG_USER_ID = mPrefs.getString(getString(R.string.user_id), "")!!
            launchMainActivity()
        }

        binding = ActivityFirstExperienceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            if(binding.etUsername.text.toString().isEmpty() && binding.etUsername.text.toString().isBlank())
                return@setOnClickListener

            createUser()
            launchMainActivity()
        }
    }

    private fun createUser() {
        val user = User(null)
        user.private = binding.sPrivate.isChecked
        user.username = binding.etUsername.text.toString()

        val id = UserModel.addUser(user)
        ARG_USER_ID = id

        val mEditor: SharedPreferences.Editor = mPrefs.edit()
        mEditor.putString(getString(R.string.user_id), id).apply()
    }

    private fun launchMainActivity () {
        startActivity(Intent(applicationContext, MapsActivity::class.java))
    }
}