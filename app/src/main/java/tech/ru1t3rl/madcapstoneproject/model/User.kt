package tech.ru1t3rl.madcapstoneproject.model

import com.google.firebase.database.DataSnapshot
import java.lang.Exception

class User(snapshot: DataSnapshot?) {
    lateinit var id: String
    lateinit var username: String
    var totalScore = 0
    var totalTime = 0
    var totalDistance = 0f
    var runs: List<String>? = null

    init {
        try {
            val data: HashMap<String, Any> = snapshot!!.value as HashMap<String, Any>

            id = snapshot.key ?: ""
            username = data["username"] as String
            totalScore = data["totalScore"] as Int
            totalTime = data["totalTime"] as Int
            totalDistance = data["totalDistance"] as Float
            runs = data["runs"] as List<String>?
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}