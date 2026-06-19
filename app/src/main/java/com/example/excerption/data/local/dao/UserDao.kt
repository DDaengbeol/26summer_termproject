package com.example.excerption.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.excerption.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserById(id: String): UserEntity?
}