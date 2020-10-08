package com.example.chatapp.login

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.main.MainActivity
import com.example.chatapp.R
import com.example.chatapp.model.Status
import com.example.chatapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    private val dialogBuilder by lazy { AlertDialog.Builder(this) }
    private lateinit var dialogLoading: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()
        viewClicked()
        subscribeViewModel()
    }

    override fun onStart() {
        super.onStart()
        checkSessionUser()
    }

    private fun initLoadingDialog(){
        dialogBuilder.setView(R.layout.loading_dialog)
        dialogLoading = dialogBuilder.create()
        dialogLoading.setCancelable(false)
    }

    private fun showLoading(){ dialogLoading.show()}
    private fun hideLoading(){ dialogLoading.dismiss()}

    private fun viewClicked(){
        binding.btnLogin.setOnClickListener {
            validation()
        }
    }

    private fun validation(){
        if (binding.textEmail.text.toString().isEmpty()){
            binding.textEmail.error = "Email Kosong"
        } else if (binding.textPassword.text.toString().isEmpty()){
            binding.textPassword.error = "Password Kosong"
        } else {
            viewModel.doLogin(
                binding.textEmail.text.toString(),
                binding.textPassword.text.toString()
            )
        }
    }

    private fun subscribeViewModel(){
        viewModel.loginResult.observe(this, Observer { status->
            Log.d("Status", "$status")
            status?.let {
                when(it){
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        hideLoading()
                        Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show()
                    }
                    Status.SUCCESS -> {
                        hideLoading()
                        Intent(this, MainActivity::class.java).also { intent->
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        })
    }

    private fun checkSessionUser(){
        if (viewModel.isLogin()){
            Intent(this, MainActivity::class.java).also { intent->
                startActivity(intent)
                finish()
            }
        }
    }
}