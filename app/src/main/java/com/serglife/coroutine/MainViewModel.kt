package com.serglife.coroutine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _loginLiveData = MutableLiveData<Resource<UserProfile>>()
    val loginLiveData: LiveData<Resource<UserProfile>> = _loginLiveData

    init {
        doWorksInParallel()
    }

    fun login(name: String, password: String) {
        runCoroutine(_loginLiveData) {
            val response = "ServerApi.restApi.authorize(name, password).execute()"
            return@runCoroutine UserProfile(name = name, password = password)
        }
    }



    private fun <T> runCoroutine(
        correspondenceLiveData: MutableLiveData<Resource<T>>,
        block: suspend () -> T
    ) {

        correspondenceLiveData.value = Resource(Resource.Status.LOADING, null, null)

        viewModelScope.launch {
            try {
                val result = block()
                correspondenceLiveData.postValue(Resource(Resource.Status.COMPLETED, result, null))
            } catch (error: Exception) {
                correspondenceLiveData.postValue(Resource(Resource.Status.COMPLETED, null, error))
            }
        }
    }

    // viewModelScope.async -> return result Coroutine

    private suspend fun doWorkFor5Seconds(): String {
        delay(5000)
        return "doWorkFor5Seconds"
    }

    private suspend fun doWorkFor10Seconds(): String {
        delay(10000)
        return "doWorkFor10Seconds"
    }

    private fun doWorksInParallel() {
        val one = viewModelScope.async {
            doWorkFor5Seconds()
        }
        val two = viewModelScope.async {
            doWorkFor10Seconds()
        }

        viewModelScope.launch {
            val five = one.await()
            println("Coroutine 5 sec : $five")
            val ten = two.await()
            println("Coroutine 10 sec : $ten")
        }
    }




}

data class Resource<T>(
    val status: Status,
    val data: T?,
    val exception: Exception?
) {
    enum class Status {
        LOADING,
        COMPLETED
    }
}

class UserProfile(val name: String, val password: String)