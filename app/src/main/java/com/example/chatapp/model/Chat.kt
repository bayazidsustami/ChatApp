package com.example.chatapp.model

data class Chat(
    val sender: String? = null,
    val message: String? = null,
    var firebaseKey: String? = null
) {
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "sender" to sender,
            "message" to message
        )
    }
}