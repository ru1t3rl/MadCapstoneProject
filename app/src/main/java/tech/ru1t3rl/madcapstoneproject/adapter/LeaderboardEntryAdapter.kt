package tech.ru1t3rl.madcapstoneproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.ru1t3rl.madcapstoneproject.R
import tech.ru1t3rl.madcapstoneproject.databinding.ItemLeaderboardEntryBinding
import tech.ru1t3rl.madcapstoneproject.databinding.ItemUserBinding
import tech.ru1t3rl.madcapstoneproject.model.User

class LeaderboardEntryAdapter(
    private val users: List<User>
) : RecyclerView.Adapter<LeaderboardEntryAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemLeaderboardEntryBinding.bind(itemView)

        fun databind(user: User, position: Int) {
            binding.tvName.text = user.username
            binding.tvPosition.text = (position + 1).toString()
            binding.tvScore.text = user.totalScore.toString()
        }
    }

    /**
     * Creates and returns a ViewHolder object, inflating a standard layout called simple_list_item_1.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_leaderboard_entry,
                parent,
                false
            )
        )
    }

    /**
     * Returns the size of the list
     */
    override fun getItemCount(): Int {
        return users.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.databind(users[position], position)
    }
}