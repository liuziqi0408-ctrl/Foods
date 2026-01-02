package com.liuziqi.a202305100203.foods.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liuziqi.a202305100203.foods.R
import com.liuziqi.a202305100203.foods.model.Restaurant

class RestaurantAdapter(
    private var restaurants: List<Restaurant> = emptyList(),
    private val onItemClick: (Restaurant) -> Unit = {}
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvAddress: TextView = itemView.findViewById(R.id.tv_address)
        private val tvDistance: TextView = itemView.findViewById(R.id.tv_distance)
        private val tvType: TextView = itemView.findViewById(R.id.tv_type)
        private val rbRating: RatingBar = itemView.findViewById(R.id.rb_rating)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)

        fun bind(restaurant: Restaurant) {
            tvName.text = restaurant.name
            tvAddress.text = restaurant.address
            tvDistance.text = "${restaurant.distance}m"
            tvType.text = restaurant.type
            rbRating.rating = restaurant.rating

            restaurant.averagePrice?.let {
                tvPrice.text = "人均¥${it}"
            } ?: run {
                tvPrice.text = "人均未知"
            }

            itemView.setOnClickListener {
                onItemClick(restaurant)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(restaurants[position])
    }

    override fun getItemCount(): Int = restaurants.size

    fun updateData(newRestaurants: List<Restaurant>) {
        restaurants = newRestaurants
        notifyDataSetChanged()
    }
}