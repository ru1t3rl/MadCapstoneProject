package tech.ru1t3rl.madcapstoneproject.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.ru1t3rl.madcapstoneproject.databinding.FragmentProfileBinding
import tech.ru1t3rl.madcapstoneproject.viewmodel.UserModel

class ProfileFragment : Fragment() {
    private lateinit var binding:  FragmentProfileBinding
    val user = UserModel.getUser(ARG_USER_ID)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swPrivate.isChecked = user!!.private

        setup()
    }

    private fun setup() {
        binding.tvName.text = user!!.username
        binding.swPrivate.setOnCheckedChangeListener { _, isChecked ->
            user.private = isChecked

            CoroutineScope(Dispatchers.IO).launch {
                UserModel.updateUser(user)
            }
        }
    }
}