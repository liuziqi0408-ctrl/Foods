data class RecommendedRestaurant(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String? = null,
    val rating: Float = 0f,
    val type: String = "餐厅",
    val distance: Int = 0,
    val imageUrl: String? = null,  // 改为可为空
    val businessHours: String? = null,
    val averagePrice: Float? = null,
    val recommendReason: String,
    val tags: List<String> = emptyList(),
    val discount: String? = null
)