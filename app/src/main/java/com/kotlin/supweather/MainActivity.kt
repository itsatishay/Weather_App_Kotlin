package com.kotlin.supweather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_brief_info.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var weather : WeatherData
    private lateinit var locationProviderClient: FusedLocationProviderClient
    private lateinit var userLocation: Location
    var userLocationInitialized : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setTodayDate()
        getLocation()
        humidityBrief.iconInfo.setImageResource(R.drawable.ic_humidity_icon)
        humidityBrief.topicTextInfo.text = "Humidity"
        cloudBrief.iconInfo.setImageResource(R.drawable.ic_cloud_icon)
        cloudBrief.topicTextInfo.text = "Cloudy"
        refreshButton.setOnClickListener {
            if(userLocationInitialized && userLocation.longitude != 0.0){
                callWeatherApi(userLocation.latitude, userLocation.longitude)
            }else{
                getLocation()
            }
        }
    }

    // method to initialize url for weather API call
    private fun callWeatherApi(lat: Double, lon: Double){
        val apiKey : String = "__Type your api key here___"
        val url : String = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$apiKey"
        newApiCall(url)
    }

    // api call method
    private fun newApiCall(url: String) {
        ApiCall(url).fetchJson(object: OnRequestCompleteListener {
            override fun onSuccess(response: String) {
                println("onSuccess: $response")
                setWeatherData(response)
            }

            override fun onError(error: String) {
                println("onError: $error")
            }
        })
    }

    // set/update the obtained weather data in the UI
    fun setWeatherData(weatherJson: String){
        if(weatherJson != ""){
            weather = Gson().fromJson(weatherJson, WeatherData::class.java)
            runOnUiThread {
                cityText.text = weather.name
                currentWeatherTypeText.text = weather.weather[0].main
                currentWeatherCelsius.text = "${kelvinToCelsius(weather.main.temp)}"
                humidityBrief.textInfo.text = "${weather.main.humidity}%"
                windBrief.textInfo.text = "${weather.wind.speed}km/h"
                cloudBrief.textInfo.text = "${weather.clouds.all}%"
                changeCurrentWeatherIcon(weather.weather[0].main)
            }
        }
    }

    // change the main image/icon that shows the current weather type
    private fun changeCurrentWeatherIcon(weatherType: String){
        when(weatherType){
            "Clouds" -> currentWeatherIcon.setImageResource(R.drawable.ic_iclouds)
            "Clear" -> currentWeatherIcon.setImageResource(R.drawable.ic_sun_icon)
            "Thunderstorm" -> currentWeatherIcon.setImageResource(R.drawable.thunderstorm_icon)
            "Drizzle" -> currentWeatherIcon.setImageResource(R.drawable.ic_cludy)
            "Rain" -> currentWeatherIcon.setImageResource(R.drawable.ic_rain_icon)
            "Snow" -> currentWeatherIcon.setImageResource(R.drawable.snowflake)
            "Atmosphere" -> currentWeatherIcon.setImageResource(R.drawable.ic_cludy)
        }
    }

    private fun kelvinToCelsius(kelvin: Float) : Int{
        val celsius = kelvin - 273.15
        return celsius.toInt()
    }

    private fun setTodayDate(){
        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val days = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val currentDate = Calendar.getInstance()
        val dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        val dayString = "${days[dayOfWeek-1]}, ${months[month]} $day"
        todayDayText.text = dayString
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        // if permission is granted or not
        if(checkPermissions()){
            // if location service is enabled or not
            if(isLocationEnabled()){
                // get current location and call weather api
                locationProviderClient.lastLocation.addOnCompleteListener(this){task->
                    if(task != null){
                        val location: Location = task.result
                        userLocation = location
                        userLocationInitialized = true
                        print("got location ${location.latitude} - ${location.longitude}")
                        callWeatherApi(location.latitude, location.longitude)
                    }
                }
            }else{
                Toast.makeText(this, "Turn on location services", Toast.LENGTH_LONG).show()
                val locationIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(locationIntent)
            }

        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
    }

    private fun checkPermissions(): Boolean{
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            return true
        }
        return false
    }

    private fun isLocationEnabled() : Boolean{
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 100){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation()
            }
        }
    }

}