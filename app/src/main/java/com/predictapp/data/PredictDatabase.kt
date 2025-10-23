package com.predictapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.predictapp.data.dao.IndustryDao
import com.predictapp.data.dao.PredictionDao
import com.predictapp.data.model.Industry
import com.predictapp.data.model.Prediction
import com.predictapp.utils.Converters

@Database(
    entities = [Industry::class, Prediction::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PredictDatabase : RoomDatabase() {
    abstract fun industryDao(): IndustryDao
    abstract fun predictionDao(): PredictionDao

    companion object {
        @Volatile
        private var INSTANCE: PredictDatabase? = null

        fun getDatabase(context: Context): PredictDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PredictDatabase::class.java,
                    "predict_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}