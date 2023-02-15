package com.kotlin.supweather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_popular_cities.*
import kotlinx.android.synthetic.main.home_brief_info.view.*

class PopularCities : AppCompatActivity() {

    private lateinit var citiesList: PopularCitiesListAdapter
    private lateinit var cities: MutableList<Cities>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popular_cities)
        cities = citiesListData()
        // call api to load current weather data for cities
        loadWeatherData()
        // set cities to recycler view
        citiesList = PopularCitiesListAdapter(cities)
        popularCitiesList.adapter = citiesList
        popularCitiesList.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )

        // top back button function
        backButton.setOnClickListener{
            onBackPressed()
        }


    }

    // dummy cities list data
    fun citiesListData() : MutableList<Cities>{
        var list = mutableListOf<Cities>()
        val city = Cities("New York", -75.000000, 43.000000, R.drawable.newyork, 0)
        val city2 = Cities("Los Angeles",-118.327759,34.098907,R.drawable.los_angeles,0)
        list.add(city)
        list.add(city2)
        return list
    }

    // call weather api's
    fun loadWeatherData(){
        for(index in 0..cities.size -1){
            val apiKey : String = "c594fd3c34f1f26ba1866b335b0b69f8"
            val url : String = "https://api.openweathermap.org/data/2.5/weather?lat=${cities[index].latitude}&lon=${cities[index].longitude}&appid=$apiKey"
            newApiCall(url,index)
        }
    }

    private fun newApiCall(url: String, cityIndex: Int) {
        ApiCall(url).fetchJson(object: OnRequestCompleteListener {
            override fun onSuccess(response: String) {
                println("onSuccess: $response")
                setWeatherData(response, cityIndex)
            }

            override fun onError(error: String) {
                println("onError: $error")
            }
        })
    }

    // set weather data in current cities list
    fun setWeatherData(weatherJson: String, cityIndex: Int){
        if(weatherJson != ""){
            var weather = Gson().fromJson(weatherJson, WeatherData::class.java)
            runOnUiThread {
                cities[cityIndex].temperature = Functions.kelvinToCelsius(weather.main.temp)
                citiesList = PopularCitiesListAdapter(cities)
                popularCitiesList.adapter = citiesList
            }
        }
    }



}