package tech.ru1t3rl.madcapstoneproject.viewmodel

import android.util.Log
import com.google.firebase.database.*
import tech.ru1t3rl.madcapstoneproject.dao.UserDao
import tech.ru1t3rl.madcapstoneproject.model.User
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList

object UserModel : Observable(), UserDao {
    private var mValueDataListener: ValueEventListener? = null
    private var mUserList: List<User> = emptyList()

    private fun getDatabaseRef(): DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("User")
    }

    // Get All Users from the database and keep the list updated
    init {
        getDatabaseRef()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val users = ArrayList<User>()
                    for (userData: DataSnapshot in snapshot.children) {
                        try {
                            users.add(User(userData))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    mUserList =  users

                    setChanged()
                    notifyObservers()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("UserModel", p0.message)
            }
        })
    }

    // Find the user in the snapshot based on it's id
    override fun getUser(id: String) : User? {
        if(mUserList.isNullOrEmpty())
            getAllUsers()

        try {
            for (user in mUserList) {
                if (user.id == id)
                    return user
            }
        } catch (e: NullPointerException) {
            Log.e("UserRepository", "User with id $id not found!")
        }
        return User(null)
    }

    /**
     * Returns the id of the new user
     * @param user a new user which will be added to the database
     * @return the id of new user
     */
    override fun addUser(user: User): String {
        val newUser = getDatabaseRef()!!.child("").push()

        user.id = newUser.key.toString()
        newUser.child("username").setValue(user.username)
        newUser.child("totalScore").setValue(user.totalScore)
        newUser.child("totalTime").setValue(user.totalTime)
        newUser.child("totalDistance").setValue(user.totalDistance)
        newUser.child("runs").setValue(user.runs)
        newUser.child("private").setValue(user.private)
        newUser.child("averageSpeed").setValue(user.averageSpeed)
        newUser.child("profileImage").setValue(user.profileImagePath)

        return user.id
    }


    override fun updateUser(user: User) {
        val updatedUser = getDatabaseRef()!!.child(user.id)

        updatedUser.child("username").setValue(user.username)
        updatedUser.child("totalScore").setValue(user.totalScore)
        updatedUser.child("totalTime").setValue(user.totalTime)
        updatedUser.child("totalDistance").setValue(user.totalDistance)
        updatedUser.child("runs").setValue(user.runs)
        updatedUser.child("private").setValue(user.private)
        updatedUser.child("averageSpeed").setValue(user.averageSpeed)
        updatedUser.child("profileImage").setValue(user.profileImagePath)
    }

    // Get all users from the database
    override fun getAllUsers(): List<User> {
        return mUserList
    }
}