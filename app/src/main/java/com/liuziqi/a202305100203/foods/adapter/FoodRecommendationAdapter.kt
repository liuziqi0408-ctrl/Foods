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
import com.liuziqi.a202305100203.foods.model.FoodRecommendation

class FoodRecommendationAdapter(
    private var foods: List<FoodRecommendation> = emptyList(),
    private val onItemClick: (FoodRecommendation) -> Unit = {}
) : RecyclerView.Adapter<FoodRecommendationAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_image)
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvRestaurant: TextView = itemView.findViewById(R.id.tv_restaurant)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val rbRating: RatingBar = itemView.findViewById(R.id.rb_rating)
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        private val ivSpicy: ImageView = itemView.findViewById(R.id.iv_spicy)

        fun bind(food: FoodRecommendation) {
            // 加载图片
            Glide.with(itemView.context)
                .load(food.imageUrl)
                .placeholder(R.drawable.ic_food_placeholder)
                .error(R.drawable.ic_food_placeholder)
                .into(ivImage)

            tvName.text = food.name
            tvRestaurant.text = food.restaurantName
            tvPrice.text = "¥${food.price}"
            rbRating.rating = food.rating
            tvCategory.text = food.category

            // 显示辣度图标
            ivSpicy.visibility = if (food.isSpicy) View.VISIBLE else View.GONE

            // 显示标签
            val tagsContainer = itemView.findViewById<ViewGroup>(R.id.container_tags)
            tagsContainer.removeAllViews()

            food.tags.take(3).forEach { tag ->
                val tagView = TextView(itemView.context).apply {
                    text = tag
                    textSize = 10f
                    setTextColor(itemView.context.getColor(R.color.gray))
                    setPadding(8, 4, 8, 4)
                    background = itemView.context.getDrawable(R.drawable.bg_tag)
                }
                tagsContainer.addView(tagView)
            }

            itemView.setOnClickListener {
                onItemClick(food)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_recommendation, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    override fun getItemCount(): Int = foods.size

    fun updateData(newFoods: List<FoodRecommendation>) {
        foods = newFoods
        notifyDataSetChanged()
    }
}