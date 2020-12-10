package tech.ru1t3rl.madcapstoneproject.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tech.ru1t3rl.madcapstoneproject.adapter.UserAdapter
import tech.ru1t3rl.madcapstoneproject.databinding.FragmentFindFriendsFragmentsBinding
import tech.ru1t3rl.madcapstoneproject.model.User
import tech.ru1t3rl.madcapstoneproject.repository.UserRepository
import java.util.*
import kotlin.collections.ArrayList

class FindFriendsFragments : Fragment() {
    private lateinit var binding: FragmentFindFriendsFragmentsBinding

    private var users = ArrayList<User>()
    private var searchItems = ArrayList<User>()
    private var userIds = ArrayList<String>()

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
            Log.i("tag","Searched")
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
        // TODO Load Next Fragment and pass trough the user
    }

    private fun addAllUser() {
        users.addAll(UserRepository.getAllUsers()!!)
        Log.i("Tag", users.toString())

        for(user in users){
            if(user.private || user.id == ARG_USER_ID) {
                users.remove(user)
            } else
                userIds.add(user.id)
        }

        searchItems.addAll(users)
    }
}
