package tech.ru1t3rl.madcapstoneproject.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tech.ru1t3rl.madcapstoneproject.R
import tech.ru1t3rl.madcapstoneproject.adapter.UserAdapter
import tech.ru1t3rl.madcapstoneproject.databinding.FragmentFindFriendsFragmentsBinding
import tech.ru1t3rl.madcapstoneproject.model.User
import tech.ru1t3rl.madcapstoneproject.repository.UserRepository
import java.util.*
import kotlin.collections.ArrayList

const val ARG_FRIEND_ID = "ARG_FRIEND_ID"

class FindFriendsFragments : Fragment() {
    private lateinit var binding: FragmentFindFriendsFragmentsBinding

    private var users = ArrayList<User>()
    private var searchItems = ArrayList<User>()

    private var userAdapter = UserAdapter(searchItems) {
        portal: User -> loadBattleFragment(portal)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindFriendsFragmentsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()

        binding.etSearchTerm.doAfterTextChanged {
            search(it.toString())
        }
    }

    private fun initRv() {
        binding.rvFriends.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        binding.rvFriends.adapter = userAdapter

        addAllUser()
        userAdapter.notifyDataSetChanged()
    }

    private fun search(searchTerm: String) {
        searchItems.clear()
        for(user in users) {
            if(user.username.toLowerCase(Locale.ROOT).contains(searchTerm.toLowerCase(Locale.ROOT))) {
                searchItems.add(user)
            }
        }

        userAdapter.notifyDataSetChanged()
    }

    private fun loadBattleFragment(user: User) {
        val args = Bundle()
        args.putString(ARG_FRIEND_ID, user.id)

        findNavController().navigate(R.id.battleFragment, args)
    }

    private fun addAllUser() {
        users.clear()
        users.addAll(UserRepository.getAllUsers())

        for(i in (0 until users.size).reversed()){
            if(users[i].private || users[i].id == ARG_USER_ID) {
                users.remove(users[i])
            }
        }

        searchItems.addAll(users)
    }
}
