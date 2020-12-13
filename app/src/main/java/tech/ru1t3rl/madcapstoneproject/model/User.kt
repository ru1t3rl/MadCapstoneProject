package tech.ru1t3rl.madcapstoneproject.model

import com.google.firebase.database.DataSnapshot
import java.lang.Exception
import kotlin.properties.Delegates

class User(snapshot: DataSnapshot?) {
    lateinit var id: String
    lateinit var username: String
    var totalScore: Long = 0
    var totalTime: Long = 0
    var totalDistance = "0.0"
    var runs: List<String>? = null
    var private: Boolean by Delegates.notNull()
    lateinit var profileImagePath: String
    lateinit var averageSpeed: String

    init {
        try {
            val data: HashMap<String, Any> = snapshot!!.value as HashMap<String, Any>

            id = snapshot.key ?: ""
            username = data["username"] as String
            totalScore = data["totalScore"] as Long
            totalTime = data["totalTime"] as Long
            totalDistance = data["totalDistance"] as String
            averageSpeed = data["averageSpeed"] as String
            runs = data["runs"] as List<String>?
            private = data["private"] as Boolean
            profileImagePath = data["profileImage"] as String
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}