package com.liuziqi.a202305100203.foods

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.liuziqi.a202305100203.foods.database.AppDatabase
import com.liuziqi.a202305100203.foods.model.Diary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*

class AddDiaryActivity : AppCompatActivity() {

    private lateinit var ivFood: ImageView
    private lateinit var etTitle: EditText
    private lateinit var etRestaurant: EditText
    private lateinit var etFoodType: EditText
    private lateinit var rbRating: RatingBar
    private lateinit var tvRatingValue: TextView
    private lateinit var etContent: EditText
    private lateinit var btnSave: Button

    private var imageUri: Uri? = null
    private var imagePath: String? = null
    private var diaryId: Int? = null
    private lateinit var database: AppDatabase

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                imageUri = uri
                loadImageFromUri(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_diary)

        initViews()
        setupDatabase()
        loadDiaryIfExists()
    }

    private fun initViews() {
        ivFood = findViewById(R.id.iv_food)
        etTitle = findViewById(R.id.et_title)
        etRestaurant = findViewById(R.id.et_restaurant)
        etFoodType = findViewById(R.id.et_food_type)
        rbRating = findViewById(R.id.rb_rating)
        tvRatingValue = findViewById(R.id.tv_rating_value)
        etContent = findViewById(R.id.et_content)
        btnSave = findViewById(R.id.btn_save)

        // 设置标题
        supportActionBar?.title = if (intent.hasExtra("diary_id")) "编辑日记" else "添加日记"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rbRating.setOnRatingBarChangeListener { _, rating, _ ->
            tvRatingValue.text = String.format(Locale.getDefault(), "%.1f", rating)
        }

        ivFood.setOnClickListener {
            checkAndRequestPermission()
        }

        btnSave.setOnClickListener {
            saveDiary()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun checkAndRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                showPermissionExplanationDialog()
            }

            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun showPermissionExplanationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("需要存储权限")
            .setMessage("需要存储权限来选择和保存图片")
            .setPositiveButton("确定") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(this, "需要存储权限来选择图片", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun loadImageFromUri(uri: Uri) {
        lifecycleScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                }

                bitmap?.let {
                    ivFood.setImageBitmap(it)

                    // 将Bitmap转换为Base64字符串存储
                    withContext(Dispatchers.IO) {
                        val outputStream = ByteArrayOutputStream()
                        it.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                        val byteArray = outputStream.toByteArray()
                        imagePath = "data:image/jpeg;base64," +
                                android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddDiaryActivity, "图片加载失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDatabase() {
        database = AppDatabase.getDatabase(this)
    }

    private fun loadDiaryIfExists() {
        diaryId = intent.getIntExtra("diary_id", -1).takeIf { it != -1 }

        diaryId?.let { id ->
            lifecycleScope.launch {
                val diary = database.diaryDao().getDiaryById(id)
                diary?.let {
                    displayDiary(it)
                }
            }
        }
    }

    private fun displayDiary(diary: Diary) {
        etTitle.setText(diary.title)
        etRestaurant.setText(diary.restaurantName)
        etFoodType.setText(diary.foodType)
        rbRating.rating = diary.rating
        etContent.setText(diary.content)

        if (!diary.imagePath.isNullOrEmpty()) {
            // 解码Base64显示图片
            if (diary.imagePath!!.startsWith("data:image/jpeg;base64,")) {
                val base64String = diary.imagePath!!.substringAfter("data:image/jpeg;base64,")
                try {
                    val decodedBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    ivFood.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    ivFood.setImageResource(R.drawable.ic_food_placeholder)
                }
            } else {
                ivFood.setImageResource(R.drawable.ic_food_placeholder)
            }
        }
    }

    private fun saveDiary() {
        val title = etTitle.text.toString().trim()
        val restaurant = etRestaurant.text.toString().trim()
        val foodType = etFoodType.text.toString().trim()
        val rating = rbRating.rating
        val content = etContent.text.toString().trim()

        if (title.isEmpty() || restaurant.isEmpty() || foodType.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
            return
        }

        val diary = Diary(
            id = diaryId ?: 0,
            title = title,
            content = content,
            restaurantName = restaurant,
            foodType = foodType,
            rating = rating,
            imagePath = imagePath
        )

        lifecycleScope.launch {
            if (diaryId == null) {
                database.diaryDao().insert(diary)
                Toast.makeText(this@AddDiaryActivity, "日记保存成功", Toast.LENGTH_SHORT).show()
            } else {
                database.diaryDao().update(diary)
                Toast.makeText(this@AddDiaryActivity, "日记更新成功", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1000
    }
}