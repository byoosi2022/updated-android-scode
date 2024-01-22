package com.byoosi.pos.model

import java.io.Serializable

data class CommonResponse<out T>(
    val message: T,
) : Serializable

data class Login(val message: LoginMessage?, val full_name: String?) : Serializable

data class LoginMessage(val sid: String?, val api_key: String?, val api_secret: String?, val user: String?) : Serializable