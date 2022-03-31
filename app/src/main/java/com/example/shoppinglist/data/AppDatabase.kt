package com.example.shoppinglist.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShopItemDBModel::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun shopListDao(): ShopListDao

    companion object {
        private var INSTANSE: AppDatabase? = null
        private val LOCK = Any()
        private val DB_NAME = "shop_item.db"

        fun getInstanse(application: Application): AppDatabase {
            INSTANSE?.let {
                return it
            }
            synchronized(LOCK) {
                INSTANSE?.let {
                    return it
                }
                val db = Room.databaseBuilder(
                    application,
                    AppDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANSE = db
                return  db
            }
        }
    }
}