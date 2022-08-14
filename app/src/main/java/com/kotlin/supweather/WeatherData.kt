package com.kotlin.supweather

data class WeatherData (
    var base : String,
    var visibility : Int,
    var dt : Int,
    var timezone : Int,
    var id : Int,
    var name : String,
    var cod : Int,
    var coord: Coord,
    var weather : MutableList<WeatherList>,
    var main : Main,
    var wind: Wind,
    var clouds: Clouds,
    var sys: Sys
)

data class Coord(
    var lon : Double,
    var lat : Double
)

data class WeatherList(
    var id : Int,
    var main : String,
    var description : String,
    var icon : String
)

data class Main(
    var temp : Float,
    var feels_like : Float,
    var temp_min : Float,
    var temp_max : Float,
    var pressure : Int,
    var humidity : Int
)

data class Wind(
    var speed : Float,
    var deg : Int
)

data class Clouds(
    var all : Int
)

data class Sys(
  var type : Int,
  var id : Int,
  var message : Double,
  var country : String,
  var sunrise : Int,
  var sunset : Int
)