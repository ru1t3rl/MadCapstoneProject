package tech.ru1t3rl.madcapstoneproject.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tech.ru1t3rl.madcapstoneproject.R
import tech.ru1t3rl.madcapstoneproject.model.User
import tech.ru1t3rl.madcapstoneproject.databinding.FragmentBattleBinding
import tech.ru1t3rl.madcapstoneproject.viewmodel.UserModel
import java.lang.NullPointerException

class BattleFragment : Fragment(){
    private lateinit var binding: FragmentBattleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBattleBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            setFriend(UserModel.getUser(arguments?.getString(ARG_FRIEND_ID)!!)!!)
            setUser(UserModel.getUser(ARG_USER_ID)!!)
        } catch (e: NullPointerException) {
            Snackbar.make(requireView(), getString(R.string.battle_failed_get_user), Snackbar.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun setFriend(friend: User) {
        binding.tvFriendName.text = friend.username
        binding.tvFTime.text = friend.totalTime.toString()
        binding.tvFDistance.text = friend.totalDistance
        binding.tvFSpeed.text = friend.averageSpeed
        binding.tvFScore.text = friend.totalScore.toString()
        loadImage(friend.profileImagePath, UserType.Friend)
    }

    private fun setUser(user: User) {
        binding.tvUName.text = user.username
        binding.tvUTime.text = user.totalTime.toString()
        binding.tvUDistance.text = user.totalDistance
        binding.tvUSpeed.text = user.averageSpeed
        binding.tvUScore.text = user.totalScore.toString()
        loadImage(user.profileImagePath, UserType.User)
    }

    private fun loadImage(profileImage: String, userType: UserType) {
        if (profileImage.isEmpty())
            return

        val storageReference = Firebase.storage.reference

        storageReference.child("${getString(R.string.profile_image_folder)}/$profileImage").downloadUrl.addOnSuccessListener {

        }.addOnFailureListener {
            // Handle any errors
        }

        storageReference.child("${getString(R.string.profile_image_folder)}/$profileImage").getBytes(Long.MAX_VALUE)
            .addOnSuccessListener {
                when(userType) {
                    UserType.Friend ->
                        binding.ivFriend.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
                    UserType.User ->
                        binding.ivUser.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
                }
            }.addOnFailureListener {
                // Handle any errors
            }
    }

    private enum class UserType {
        Friend,
        User
    }
}