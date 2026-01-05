package com.liuziqi.a202305100203.foods

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.liuziqi.a202305100203.foods.adapter.FoodRecommendationAdapter
import com.liuziqi.a202305100203.foods.adapter.RecommendedRestaurantAdapter
import com.liuziqi.a202305100203.foods.viewmodel.RecommendationViewModel

class RecommendationActivity : AppCompatActivity() {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var rvRestaurants: RecyclerView
    private lateinit var rvFoods: RecyclerView
    private lateinit var chipAll: Chip

    private lateinit var viewModel: RecommendationViewModel
    private lateinit var restaurantAdapter: RecommendedRestaurantAdapter
    private lateinit var foodAdapter: FoodRecommendationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)

        initViews()
        setupViewModel()
        setupAdapters()
        setupObservers()
        setupChipGroup()
        setupRefresh()
    }

    private fun initViews() {
        swipeRefresh = findViewById(R.id.swipe_refresh)
        progressBar = findViewById(R.id.progress_bar)
        rvRestaurants = findViewById(R.id.rv_restaurants)
        rvFoods = findViewById(R.id.rv_foods)
        chipAll = findViewById(R.id.chip_all)

        // 设置Toolbar
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).apply {
            setNavigationOnClickListener {
                finish()
            }
        }

        // 设置餐厅列表为水平滚动
        rvRestaurants.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 设置美食列表为垂直滚动
        rvFoods.layoutManager = LinearLayoutManager(this)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[RecommendationViewModel::class.java]
    }

    private fun setupAdapters() {
        restaurantAdapter = RecommendedRestaurantAdapter(
            onItemClick = { restaurant ->
                // 跳转到餐厅详情或地图页面
                val intent = Intent(this, MapActivity::class.java).apply {
                    putExtra("restaurant_id", restaurant.id)
                    putExtra("latitude", restaurant.latitude)
                    putExtra("longitude", restaurant.longitude)
                }
                startActivity(intent)
            }
        )
        rvRestaurants.adapter = restaurantAdapter

        foodAdapter = FoodRecommendationAdapter(
            onItemClick = { food ->
                // 显示食物详情
                showFoodDetail(food)
            }
        )
        rvFoods.adapter = foodAdapter
    }

    private fun setupObservers() {
        viewModel.recommendedRestaurants.observe(this) { restaurants ->
            restaurantAdapter.updateData(restaurants)
        }

        viewModel.foodRecommendations.observe(this) { foods ->
            foodAdapter.updateData(foods)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            swipeRefresh.isRefreshing = isLoading
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupChipGroup() {
        val chipGroup = findViewById<com.google.android.material.chip.ChipGroup>(R.id.chip_group)
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val chip = group.findViewById<Chip>(checkedIds.first())
            chip?.text?.toString()?.let { category ->
                viewModel.setSelectedCategory(if (category == "全部") "全部" else category)
            }
        }
    }

    private fun setupRefresh() {
        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun showFoodDetail(food: com.liuziqi.a202305100203.foods.model.FoodRecommendation) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(food.name)
            .setMessage("""
                餐厅：${food.restaurantName}
                价格：¥${food.price}
                评分：${food.rating}/5.0
                分类：${food.category}
                描述：${food.description}
                
                标签：${food.tags.joinToString("、")}
                ${if (food.isSpicy) "🌶️ 辛辣" else "😋 不辣"}
                ${food.calories?.let { "🔥 约${it}卡路里" } ?: ""}
            """.trimIndent())
            .setPositiveButton("去这家餐厅") { _, _ ->
                val intent = Intent(this, MapActivity::class.java).apply {
                    putExtra("restaurant_id", food.restaurantId)
                }
                startActivity(intent)
            }
            .setNegativeButton("取消", null)
            .create()

        dialog.show()
    }
}