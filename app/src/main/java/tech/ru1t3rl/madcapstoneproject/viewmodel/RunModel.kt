package tech.ru1t3rl.madcapstoneproject.viewmodel

import android.util.Log
import com.google.firebase.database.*
import tech.ru1t3rl.madcapstoneproject.dao.RunDao
import tech.ru1t3rl.madcapstoneproject.model.Run
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

object RunModel: RunDao, Observable() {
    private var mValueDataListener: ValueEventListener? = null
    private var mRunList: List<Run> = emptyList()

    private fun getDatabaseRef(): DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("Run")
    }


    init {
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

                    setChanged()
                    notifyObservers()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("RunModel", p0.message)
            }
        }
    }

    // Find the user in the snapshot based on it's id
    override fun getRun(id: String) : Run {
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
    override fun addRun(run: Run) {
        val newRun = getDatabaseRef()!!.child("").push()

        newRun.child("routePoints").setValue(run.routePoints)
        newRun.child("distance").setValue(run.distance)
        newRun.child("time").setValue(run.time)
        newRun.child("calories").setValue(run.time)
        newRun.child("score").setValue(run.score)
        newRun.child("aSpeed").setValue(run.averageSpeed)
    }

    // Get all users from the database
    override fun getAllRuns(): List<Run> {
        return mRunList
    }
}