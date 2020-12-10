package tech.ru1t3rl.madcapstoneproject.model

import com.google.firebase.database.DataSnapshot
import com.google.type.LatLng

class Run (snapshot: DataSnapshot) {
    var id: String
    var routePoints: List<LatLng>
    var distance = 0f
    var time = 0
    var calories = 0
    var score = 0
    var averageSpeed = 0f

    init {
        val data: HashMap<String, Any> = snapshot!!.value as HashMap<String, Any>

        id = snapshot.key ?: ""
        routePoints = data["routePoints"] as List<LatLng>
        distance = data["distance"] as Float
        time = data["time"] as Int
        calories = data["calories"] as Int
        score = data["score"] as Int
        averageSpeed = data["aSpeed"] as Float
    }

}