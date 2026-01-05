package com.liuziqi.a202305100203.foods.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)

// 推荐餐厅数据模型
data class RecommendedRestaurant(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("phone") val phone: String?,
    @SerializedName("rating") val rating: Float,
    @SerializedName("type") val type: String,
    @SerializedName("distance") val distance: Int,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("businessHours") val businessHours: String?,
    @SerializedName("averagePrice") val averagePrice: Float?,
    @SerializedName("recommendReason") val recommendReason: String,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("discount") val discount: String?
)

// 食物推荐数据模型
data class FoodRecommendation(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("restaurantId") val restaurantId: String,
    @SerializedName("restaurantName") val restaurantName: String,
    @SerializedName("price") val price: Float,
    @SerializedName("rating") val rating: Float,
    @SerializedName("category") val category: String,
    @SerializedName("isSpicy") val isSpicy: Boolean,
    @SerializedName("calories") val calories: Int?,
    @SerializedName("tags") val tags: List<String>
)