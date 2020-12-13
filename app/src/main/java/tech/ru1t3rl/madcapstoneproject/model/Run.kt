package tech.ru1t3rl.madcapstoneproject.model

import com.google.firebase.database.DataSnapshot
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.lang.Exception

class Run (snapshot: DataSnapshot?) : Serializable {
    var id = ""
    var routePoints = ArrayList<LatLng>()
    var distance = "0.0"
    var time: Long = 0
    var calories: Long = 0
    var score: Long = 0
    var averageSpeed = "0.0"
    var date = ""

    init {
        try {
            val data: HashMap<String, Any> = snapshot!!.value as HashMap<String, Any>

            id = snapshot.key ?: ""

                routePoints = data["routePoints"] as ArrayList<LatLng>
                distance = data["distance"] as String
                time = data["time"] as Long
                calories = data["calories"] as Long
                score = data["score"] as Long
                averageSpeed = data["aSpeed"] as String
                date = data["date"] as String

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}