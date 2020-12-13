package tech.ru1t3rl.madcapstoneproject.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.ru1t3rl.madcapstoneproject.R
import tech.ru1t3rl.madcapstoneproject.adapter.HistoryAdapter
import tech.ru1t3rl.madcapstoneproject.databinding.FragmentProfileBinding
import tech.ru1t3rl.madcapstoneproject.model.Run
import tech.ru1t3rl.madcapstoneproject.model.User
import tech.ru1t3rl.madcapstoneproject.viewmodel.RunModel
import tech.ru1t3rl.madcapstoneproject.viewmodel.UserModel
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment(), Observer {
    private lateinit var binding:  FragmentProfileBinding

    private var runs = ArrayList<Run>()
    private val historyAdapter = HistoryAdapter(runs)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = UserModel.getUser(ARG_USER_ID)
        binding.swPrivate.isChecked = user!!.private

        setup(user)
    }

    private fun setup(user: User) {
        binding.tvName.text = user.username
        binding.swPrivate.setOnCheckedChangeListener { _, isChecked ->
            user.private = isChecked

            CoroutineScope(Dispatchers.IO).launch {
                UserModel.updateUser(user)
            }
        }

        loadImage(user.profileImagePath)

        CoroutineScope(Dispatchers.Main).launch {
            initRv()

            loadRuns(user)
        }

        UserModel.addObserver(this)
    }

    private fun initRv() {
        binding.rvHistory.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        binding.rvHistory.adapter = historyAdapter
    }

    private fun loadImage(profileImage: String) {
        if (profileImage.isEmpty())
            return

        val storageReference = Firebase.storage.reference

        storageReference.child("${getString(R.string.profile_image_folder)}/$profileImage").downloadUrl.addOnSuccessListener {

        }.addOnFailureListener {
            // Handle any errors
        }

        storageReference.child("${getString(R.string.profile_image_folder)}/$profileImage").getBytes(Long.MAX_VALUE)
            .addOnSuccessListener {
                binding.ivProfile.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }.addOnFailureListener {
                // Handle any errors
            }
    }



    private fun loadRuns(user: User) {

            for (runId in user.runs!!) {
                try {
                    runs.add(RunModel.getRun(runId)!!)
                } catch (e: NullPointerException) {

                }
            }

            historyAdapter.notifyDataSetChanged()

        if(runs.size > 0)
            binding.tvNoRuns.visibility = View.GONE
        else
            binding.rvHistory.visibility = View.GONE

    }

    override fun update(o: Observable?, arg: Any?) {
        val user = UserModel.getUser(ARG_USER_ID) ?: return

        runs.clear()
        loadRuns(user)
    }
}