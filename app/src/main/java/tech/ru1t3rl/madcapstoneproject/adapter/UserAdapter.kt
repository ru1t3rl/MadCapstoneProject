package tech.ru1t3rl.madcapstoneproject.adapter

import android.graphics.BitmapFactory
import android.provider.Settings.Global.getString
import tech.ru1t3rl.madcapstoneproject.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            itemView.setOnClickListener { clickListener(user) }
            binding.btnLoad.setOnClickListener { clickListener(user) }

            CoroutineScope(Dispatchers.Main).launch {
                loadImage(user.profileImagePath)
            }
        }

        private fun loadImage(profileImage: String) {
            if (profileImage.isEmpty())
                return

            val storageReference = Firebase.storage.reference

            storageReference.child("profile_pictures/$profileImage").downloadUrl.addOnSuccessListener {

            }.addOnFailureListener {
                // Handle any errors
            }

            storageReference.child("profile_pictures/$profileImage").getBytes(Long.MAX_VALUE)
                .addOnSuccessListener {
                    binding.ivProfile.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
                }.addOnFailureListener {
                    // Handle any errors
                }
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