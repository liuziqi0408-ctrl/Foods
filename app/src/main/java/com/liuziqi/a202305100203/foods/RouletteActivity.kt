package com.liuziqi.a202305100203.foods

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.liuziqi.a202305100203.foods.view.RouletteView

class RouletteActivity : AppCompatActivity() {

    private lateinit var rouletteView: RouletteView
    private lateinit var btnSpin: Button
    private lateinit var tvResult: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roulette)

        initViews()
        setupNavigation()
    }

    private fun initViews() {
        rouletteView = findViewById(R.id.rouletteView)
        btnSpin = findViewById(R.id.btn_spin)
        tvResult = findViewById(R.id.tv_result)

        btnSpin.setOnClickListener {
            spinRoulette()
        }

        rouletteView.setOnSpinCompleteListener { result ->
            tvResult.text = "今天吃：$result"
            tvResult.visibility = TextView.VISIBLE
            Toast.makeText(this, "决定了！今天吃$result", Toast.LENGTH_LONG).show()
        }
    }

    private fun spinRoulette() {
        rouletteView.spin()
        tvResult.visibility = TextView.GONE
    }

    private fun setupNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.nav_roulette

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_roulette -> {
                    // 已经在当前页面
                    true
                }R.id.nav_diary -> {
                startActivity(Intent(this, DiaryActivity::class.java))
                true
            }
                else -> false
            }
        }
    }
}