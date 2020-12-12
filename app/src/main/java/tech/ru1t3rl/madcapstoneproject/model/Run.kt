package tech.ru1t3rl.madcapstoneproject.model

import com.google.firebase.database.DataSnapshot
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class Run (snapshot: DataSnapshot?) : Serializable {
    var id: String
    var routePoints = ArrayList<LatLng>()
    var distance = "0.0"
    var time = 0
    var calories = 0
    var score = 0
    var averageSpeed = "0.0"

    init {
            val data = if(snapshot != null)
                snapshot.value as HashMap<String, Any>
                else
                    HashMap()

            id = snapshot?.key ?: ""

            if(data.size > 0) {
                routePoints = data["routePoints"] as ArrayList<LatLng>
                distance = data["distance"] as String
                time = data["time"] as Int
                calories = data["calories"] as Int
                score = data["score"] as Int
                averageSpeed = data["aSpeed"] as String
            }
    }
}