package tech.ru1t3rl.madcapstoneproject.ui

import android.os.Bundle
import android.os.UserManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.ru1t3rl.madcapstoneproject.R
import tech.ru1t3rl.madcapstoneproject.adapter.LeaderboardEntryAdapter
import tech.ru1t3rl.madcapstoneproject.databinding.FragmentLeaderboardBinding
import tech.ru1t3rl.madcapstoneproject.model.User
import tech.ru1t3rl.madcapstoneproject.viewmodel.UserModel
import java.util.*
import kotlin.collections.ArrayList

class LeaderboardFragment : Fragment(), Observer {
    private lateinit var binding: FragmentLeaderboardBinding

    private var users = UserModel.getAllUsers() as ArrayList
    private var entryAdapter = LeaderboardEntryAdapter(users)

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaderboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()

        ioScope.launch {
            sortUsers()
        }

        entryAdapter.notifyDataSetChanged()
    }

    override fun update(o: Observable?, arg: Any?) {
        users.clear()
        users.addAll(UserModel.getAllUsers())

        ioScope.launch {
            sortUsers()
        }

        entryAdapter.notifyDataSetChanged()
    }

    private fun initRv() {
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        binding.rvLeaderboard.adapter = entryAdapter
    }

    private fun sortUsers() {
        val sorted = ArrayList<User>()

        if (users.isNullOrEmpty()) {
            users = ArrayList()
            users.addAll(UserModel.getAllUsers())
        }

        for (user in users) {
            if (!user.private || user.id == ARG_USER_ID) {
                sorted.add(user)

                for (i in 1 until sorted.size) {
                    if (sorted[i].totalScore < user.totalScore) {
                        sorted[i + 1] = sorted[i]
                    } else {
                        sorted[i] = user
                        break
                    }
                }
            }
        }

        users = sorted
    }
}