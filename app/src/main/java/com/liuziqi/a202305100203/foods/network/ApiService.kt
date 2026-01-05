package com.liuziqi.a202305100203.foods.network

import com.liuziqi.a202305100203.foods.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.liuziqi.a202305100203.foods.model.RecommendedRestaurant
import com.liuziqi.a202305100203.foods.model.FoodRecommendation
interface ApiService {

    // 获取推荐餐厅
    @GET("recommend/restaurants")
    suspend fun getRecommendedRestaurants(
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("category") category: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<RecommendedRestaurant>>>

    // 获取推荐美食
    @GET("recommend/foods")
    suspend fun getRecommendedFoods(
        @Query("category") category: String? = null,
        @Query("isSpicy") isSpicy: Boolean? = null,
        @Query("maxPrice") maxPrice: Float? = null,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<FoodRecommendation>>>

    // 根据食物类型搜索餐厅
    @GET("search/restaurants")
    suspend fun searchRestaurantsByFoodType(
        @Query("foodType") foodType: String,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("radius") radius: Int = 5000
    ): Response<ApiResponse<List<RecommendedRestaurant>>>

    // 获取热门标签
    @GET("tags/hot")
    suspend fun getHotTags(
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<String>>>

}