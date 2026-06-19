package com.example.excerption.data.repository

import com.example.excerption.data.local.dao.UserDao
import com.example.excerption.data.local.entity.UserEntity

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    suspend fun getUserById(id: String) = userDao.getUserById(id)
}