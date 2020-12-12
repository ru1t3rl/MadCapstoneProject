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

    private var users = ArrayList<User>(UserModel.getAllUsers())
    private var leaderbord = ArrayList<User>()
    private var entryAdapter = LeaderboardEntryAdapter(leaderbord)

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

        CoroutineScope(Dispatchers.Main).launch {
            initRv()

            withContext(Dispatchers.IO) {
                sortUsers()
            }

            entryAdapter.notifyDataSetChanged()
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        users.clear()
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                sortUsers()
            }

            initRv()
            entryAdapter.notifyDataSetChanged()
        }
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
        leaderbord.clear()

        if (users.isNullOrEmpty()) {
            users = ArrayList(UserModel.getAllUsers())
        }

        for (user in users.reversed()) {
            if (!user.private || user.id == ARG_USER_ID) {
                leaderbord.add(user)

                Log.i("leader", "Checking ${user.username} Score: ${user.totalScore}")
                for (i in (0 until leaderbord.size - 1)) {
                    if (user.totalScore > leaderbord[i].totalScore && i + 1 < leaderbord.size) {
                        leaderbord[i + 1] = leaderbord[i]
                        leaderbord[i] = user
                    }
                }

                Log.i("leaderboard", leaderbord.toString())
            } else
                users.remove(user)
        }
    }
}