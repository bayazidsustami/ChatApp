package com.example.chatapp.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel: ViewModel() {
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }

    private val _chatList by lazy { MutableLiveData<List<Chat>>() }
    val chatList : LiveData<List<Chat>> get() = _chatList

    private val _bubbleChat by lazy { MutableLiveData<Chat>() }
    val bubbleChat: LiveData<Chat> get() = _bubbleChat

    private val MESSAGE = "message"
    private lateinit var valueEventListener: ValueEventListener

    fun doLogout(){
        firebaseAuth.signOut()
    }

    fun getUsername(): String {
        val email = firebaseAuth.currentUser?.email.orEmpty()
        return email.split("@").first()
    }

    fun postMessage(message: String){
        val chat = Chat(getUsername(), message)
        databaseReference.child(MESSAGE).push().setValue(chat)
    }

    fun readMessageFromFirebase(){
        valueEventListener = object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Log.e("ERROR_DB", error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val dataChat = snapshot.children.toMutableList().map {child->
                    val chat = child.getValue(Chat::class.java)
                    chat?.firebaseKey = child.key
                    chat ?: Chat()
                }
                _chatList.value = dataChat
            }

        }

        databaseReference.child(MESSAGE).addValueEventListener(valueEventListener)
    }

    fun updateChat(chat: Chat){
        val childUpdater = hashMapOf<String, Any>(
            "/$MESSAGE/${chat.firebaseKey}" to chat.toMap()
        )
        databaseReference.updateChildren(childUpdater)
    }

    fun deleteChat(chat: Chat){
        databaseReference.child(MESSAGE)
            .child(chat.firebaseKey.orEmpty())
            .removeValue()
    }

    fun onBubbleChatLongClick(chat: Chat){

    }

    override fun onCleared() {
        databaseReference.removeEventListener(valueEventListener)
        super.onCleared()
    }
}