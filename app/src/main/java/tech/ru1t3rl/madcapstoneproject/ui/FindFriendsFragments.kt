package tech.ru1t3rl.madcapstoneproject.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.ru1t3rl.madcapstoneproject.R
import tech.ru1t3rl.madcapstoneproject.adapter.UserAdapter
import tech.ru1t3rl.madcapstoneproject.databinding.FragmentFindFriendsFragmentsBinding
import tech.ru1t3rl.madcapstoneproject.model.User
import tech.ru1t3rl.madcapstoneproject.viewmodel.UserModel
import java.util.*
import kotlin.collections.ArrayList

const val ARG_FRIEND_ID = "ARG_FRIEND_ID"

class FindFriendsFragments : Fragment(), Observer{
    private lateinit var binding: FragmentFindFriendsFragmentsBinding

    private var users = ArrayList<User>(UserModel.getAllUsers())
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

        CoroutineScope(Dispatchers.Main).launch {
            initRv()

            withContext(Dispatchers.IO) {
                addAllUser()
            }

            userAdapter.notifyDataSetChanged()
        }

        binding.etSearchTerm.doAfterTextChanged {
            search(it.toString())
        }

        UserModel.addObserver(this)
    }

    private fun initRv() {
        binding.rvFriends.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        binding.rvFriends.adapter = userAdapter
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
        users.addAll(UserModel.getAllUsers() )

        for(user in users.reversed()){
            if(user.private || user.id == ARG_USER_ID) {
                users.remove(user)
            }
        }

        searchItems.clear()
        searchItems.addAll(users)
    }

    override fun update(o: Observable?, arg: Any?) {
        this.users.clear()

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                addAllUser()
            }

            userAdapter.notifyDataSetChanged()
            search(binding.etSearchTerm.text.toString())
        }
    }
}
