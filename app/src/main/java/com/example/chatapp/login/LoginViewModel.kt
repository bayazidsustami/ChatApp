package com.example.chatapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.model.Status
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel: ViewModel() {
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val _loginResult by lazy { MutableLiveData<Status>() }
    val loginResult: LiveData<Status>get() = _loginResult

    fun doLogin(email: String, password: String){
        _loginResult.value = Status.LOADING
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { result->
                if (result.isSuccessful){
                    _loginResult.value = Status.SUCCESS
                } else {
                    _loginResult.value = Status.ERROR
                }
            }
    }

    fun isLogin(): Boolean{
        val session = firebaseAuth.currentUser
        return  session != null
    }
}