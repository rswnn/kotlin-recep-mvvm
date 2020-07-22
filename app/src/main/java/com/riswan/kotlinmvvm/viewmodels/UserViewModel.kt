package com.riswan.kotlinmvvm.viewmodels

import androidx.lifecycle.ViewModel
import com.riswan.kotlinmvvm.models.User
import com.riswan.kotlinmvvm.utils.Constants
import com.riswan.kotlinmvvm.utils.SingleLiveEvent
import com.riswan.kotlinmvvm.utils.WrappedResponse
import com.riswan.kotlinmvvm.webservices.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {
    private var state: SingleLiveEvent<UserState> = SingleLiveEvent()
    private var api = ApiClient.instance()

    fun login (email: String, password: String) {
        state.value = UserState.IsLoading(true)
        api.onLogin(email, password).enqueue(object : Callback<WrappedResponse<User>> {
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                println(t.message)
                state.value = UserState.Error(t.message)
            }

            override fun onResponse( call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>){
                if (response.isSuccessful){
                    val body = response.body() as WrappedResponse<User>
                    if (body.status.equals("1")) {
                        var res = body.data
                        state.value = UserState.Success("Bearer ${body.data!!.api_token}")
                    } else {
                        state.value = UserState.Failed("Login Failed ${body.message}")
                    }
                } else {
                    state.value = UserState.Error("Some went wrong")
                }
                state.value = UserState.IsLoading(false)
            }
        })
    }
    fun register (name: String,email: String, password: String) {
        state.value = UserState.IsLoading(true)
        api.onRegister(name, email, password).enqueue(object : Callback<WrappedResponse<User>> {
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                println(t.message)
                state.value = UserState.Error(t.message)
            }

            override fun onResponse( call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>){
                if (response.isSuccessful){
                    val body = response.body() as WrappedResponse<User>
                    if (body.status.equals("1")) {
                        var res = body.data
                        state.value = UserState.Success("Bearer ${body.data!!.api_token}")
                    } else {
                        state.value = UserState.Failed("register Failed ${body.message}")
                    }
                } else {
                    state.value = UserState.Error("Register went wrong")
                }
                state.value = UserState.IsLoading(false)
            }
        })
    }
        fun validate(name:String?, email:String, password:String): Boolean {
        state.value = UserState.Reset
        if (name != null) {
            if (name.isEmpty()){
                state.value = UserState.ShowToast("Nama cannot empety")
                return false
            }
            if (name.length < 5) {
                state.value = UserState.Validate(name = "Name of register must have 5 character")
                return false
            }
        }
        if (email.isEmpty() || password.isEmpty()){
            state.value = UserState.ShowToast("Name and password cannot empety")
            return false
        }
        if (!Constants.isValidEmail(email)) {
            state.value = UserState.Validate(email = "Name must type of email")
            return false
        }
        if (!Constants.isValidPassword(password)) {
            state.value = UserState.Validate(password="Password not Valid")
            return false
        }
        return true
    }

    fun getState() = state
}

sealed class UserState {
    data class Error(var err: String?): UserState()
    data class ShowToast(var message:String): UserState()
    data class Validate(var name:String? = null, var email: String?=null, var password: String? = null): UserState()
    data class IsLoading(var state: Boolean = false): UserState()
    data class Success(var token: String): UserState()
    data class Failed(var message: String): UserState()
    object Reset:UserState()
}