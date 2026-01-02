package com.liuziqi.a202305100203.foods

import android.app.Application
import com.amap.api.maps.MapsInitializer
import com.amap.api.location.AMapLocationClient

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        //高德地图隐私合规（必须最先调用）
        MapsInitializer.updatePrivacyShow(this, true, true)
        MapsInitializer.updatePrivacyAgree(this, true)

        //高德定位隐私合规
        AMapLocationClient.updatePrivacyShow(this, true, true)
        AMapLocationClient.updatePrivacyAgree(this, true)
    }
}
