package com.liuziqi.a202305100203.foods.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "diaries")
data class Diary(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val restaurantName: String,
    val foodType: String,
    val rating: Float, // 1-5分
    val imagePath: String? = null,
    val createdAt: Long = Date().time,
    val location: String? = null
)