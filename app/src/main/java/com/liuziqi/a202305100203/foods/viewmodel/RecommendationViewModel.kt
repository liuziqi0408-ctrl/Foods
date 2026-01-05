package com.liuziqi.a202305100203.foods.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liuziqi.a202305100203.foods.network.RetrofitClient
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.liuziqi.a202305100203.foods.model.FoodRecommendation
import com.liuziqi.a202305100203.foods.model.RecommendedRestaurant

class RecommendationViewModel : ViewModel() {

    private val _recommendedRestaurants = MutableLiveData<List<RecommendedRestaurant>>()
    val recommendedRestaurants: LiveData<List<RecommendedRestaurant>> = _recommendedRestaurants

    private val _foodRecommendations = MutableLiveData<List<FoodRecommendation>>()
    val foodRecommendations: LiveData<List<FoodRecommendation>> = _foodRecommendations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _selectedCategory = MutableLiveData<String>("全部")
    val selectedCategory: LiveData<String> = _selectedCategory

    init {
        loadRecommendations()
    }

    fun loadRecommendations() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // 尝试从网络获取数据
                // val response = RetrofitClient.apiService.getRecommendedRestaurants()
                // if (response.isSuccessful && response.body()?.code == 200) {
                //     _recommendedRestaurants.value = response.body()?.data ?: emptyList()
                // } else {
                //     // 网络失败时使用模拟数据
                //     loadMockData()
                // }

                // 暂时使用模拟数据
                loadMockData()

            } catch (e: Exception) {
                when (e) {
                    is SocketTimeoutException -> {
                        _errorMessage.value = "网络连接超时"
                    }
                    is UnknownHostException -> {
                        _errorMessage.value = "网络连接失败"
                    }
                    else -> {
                        _errorMessage.value = "加载失败: ${e.message}"
                    }
                }
                // 出错时使用模拟数据
                loadMockData()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadFoodRecommendations(category: String? = null) {
        viewModelScope.launch {
            try {
                // 1. 先明确获取的模拟数据类型是 List<FoodRecommendation>
                val allFoods: List<FoodRecommendation> = RetrofitClient.MockService.getMockFoodRecommendations()
                // 2. 修复过滤逻辑（确保 it.category 是 String 类型）
                val filteredFoods: List<FoodRecommendation> = if (category == null || category == "全部") {
                    allFoods
                } else {
                    // 如果 it.category 实际是 CharCategory，需要转成 String（比如 it.category.name）
                    allFoods.filter { it.category.toString() == category }
                    // 若 it.category 本身是 String，直接写：it.category == category
                }
                // 3. 赋值给 LiveData
                _foodRecommendations.value = filteredFoods
            } catch (e: Exception) {
                _errorMessage.value = "加载失败: ${e.message}"
            }
        }
    }

    private fun loadMockData() {
        _recommendedRestaurants.value = RetrofitClient.MockService.getMockRecommendedRestaurants()
        _foodRecommendations.value = RetrofitClient.MockService.getMockFoodRecommendations()
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
        loadFoodRecommendations(category)
    }

    fun refresh() {
        loadRecommendations()
    }
}