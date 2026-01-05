// 在 com.liuziqi.a202305100203.foods.model 包下

data class FoodRecommendation(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val restaurantId: String,
    val restaurantName: String,
    val price: Float,
    val rating: Float,
    val category: String?,  // 改为可为空
    val isSpicy: Boolean,
    val calories: Int?,
    val tags: List<String>
)

