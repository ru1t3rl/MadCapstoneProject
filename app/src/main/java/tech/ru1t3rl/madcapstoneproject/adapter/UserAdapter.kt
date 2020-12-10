package tech.ru1t3rl.madcapstoneproject.adapter

import tech.ru1t3rl.madcapstoneproject.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.ru1t3rl.madcapstoneproject.databinding.ItemUserBinding
import tech.ru1t3rl.madcapstoneproject.model.User

class UserAdapter(
    private val users: List<User>,
    private val clickListener: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemUserBinding.bind(itemView)

        fun databind(user: User, clickListener: (User) -> Unit) {
            binding.tvName.text = user.username
            binding.btnLoad.setOnClickListener { clickListener(user) }
        }
    }

    /**
     * Creates and returns a ViewHolder object, inflating a standard layout called simple_list_item_1.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user,
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
        holder.databind(users[position], clickListener)
    }
}