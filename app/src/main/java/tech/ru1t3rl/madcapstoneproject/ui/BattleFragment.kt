package tech.ru1t3rl.madcapstoneproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import tech.ru1t3rl.madcapstoneproject.R
import tech.ru1t3rl.madcapstoneproject.model.User
import tech.ru1t3rl.madcapstoneproject.databinding.FragmentBattleBinding
import tech.ru1t3rl.madcapstoneproject.repository.UserRepository
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
            setFriend(UserRepository.getUser(arguments?.getString(ARG_FRIEND_ID)!!)!!)
            setUser(UserRepository.getUser(ARG_USER_ID)!!)
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
    }

    private fun setUser(user: User) {
        binding.tvUName.text = user.username
        binding.tvUTime.text = user.totalTime.toString()
        binding.tvUDistance.text = user.totalDistance
        binding.tvUSpeed.text = user.averageSpeed
        binding.tvUScore.text = user.totalScore.toString()
    }
}