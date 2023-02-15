package com.kotlin.supweather

class Functions {

    companion object{
        fun kelvinToCelsius(kelvin: Float) : Int{
            val celsius = kelvin - 273.15
            return celsius.toInt()
        }
    }
}