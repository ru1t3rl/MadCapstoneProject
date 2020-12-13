package tech.ru1t3rl.madcapstoneproject.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import tech.ru1t3rl.madcapstoneproject.model.User
import tech.ru1t3rl.madcapstoneproject.R
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.ImageDecoder.createSource
import android.graphics.ImageDecoder.decodeBitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage


import tech.ru1t3rl.madcapstoneproject.databinding.ActivityFirstExperienceBinding
import tech.ru1t3rl.madcapstoneproject.viewmodel.UserModel
import java.io.IOException
import java.util.*

var ARG_USER_ID = "ARG_USER_ID"

class FirstExperienceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstExperienceBinding
    private lateinit var mPrefs: SharedPreferences

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var storage = FirebaseStorage.getInstance()
    private var imageRef: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefs= application.getSharedPreferences(getString(R.string.user_id), Context.MODE_PRIVATE)

        // Check if the user already has an account
        if (!mPrefs.getString(getString(R.string.user_id), "").isNullOrEmpty()){
            ARG_USER_ID = mPrefs.getString(getString(R.string.user_id), "")!!

            if(UserModel.getUser(ARG_USER_ID) != null) {
                // launchMainActivity()
            }
        }

        binding = ActivityFirstExperienceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivLogo.setOnClickListener {
            chooseImage()
        }

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

        uploadImage()
        user.profileImagePath = imageRef!!
    }

    private fun uploadImage() {
        if (filePath == null)
            return

        val progressDialog = ProgressDialog(applicationContext)
        progressDialog.setTitle(getString(R.string.first_creating_profile))
        progressDialog.show()

        // Give the image a random and set it's firebase reference
        imageRef = UUID.randomUUID().toString() + ".bmp"
        val ref = storage.reference.child("profile_pictures/$imageRef")

        // Upload the image to firebase
        ref.putFile(filePath!!)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Snackbar.make(binding.root, R.string.first_profile_created, Snackbar.LENGTH_INDEFINITE).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Snackbar.make(binding.root, "${R.string.first_profile_failed} ${e.message}", Snackbar.LENGTH_INDEFINITE).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                    .totalByteCount
                progressDialog.setMessage("${R.string.first_profile_created} ${progress.toInt()}%")
            }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.first_select_image)), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            filePath = data.data
            try {
                val bitmap = if (android.os.Build.VERSION.SDK_INT >= 29)
                    decodeBitmap(createSource(applicationContext.contentResolver, filePath!!))
                else
                    MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, filePath!!)

                binding.ivLogo.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun launchMainActivity () {
        startActivity(Intent(applicationContext, MapsActivity::class.java))
    }
}