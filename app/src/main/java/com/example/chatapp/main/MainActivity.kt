package com.example.chatapp.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewClicked()
        setupRecyclerView()
        subscribeVm()

        viewModel.readMessageFromFirebase()
    }

    private fun setupRecyclerView(){
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        mainAdapter = MainAdapter(viewModel.getUsername(), viewModel)
        binding.rvChat.layoutManager = layoutManager
        binding.rvChat.adapter = mainAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return  when(item.itemId){
            R.id.actionLogout -> {
                viewModel.doLogout()
                Intent(this, LoginActivity::class.java).also {intent ->
                    startActivity(intent)
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun viewClicked(){
        binding.btnSend.setOnClickListener {
            if (binding.textChat.text.toString().isNotEmpty()){
                viewModel.postMessage(binding.textChat.text.toString())
                binding.textChat.setText("")
            } else {
                Toast.makeText(this, "Pesan tidak boleh kosong ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun subscribeVm(){
        viewModel.chatList.observe(this, Observer { chats ->
            chats?.let {
                mainAdapter.setChatDataList(it)
                binding.rvChat.smoothScrollToPosition(it.size)
            }
        })
    }
}