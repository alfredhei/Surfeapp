package com.example.surfeapp

import android.location.Location
import android.location.LocationManager
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import kotlin.math.absoluteValue

class DataSource {
//CLIENT ID FROST: af800469-bcec-450b-95c7-d7944ca2b73b
//CLIENT SECRET FROST: 0f39f1cf-033e-43a5-9602-f5855725a638

    public fun getConditions(spot:Surfespot):Conditions{
        // BESKRIVELSE
        // Når du bruker Surfespot.getConditions så kaller den egentlig bare på denne

        // LEGGE TIL ASYNKRON GET MED FUEL HER <------------
        var url = "https://in2000-apiproxy.ifi.uio.no/weatherapi/oceanforecast/2.0/complete?"

        url += "lat=" + spot.coordinates.latitude.toString()
        url += "&lon=" + spot.coordinates.longitude.toString()

        val gson = Gson()
        var conditions:Conditions = Conditions(0.0F, 0.0F, 0.0F)
        runBlocking {
            try {
                println(Fuel.get(url).awaitString())
                val response = gson.fromJson(Fuel.get(url).awaitString(), Base::class.java)
                println(response.toString())
                val wavesize:Float? = response.properties?.timeseries?.get(0)?.data?.instant?.details?.sea_surface_wave_height
                val currentspeed:Float? = response.properties?.timeseries?.get(0)?.data?.instant?.details?.sea_water_speed
                val currentdirection:Float? = response.properties?.timeseries?.get(0)?.data?.instant?.details?.sea_water_to_direction
                conditions = Conditions(wavesize, currentspeed, currentdirection)
            } catch(exception: Exception) {
                println("A network request exception was thrown: ${exception.message}")
            }
        }
        println(conditions.toString())
        return conditions
    }
    public fun getSpots():Spots{
        // BESKRIVELSE
        // Kall på denne funksjonen for å få en liste med alle surfespot-objekte

        val coordinates:Coordinates = Coordinates(58.8849857, 5.60265)

        val eksempelSpot:Surfespot = Surfespot(0, "Solastranden", coordinates, "Et eksempel på en beskrivelse.")

        val spots:Spots = Spots(listOf<Surfespot>(eksempelSpot))

        return spots
    }
}

// result generated from /json

data class Base(val type: String?, val geometry: Geometry?, val properties: Properties?)

data class Data(val instant: Instant?)

data class Details(val sea_surface_wave_from_direction: Float?, val sea_surface_wave_height: Float?, val sea_water_speed: Float?, val sea_water_temperature: Float?, val sea_water_to_direction: Float?)

data class Geometry(val type: String?, val coordinates: List<Number>?)

data class Instant(val details: Details?)

data class Meta(val updated_at: String?, val units: Units?)

data class Properties(val meta: Meta?, val timeseries: List<Timeseries139117139>?)

data class Timeseries139117139(val time: String?, val data: Data?)

data class Units(val sea_surface_wave_from_direction: String?, val sea_surface_wave_height: String?, val sea_water_speed: String?, val sea_water_temperature: String?, val sea_water_to_direction: String?)