package com.kotlin.supweather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.popular_city_tile.view.*

class PopularCitiesListAdapter(
    private val cities : MutableList<Cities>
) : RecyclerView.Adapter<PopularCitiesListAdapter.CitiesViewHolder>() {
    class CitiesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesViewHolder {
        return CitiesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.popular_city_tile,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CitiesViewHolder, position: Int) {
        val city = cities[position]
        holder.itemView.apply {
            cityText.text = city.name
            temperature.text = "${city.temperature}"
            cityLayout.setBackgroundResource(city.imageUrl)
        }
    }

    override fun getItemCount(): Int {
        return cities.size
    }



}