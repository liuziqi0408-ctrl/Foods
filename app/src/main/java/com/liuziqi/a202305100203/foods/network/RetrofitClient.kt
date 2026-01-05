package com.liuziqi.a202305100203.foods.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.liuziqi.a202305100203.foods.model.RecommendedRestaurant
import com.liuziqi.a202305100203.foods.model.FoodRecommendation

object RetrofitClient {

    // 基础URL（这里使用模拟数据，实际开发时替换为真实API地址）
    private const val BASE_URL = "https://api.example.com/"
    // 本地模拟服务器URL（用于测试）
    // private const val BASE_URL = "http://10.0.2.2:3000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "FoodsApp/1.0")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    // 模拟数据服务（用于开发测试）
    object MockService {
        fun getMockRecommendedRestaurants(): List<RecommendedRestaurant> {
            return listOf(
                RecommendedRestaurant(
                    id = "101",
                    name = "海底捞火锅（中关村店）",
                    address = "北京市海淀区中关村大街15号",
                    latitude = 39.989614,
                    longitude = 116.316384,
                    phone = "010-12345678",
                    rating = 4.8f,
                    type = "火锅",
                    distance = 500,
                    imageUrl = "https://example.com/haidilao.jpg",
                    businessHours = "10:00-24:00",
                    averagePrice = 120f,
                    recommendReason = "服务周到，食材新鲜，24小时营业",
                    tags = listOf("火锅", "24小时", "服务好", "网红店"),
                    discount = "学生证8.8折"
                ),
                RecommendedRestaurant(
                    id = "102",
                    name = "西贝莜面村",
                    address = "北京市海淀区清华东路甲1号",
                    latitude = 39.998714,
                    longitude = 116.326284,
                    phone = "010-87654321",
                    rating = 4.5f,
                    type = "西北菜",
                    distance = 800,
                    imageUrl = "https://example.com/xibei.jpg",
                    businessHours = "11:00-22:00",
                    averagePrice = 80f,
                    recommendReason = "正宗西北风味，莜面系列必点",
                    tags = listOf("西北菜", "面食", "家庭聚餐"),
                    discount = "午市套餐7.5折"
                ),
                RecommendedRestaurant(
                    id = "103",
                    name = "星巴克臻选",
                    address = "北京市海淀区中关村软件园二期",
                    latitude = 40.048645,
                    longitude = 116.286384,
                    phone = "010-23456789",
                    rating = 4.3f,
                    type = "咖啡厅",
                    distance = 1200,
                    imageUrl = "https://example.com/starbucks.jpg",
                    businessHours = "07:00-23:00",
                    averagePrice = 45f,
                    recommendReason = "环境优雅，适合学习工作",
                    tags = listOf("咖啡", "下午茶", "WiFi", "安静"),
                    discount = null
                ),
                RecommendedRestaurant(
                    id = "104",
                    name = "麦当劳（24小时店）",
                    address = "北京市海淀区上地信息路22号",
                    latitude = 40.038614,
                    longitude = 116.306384,
                    phone = "010-34567890",
                    rating = 4.0f,
                    type = "快餐",
                    distance = 1500,
                    imageUrl = "https://example.com/mcdonalds.jpg",
                    businessHours = "00:00-24:00",
                    averagePrice = 35f,
                    recommendReason = "24小时营业，方便快捷",
                    tags = listOf("快餐", "24小时", "汉堡"),
                    discount = "APP点餐满30减5"
                ),
                RecommendedRestaurant(
                    id = "105",
                    name = "必胜客",
                    address = "北京市海淀区五道口购物中心",
                    latitude = 39.998614,
                    longitude = 116.336384,
                    phone = "010-45678901",
                    rating = 4.2f,
                    type = "西餐",
                    distance = 1800,
                    imageUrl = "https://example.com/pizzahut.jpg",
                    businessHours = "10:00-23:00",
                    averagePrice = 70f,
                    recommendReason = "披萨口味多样，适合朋友聚餐",
                    tags = listOf("披萨", "西餐", "聚会"),
                    discount = "周二披萨半价"
                )
            )
        }

        fun getMockFoodRecommendations(): List<FoodRecommendation> {
            return listOf(
                FoodRecommendation(
                    id = "f001",
                    name = "麻辣火锅",
                    description = "正宗重庆风味，麻辣鲜香，牛肉、毛肚、鸭肠等食材新鲜",
                    imageUrl = "https://example.com/hotpot.jpg",
                    restaurantId = "101",
                    restaurantName = "海底捞火锅",
                    price = 128f,
                    rating = 4.8f,
                    category = "火锅",
                    isSpicy = true,
                    calories = 680,
                    tags = listOf("麻辣", "牛肉", "毛肚", "必点")
                ),
                FoodRecommendation(
                    id = "f002",
                    name = "羊肉莜面",
                    description = "西北特色面食，莜面劲道，羊肉汤鲜美",
                    imageUrl = "https://example.com/youmian.jpg",
                    restaurantId = "102",
                    restaurantName = "西贝莜面村",
                    price = 48f,
                    rating = 4.5f,
                    category = "面食",
                    isSpicy = false,
                    calories = 420,
                    tags = listOf("西北菜", "面食", "招牌")
                ),
                FoodRecommendation(
                    id = "f003",
                    name = "拿铁咖啡",
                    description = "精选咖啡豆，牛奶与咖啡完美融合",
                    imageUrl = "https://example.com/latte.jpg",
                    restaurantId = "103",
                    restaurantName = "星巴克",
                    price = 32f,
                    rating = 4.3f,
                    category = "饮品",
                    isSpicy = false,
                    calories = 180,
                    tags = listOf("咖啡", "经典", "提神")
                ),
                FoodRecommendation(
                    id = "f004",
                    name = "巨无霸汉堡套餐",
                    description = "双层牛肉饼，配上新鲜蔬菜和特制酱料",
                    imageUrl = "https://example.com/bigmac.jpg",
                    restaurantId = "104",
                    restaurantName = "麦当劳",
                    price = 38f,
                    rating = 4.0f,
                    category = "快餐",
                    isSpicy = false,
                    calories = 560,
                    tags = listOf("汉堡", "套餐", "经典")
                ),
                FoodRecommendation(
                    id = "f005",
                    name = "超级至尊披萨",
                    description = "多种配料，芝士浓郁，饼底松软",
                    imageUrl = "https://example.com/pizza.jpg",
                    restaurantId = "105",
                    restaurantName = "必胜客",
                    price = 89f,
                    rating = 4.2f,
                    category = "西餐",
                    isSpicy = false,
                    calories = 720,
                    tags = listOf("披萨", "芝士", "聚会")
                )
            )
        }
    }
}