package tech.ru1t3rl.madcapstoneproject.model

import com.google.firebase.database.DataSnapshot
import java.lang.Exception
import java.lang.NullPointerException
import kotlin.properties.Delegates

class User(snapshot: DataSnapshot?) {
    lateinit var id: String
    lateinit var username: String
    var totalScore: Long = 0
    var totalTime: Long = 0
    var totalDistance = "0.0"
    var runs: ArrayList<String>? = null
    var private: Boolean by Delegates.notNull()
    var profileImagePath: String = ""
    var averageSpeed: String = "0.0"

    init {
        try {
            if(snapshot != null) {
                val data: HashMap<String, Any> = snapshot.value as HashMap<String, Any>

                id = snapshot.key ?: ""
                username = data["username"] as String
                totalScore = data["totalScore"] as Long
                totalTime = data["totalTime"] as Long
                totalDistance = data["totalDistance"] as String
                averageSpeed = data["averageSpeed"] as String
                runs = data["runs"] as ArrayList<String>?
                private = data["private"] as Boolean

                profileImagePath = try {
                    data["profileImage"] as String
                } catch (e: NullPointerException) {
                    ""
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}