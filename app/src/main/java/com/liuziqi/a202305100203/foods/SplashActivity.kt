package com.liuziqi.a202305100203.foods

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 延迟2秒后跳转到主页面
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, RouletteActivity::class.java))
            finish()
        }, 2000)
    }
}