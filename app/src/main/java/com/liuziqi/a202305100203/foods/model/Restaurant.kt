package com.liuziqi.a202305100203.foods.model

import android.os.Parcelable
import com.amap.api.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class Restaurant(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String? = null,
    val rating: Float = 0f,
    val type: String = "餐厅",
    val distance: Int = 0, // 距离，单位：米
    val photos: List<String> = emptyList(),
    val businessHours: String? = null,
    val averagePrice: Float? = null
) : Parcelable {

    fun getLocation(): LatLng {
        return LatLng(latitude, longitude)
    }
}

// 模拟数据
object MockRestaurantData {
    val restaurants = listOf(
        Restaurant(
            id = "1",
            name = "海底捞火锅",
            address = "北京市海淀区中关村大街15号",
            latitude = 39.989614,
            longitude = 116.316384,
            phone = "010-12345678",
            rating = 4.8f,
            type = "火锅",
            distance = 500,
            averagePrice = 120f
        ),
        Restaurant(
            id = "2",
            name = "西贝莜面村",
            address = "北京市海淀区清华东路甲1号",
            latitude = 39.998714,
            longitude = 116.326284,
            phone = "010-87654321",
            rating = 4.5f,
            type = "西北菜",
            distance = 800,
            averagePrice = 80f
        ),
        Restaurant(
            id = "3",
            name = "星巴克咖啡",
            address = "北京市海淀区中关村软件园二期",
            latitude = 40.048645,
            longitude = 116.286384,
            phone = "010-23456789",
            rating = 4.3f,
            type = "咖啡厅",
            distance = 1200,
            averagePrice = 40f
        ),
        Restaurant(
            id = "4",
            name = "麦当劳",
            address = "北京市海淀区上地信息路22号",
            latitude = 40.038614,
            longitude = 116.306384,
            phone = "010-34567890",
            rating = 4.0f,
            type = "快餐",
            distance = 1500,
            averagePrice = 35f
        ),
        Restaurant(
            id = "5",
            name = "必胜客",
            address = "北京市海淀区五道口购物中心",
            latitude = 39.998614,
            longitude = 116.336384,
            phone = "010-45678901",
            rating = 4.2f,
            type = "西餐",
            distance = 1800,
            averagePrice = 70f
        )
    )
}