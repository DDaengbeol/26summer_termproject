package com.example.excerption.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String, // 아이디를 PK로 사용
    val password: String
)