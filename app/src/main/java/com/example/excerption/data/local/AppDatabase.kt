package com.example.excerption.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.excerption.data.local.dao.BookDao
import com.example.excerption.data.local.dao.ExcerptDao
import com.example.excerption.data.local.dao.ReviewDao
import com.example.excerption.data.local.entity.BookEntity
import com.example.excerption.data.local.entity.ExcerptEntity
import com.example.excerption.data.local.entity.ReviewEntity

@Database(
    entities = [BookEntity::class, ExcerptEntity::class, ReviewEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun excerptDao(): ExcerptDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE books ADD COLUMN rating REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE books ADD COLUMN startedAt INTEGER")
                db.execSQL("ALTER TABLE books ADD COLUMN finishedAt INTEGER")
                db.execSQL("ALTER TABLE books ADD COLUMN oneLineReview TEXT")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val columns = mutableSetOf<String>()
                db.query("PRAGMA table_info(books)").use { cursor ->
                    val nameIndex = cursor.getColumnIndex("name")
                    while (cursor.moveToNext()) {
                        columns += cursor.getString(nameIndex)
                    }
                }

                if ("rating" !in columns) {
                    db.execSQL("ALTER TABLE books ADD COLUMN rating REAL NOT NULL DEFAULT 0.0")
                }
                if ("startedAt" !in columns) {
                    db.execSQL("ALTER TABLE books ADD COLUMN startedAt INTEGER")
                }
                if ("finishedAt" !in columns) {
                    db.execSQL("ALTER TABLE books ADD COLUMN finishedAt INTEGER")
                }
                if ("oneLineReview" !in columns) {
                    db.execSQL("ALTER TABLE books ADD COLUMN oneLineReview TEXT")
                }

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS books_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        author TEXT NOT NULL,
                        publisher TEXT,
                        thumbnailUrl TEXT,
                        isbn TEXT,
                        rating REAL NOT NULL,
                        startedAt INTEGER,
                        finishedAt INTEGER,
                        oneLineReview TEXT,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO books_new (
                        id, title, author, publisher, thumbnailUrl, isbn,
                        rating, startedAt, finishedAt, oneLineReview, createdAt
                    )
                    SELECT
                        id, title, author, publisher, thumbnailUrl, isbn,
                        rating, startedAt, finishedAt, oneLineReview, createdAt
                    FROM books
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE books")
                db.execSQL("ALTER TABLE books_new RENAME TO books")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE books ADD COLUMN categoryName TEXT")
            }
        }

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "excerption.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
