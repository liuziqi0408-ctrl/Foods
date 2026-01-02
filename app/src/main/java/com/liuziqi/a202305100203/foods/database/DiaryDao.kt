package com.liuziqi.a202305100203.foods.database

import androidx.room.*
import com.liuziqi.a202305100203.foods.model.Diary
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diaries ORDER BY createdAt DESC")
    fun getAllDiaries(): Flow<List<Diary>>

    @Query("SELECT * FROM diaries WHERE id = :id")
    suspend fun getDiaryById(id: Int): Diary?

    @Insert
    suspend fun insert(diary: Diary): Long

    @Update
    suspend fun update(diary: Diary)

    @Delete
    suspend fun delete(diary: Diary)

    @Query("DELETE FROM diaries WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM diaries WHERE foodType = :foodType ORDER BY rating DESC")
    fun getDiariesByFoodType(foodType: String): Flow<List<Diary>>
}