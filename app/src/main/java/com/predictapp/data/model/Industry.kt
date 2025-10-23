package com.predictapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "industries")
data class Industry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)