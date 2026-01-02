package com.liuziqi.a202305100203.foods.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liuziqi.a202305100203.foods.R
import com.liuziqi.a202305100203.foods.model.Diary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryAdapter(
    private var diaries: List<Diary> = emptyList(),
    private val onItemClick: (Diary) -> Unit = {},
    private val onItemLongClick: (Diary) -> Unit = {}
) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivFood: ImageView = itemView.findViewById(R.id.iv_food)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvRestaurant: TextView = itemView.findViewById(R.id.tv_restaurant)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        private val tvFoodType: TextView = itemView.findViewById(R.id.tv_food_type)
        private val rbRating: RatingBar = itemView.findViewById(R.id.rb_rating)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)

        fun bind(diary: Diary) {
            // 加载图片（如果存在）
            diary.imagePath?.let { path ->
                if (path.startsWith("data:image/jpeg;base64,")) {
                    val base64String = path.substringAfter("data:image/jpeg;base64,")
                    try {
                        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        ivFood.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        ivFood.setImageResource(R.drawable.ic_food_placeholder)
                    }
                } else {
                    ivFood.setImageResource(R.drawable.ic_food_placeholder)
                }
            } ?: run {
                ivFood.setImageResource(R.drawable.ic_food_placeholder)
            }

            tvTitle.text = diary.title
            tvRestaurant.text = diary.restaurantName
            tvContent.text = diary.content
            tvFoodType.text = diary.foodType
            rbRating.rating = diary.rating

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            tvDate.text = dateFormat.format(Date(diary.createdAt))

            itemView.setOnClickListener { onItemClick(diary) }
            itemView.setOnLongClickListener {
                onItemLongClick(diary)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diary, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(diaries[position])
    }

    override fun getItemCount(): Int = diaries.size

    fun updateData(newDiaries: List<Diary>) {
        diaries = newDiaries
        notifyDataSetChanged()
    }
}