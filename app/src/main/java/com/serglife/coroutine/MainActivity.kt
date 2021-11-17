package com.serglife.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var scope: CoroutineScope

/*  Add dependency to use "private val viewModel:MainViewModel by viewModels()"
    implementation "androidx.fragment:fragment-ktx:1.3.6"
    private val viewModel:MainViewModel by viewModels()*/


    private val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        viewModel.login("Sergey", "12345789")

        viewModel.loginLiveData.observe(this,{
            when(it.status){
                Resource.Status.LOADING ->{
                    Log.i("STATUS","LOADING")

                }
                Resource.Status.COMPLETED -> {
                    Log.i("STATUS", "Name: ${it.data?.name}")
                    Log.i("STATUS", "Password: ${it.data?.password}")
                }
            }
        })


        // Global coroutine
        scope = CoroutineScope(Dispatchers.IO)
        scope.launch {

        }

        // Start coroutine for activity and fragment
        lifecycleScope.launch{

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Destroy global coroutine
        scope.cancel()

    }
}