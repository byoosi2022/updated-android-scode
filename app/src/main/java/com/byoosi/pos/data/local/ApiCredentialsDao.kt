package com.byoosi.pos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ApiCredentialsDao {

    @Insert
    suspend fun insert(apiCredentials: ApiCredentials)

    @Query("SELECT * FROM api_credentials LIMIT 1")
    suspend fun getApiCredentials(): ApiCredentials?
}
