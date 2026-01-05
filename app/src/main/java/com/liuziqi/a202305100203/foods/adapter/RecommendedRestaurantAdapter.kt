package com.liuziqi.a202305100203.foods.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.liuziqi.a202305100203.foods.R
import com.liuziqi.a202305100203.foods.model.RecommendedRestaurant

class RecommendedRestaurantAdapter(
    private var restaurants: List<RecommendedRestaurant> = emptyList(),
    private val onItemClick: (RecommendedRestaurant) -> Unit = {}
) : RecyclerView.Adapter<RecommendedRestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_image)
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvType: TextView = itemView.findViewById(R.id.tv_type)
        private val rbRating: RatingBar = itemView.findViewById(R.id.rb_rating)
        private val tvDistance: TextView = itemView.findViewById(R.id.tv_distance)
        private val tvReason: TextView = itemView.findViewById(R.id.tv_reason)
        private val tvDiscount: TextView = itemView.findViewById(R.id.tv_discount)

        fun bind(restaurant: RecommendedRestaurant) {
            // 加载图片
            restaurant.imageUrl?.let { url ->
                Glide.with(itemView.context)
                    .load(url)
                    .placeholder(R.drawable.ic_restaurant_placeholder)
                    .error(R.drawable.ic_restaurant_placeholder)
                    .into(ivImage)
            } ?: run {
                ivImage.setImageResource(R.drawable.ic_restaurant_placeholder)
            }

            tvName.text = restaurant.name
            tvType.text = restaurant.type
            rbRating.rating = restaurant.rating
            tvDistance.text = "${restaurant.distance}m"
            tvReason.text = restaurant.recommendReason

            restaurant.discount?.let {
                tvDiscount.text = it
                tvDiscount.visibility = View.VISIBLE
            } ?: run {
                tvDiscount.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onItemClick(restaurant)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommended_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(restaurants[position])
    }

    override fun getItemCount(): Int = restaurants.size

    fun updateData(newRestaurants: List<RecommendedRestaurant>) {
        restaurants = newRestaurants
        notifyDataSetChanged()
    }
}