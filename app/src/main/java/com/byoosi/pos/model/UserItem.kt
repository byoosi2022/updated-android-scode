package com.byoosi.pos.model

import java.io.Serializable


/**
 * Created by pintusingh on 23/12/20.
 */

data class UserItem(
    val name: String?,
    val customer_name: String?,
    val mobile_no: String?,
    val email_id: String?
) : Serializable