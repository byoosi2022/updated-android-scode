package com.byoosi.pos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_credentials")
data class ApiCredentials(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val apiKey: String,
    val apiSecret: String
)
