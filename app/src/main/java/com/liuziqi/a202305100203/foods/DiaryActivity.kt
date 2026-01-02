package com.liuziqi.a202305100203.foods

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.liuziqi.a202305100203.foods.adapter.DiaryAdapter
import com.liuziqi.a202305100203.foods.database.AppDatabase
import com.liuziqi.a202305100203.foods.model.Diary
import kotlinx.coroutines.launch

class DiaryActivity : AppCompatActivity() {

    private lateinit var rvDiaries: androidx.recyclerview.widget.RecyclerView
    private lateinit var llEmpty: LinearLayout
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: DiaryAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        initViews()
        setupDatabase()
        setupRecyclerView()
        loadDiaries()
    }

    private fun initViews() {
        rvDiaries = findViewById(R.id.rv_diaries)
        llEmpty = findViewById(R.id.ll_empty)
        fabAdd = findViewById(R.id.fab_add)

        // 设置Toolbar
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).apply {
            setNavigationOnClickListener {
                finish()
            }
        }

        // 添加按钮点击事件
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddDiaryActivity::class.java))
        }
    }

    private fun setupDatabase() {
        database = AppDatabase.getDatabase(this)
    }

    private fun setupRecyclerView() {
        adapter = DiaryAdapter(
            onItemClick = { diary ->
                // 点击查看详情（可以后续扩展）
                val intent = Intent(this, AddDiaryActivity::class.java).apply {
                    putExtra("diary_id", diary.id)
                }
                startActivity(intent)
            },
            onItemLongClick = { diary ->
                // 长按删除
                showDeleteDialog(diary)
            }
        )

        rvDiaries.layoutManager = LinearLayoutManager(this)
        rvDiaries.adapter = adapter
    }

    private fun loadDiaries() {
        lifecycleScope.launch {
            database.diaryDao().getAllDiaries().collect { diaries ->
                if (diaries.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                    adapter.updateData(diaries)
                }
            }
        }
    }

    private fun showEmptyState() {
        llEmpty.visibility = LinearLayout.VISIBLE
        rvDiaries.visibility = android.view.View.GONE
    }

    private fun hideEmptyState() {
        llEmpty.visibility = LinearLayout.GONE
        rvDiaries.visibility = android.view.View.VISIBLE
    }

    private fun showDeleteDialog(diary: Diary) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("删除日记")
            .setMessage("确定要删除这篇日记吗？")
            .setPositiveButton("删除") { _, _ ->
                deleteDiary(diary)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun deleteDiary(diary: Diary) {
        lifecycleScope.launch {
            database.diaryDao().delete(diary)
            // 数据会自动刷新
        }
    }

    override fun onResume() {
        super.onResume()
        loadDiaries()
    }
}