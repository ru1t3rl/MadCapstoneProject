package tech.ru1t3rl.madcapstoneproject.repository

import android.util.Log
import com.google.firebase.database.*
import tech.ru1t3rl.madcapstoneproject.model.Run
import java.lang.Exception

class RunRepository {
    private var mValueDataListener: ValueEventListener? = null
    private var mRunList: ArrayList<Run>? = ArrayList()

    private fun getDatabaseRef(): DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("Run")
    }


    // Find the user in the snapshot based on it's id
    fun getRun(id: String) : Run {
        if (mValueDataListener != null) {
            getDatabaseRef()?.removeEventListener(mValueDataListener!!)
        }
        mValueDataListener = null

        var run: Run? = null
        mValueDataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    run = Run(snapshot.child(id))
                } catch (e: Exception) {
                    e.stackTrace
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("RunModel", p0.message)
            }
        }

        return run!!
    }

    // Add users to the database
    fun addRun(run: Run) {
        val newRun = getDatabaseRef()!!.child("").push()

        newRun.child("routePoints").setValue(run.routePoints)
        newRun.child("distance").setValue(run.distance)
        newRun.child("time").setValue(run.time)
        newRun.child("calories").setValue(run.time)
        newRun.child("score").setValue(run.score)
        newRun.child("aSpeed").setValue(run.averageSpeed)
    }

    // Get all users from the database
    fun getAllUsers(): ArrayList<Run>? {
        if (mValueDataListener != null) {
            getDatabaseRef()?.removeEventListener(mValueDataListener!!)
        }
        mValueDataListener = null

        mValueDataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val data: ArrayList<Run> = ArrayList()
                    for (runData: DataSnapshot in snapshot.children) {
                        try {
                            data.add(Run(runData))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    mRunList = data

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("RunModel", p0.message)
            }
        }

        return mRunList
    }
}