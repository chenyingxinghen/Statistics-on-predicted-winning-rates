package com.predictapp

import android.app.Application
import com.predictapp.data.PredictDatabase

class PredictApplication : Application() {
    val database by lazy { PredictDatabase.getDatabase(this) }
}