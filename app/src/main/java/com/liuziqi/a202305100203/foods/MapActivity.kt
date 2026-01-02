package com.liuziqi.a202305100203.foods

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.liuziqi.a202305100203.foods.adapter.RestaurantAdapter
import com.liuziqi.a202305100203.foods.model.MockRestaurantData
import com.liuziqi.a202305100203.foods.model.Restaurant
import androidx.cardview.widget.CardView

class MapActivity : AppCompatActivity(), LocationSource,
    AMapLocationListener {


    private lateinit var mapView: MapView
    private lateinit var aMap: AMap
    private lateinit var progressBar: ProgressBar
    private lateinit var cardBottomSheet: CardView
    private lateinit var rvNearbyRestaurants: androidx.recyclerview.widget.RecyclerView
    private lateinit var fabLocation: FloatingActionButton
    private lateinit var etSearch: EditText
    private lateinit var ibMyLocation: ImageButton

    private lateinit var locationClient: AMapLocationClient
    private var locationSourceListener: LocationSource.OnLocationChangedListener? = null
    private var currentLocation: LatLng? = null

    private lateinit var restaurantAdapter: RestaurantAdapter
    private val markers = mutableMapOf<String, Marker>()
    private var selectedMarker: Marker? = null

    // 权限请求码
    private companion object {
        const val PERMISSIONS_REQUEST_LOCATION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        initViews()
        initMap(savedInstanceState)
        setupRecyclerView()
        checkPermissions()
        setupSearch()
        loadMockData() // 先用模拟数据，后续可以换成真实数据
    }

    private fun initViews() {
        mapView = findViewById(R.id.map_view)
        progressBar = findViewById(R.id.progress_bar)
        cardBottomSheet = findViewById(R.id.card_bottom_sheet)
        rvNearbyRestaurants = findViewById(R.id.rv_nearby_restaurants)
        fabLocation = findViewById(R.id.fab_location)
        etSearch = findViewById(R.id.et_search)
        ibMyLocation = findViewById(R.id.ib_my_location)

        // 设置Toolbar
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).apply {
            setNavigationOnClickListener {
                finish()
            }
        }

        // 定位按钮点击
        fabLocation.setOnClickListener {
            currentLocation?.let {
                moveToLocation(it)
            }
        }

        // 我的位置按钮
        ibMyLocation.setOnClickListener {
            currentLocation?.let {
                moveToLocation(it)
            }
        }
    }

    private fun initMap(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        aMap = mapView.map

        // 设置地图类型
        aMap.mapType = AMap.MAP_TYPE_NORMAL

        // 设置缩放级别
        aMap.uiSettings.isZoomControlsEnabled = false
        aMap.uiSettings.isMyLocationButtonEnabled = false
        aMap.uiSettings.isCompassEnabled = true
        aMap.uiSettings.isScaleControlsEnabled = true

        // 设置定位源
        aMap.setLocationSource(this)
        aMap.isMyLocationEnabled = true

        // 设置地图点击监听
        aMap.setOnMapClickListener { latLng ->
            cardBottomSheet.visibility = android.view.View.GONE
            selectedMarker = null
        }

        // 设置标记点击监听
        aMap.setOnMarkerClickListener { marker ->
            val restaurantId = marker.`object` as? String
            restaurantId?.let {
                val restaurant = getRestaurantById(it)
                restaurant?.let {
                    showRestaurantInfo(it)
                    marker.showInfoWindow()
                }
            }
            true
        }
    }

    private fun setupRecyclerView() {
        restaurantAdapter = RestaurantAdapter(
            onItemClick = { restaurant ->
                moveToRestaurant(restaurant)
                showRestaurantInfo(restaurant)
            }
        )

        rvNearbyRestaurants.layoutManager = LinearLayoutManager(this)
        rvNearbyRestaurants.adapter = restaurantAdapter
    }

    private fun setupSearch() {
        etSearch.isEnabled = false
        etSearch.hint = "POI搜索功能开发中"
    }


    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val granted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!granted) {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            startLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startLocation()
            } else {
                showMessage("需要位置权限来获取周边美食")
                // 使用默认位置
                val defaultLocation = LatLng(39.9042, 116.4074) // 北京
                currentLocation = defaultLocation
                moveToLocation(defaultLocation)
                loadMockData()
            }
        }
    }

    private fun startLocation() {
        showLoading(true)
        locationClient = AMapLocationClient(this)
        val locationOption = AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            isOnceLocation = false
            interval = 5000
            isNeedAddress = true
            isWifiActiveScan = true
        }
        locationClient.setLocationOption(locationOption)
        locationClient.setLocationListener(this)
        locationClient.startLocation()
    }

    private fun loadMockData() {
        val restaurants = MockRestaurantData.restaurants
        restaurantAdapter.updateData(restaurants)
        addMarkers(restaurants)

        // 显示底部列表
        cardBottomSheet.visibility = android.view.View.VISIBLE
    }

    private fun addMarkers(restaurants: List<Restaurant>) {
        // 清除现有标记
        markers.values.forEach { it.remove() }
        markers.clear()

        restaurants.forEach { restaurant ->
            val markerOptions = MarkerOptions()
                .position(restaurant.getLocation())
                .title(restaurant.name)
                .snippet("${restaurant.type} | 人均¥${restaurant.averagePrice ?: "未知"}")
                .icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_RED
                ))

            val marker = aMap.addMarker(markerOptions)
            marker?.`object` = restaurant.id
            marker?.let {
                markers[restaurant.id] = it
            }
        }
    }

    private fun getRestaurantById(id: String): Restaurant? {
        return MockRestaurantData.restaurants.find { it.id == id }
    }

    private fun showRestaurantInfo(restaurant: Restaurant) {
        // 移动地图到餐厅位置
        moveToRestaurant(restaurant)

        // 高亮选中的标记
        selectedMarker?.setIcon(BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_RED
        ))

        val marker = markers[restaurant.id]
        marker?.let {
            it.setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_BLUE
            ))
            selectedMarker = it
        }

        // 显示信息窗口
        marker?.showInfoWindow()

        // 滚动列表到对应位置
        val position = MockRestaurantData.restaurants.indexOfFirst { it.id == restaurant.id }
        if (position != -1) {
            rvNearbyRestaurants.smoothScrollToPosition(position)
        }
    }

    private fun moveToLocation(latLng: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        aMap.animateCamera(cameraUpdate)
    }

    private fun moveToRestaurant(restaurant: Restaurant) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            restaurant.getLocation(),
            16f
        )
        aMap.animateCamera(cameraUpdate)
    }

    // LocationSource 接口实现
    override fun activate(listener: LocationSource.OnLocationChangedListener) {
        locationSourceListener = listener
    }

    override fun deactivate() {
        locationSourceListener = null
        locationClient.stopLocation()
        locationClient.onDestroy()
    }

    // AMapLocationListener 接口实现
    override fun onLocationChanged(location: AMapLocation?) {
        location?.let {
            if (it.errorCode == 0) {
                val latLng = LatLng(it.latitude, it.longitude)
                currentLocation = latLng

                locationSourceListener?.onLocationChanged(it)

                // 首次定位时移动到当前位置
                if (markers.isEmpty()) {
                    moveToLocation(latLng)
                    loadMockData()
                }

                showLoading(false)
            } else {
                showMessage("定位失败: ${it.errorInfo}")
                showLoading(false)

                // 使用默认位置
                val defaultLocation = LatLng(39.9042, 116.4074) // 北京
                currentLocation = defaultLocation
                moveToLocation(defaultLocation)
                loadMockData()
            }
        }
    }

    // PoiSearch.OnPoiSearchListener 接口实现




    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    // 生命周期管理
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        if (::locationClient.isInitialized) {
            locationClient.stopLocation()
            locationClient.onDestroy()
        }
    }
}