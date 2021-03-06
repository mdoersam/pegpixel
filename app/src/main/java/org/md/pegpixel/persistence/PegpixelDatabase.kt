package org.md.pegpixel.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context


@Database(entities = [PegboardEntity::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class PegpixelDatabase : RoomDatabase() {
    abstract fun pegboardDao(): PegboardDao

    companion object {
        private var INSTANCE: PegpixelDatabase? = null

        fun getInstance(context: Context): PegpixelDatabase {
            if (INSTANCE == null) {
                synchronized(PegpixelDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            PegpixelDatabase::class.java, "pegboards.db")
                            .build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
