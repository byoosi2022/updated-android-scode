package com.byoosi.pos.model
import com.google.gson.Gson

data class CancelResponse(
    val message: Message, // Assuming Message is correctly imported
    val status: String
)

data class Message(
    val message: String,
    val status: String
)