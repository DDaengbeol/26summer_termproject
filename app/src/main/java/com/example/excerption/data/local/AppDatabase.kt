package com.example.excerption.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.excerption.data.local.dao.BookDao
import com.example.excerption.data.local.dao.ExcerptDao
import com.example.excerption.data.local.dao.ReviewDao
import com.example.excerption.data.local.dao.UserDao
import com.example.excerption.data.local.entity.BookEntity
import com.example.excerption.data.local.entity.ExcerptEntity
import com.example.excerption.data.local.entity.ReviewEntity
import com.example.excerption.data.local.entity.UserEntity

// 모든 Entity를 리스트에 등록해야 합니다.
@Database(
    entities = [BookEntity::class, ExcerptEntity::class, ReviewEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun excerptDao(): ExcerptDao
    abstract fun reviewDao(): ReviewDao
    abstract fun userDao(): UserDao // 로그인/회원가입용 DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "excerption-database" // DB 파일 이름
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}