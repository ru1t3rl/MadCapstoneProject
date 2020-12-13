package tech.ru1t3rl.madcapstoneproject.viewmodel

import android.util.Log
import com.google.firebase.database.*
import tech.ru1t3rl.madcapstoneproject.dao.RunDao
import tech.ru1t3rl.madcapstoneproject.model.Run
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList

object RunModel: RunDao, Observable() {
    private var mRunList: List<Run> = emptyList()

    private fun getDatabaseRef(): DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("Run")
    }


    init {
         getDatabaseRef()?.addValueEventListener(object : ValueEventListener {
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
        })
    }

    // Find the user in the snapshot based on it's id
    override fun getRun(id: String) : Run? {
        if(mRunList.isNullOrEmpty())
            getAllRuns()

        try {
            for (run in mRunList) {
                if (run.id == id)
                    return run
            }
        } catch (e: NullPointerException) {
            Log.e("RunModel", "Run with id $id not found!")
        }

        return null
    }

    /**
     * Add users to the database
     * @return Returns the id of the newly added run
     */
    override fun addRun(run: Run) : String {
        val newRun = getDatabaseRef()!!.child("").push()

        newRun.child("routePoints").setValue(run.routePoints)
        newRun.child("distance").setValue(run.distance)
        newRun.child("time").setValue(run.time)
        newRun.child("calories").setValue(run.calories)
        newRun.child("score").setValue(run.score)
        newRun.child("aSpeed").setValue(run.averageSpeed)
        newRun.child("date").setValue(run.date)

        return newRun.key ?: ""
    }

    // Get all users from the database
    override fun getAllRuns(): List<Run> {
        return mRunList
    }
}