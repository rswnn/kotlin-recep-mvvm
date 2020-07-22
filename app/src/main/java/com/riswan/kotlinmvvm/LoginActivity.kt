package com.riswan.kotlinmvvm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.riswan.kotlinmvvm.models.User
import com.riswan.kotlinmvvm.utils.Constants
import com.riswan.kotlinmvvm.viewmodels.UserState
import com.riswan.kotlinmvvm.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.loading

class LoginActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        userViewModel =  ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.getState().observer(this, Observer {
            handleUIState(it)
        })
        doLogin()

        btn_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun doLogin () {
        btn_login.setOnClickListener {
            val email = et_email.text.toString().trim()
            val password = et_password.text.toString().trim()

            if (userViewModel.validate(null,  email,  password)) {
                userViewModel.login(email, password)
            }
        }
    }


    private fun handleUIState (it:UserState) {
        when(it) {
            is UserState.Reset -> {
                setEmailError(null)
                setPasswordError(null)
            }
            is UserState.Error -> {
                isLoading(false)
                toast(it.err)
            }

            is UserState.ShowToast -> toast(it.message)
            is UserState.Failed -> {
                isLoading(false)
                toast(it.message)
            }
            is UserState.Validate -> {
                it.email?.let {
                    setEmailError(it)
                }
                it.password?.let {
                    setPasswordError(it)
                }
            }

            is  UserState.Success -> {
                Constants.setToken(this, it.token)
                startActivity(Intent(this, MainActivity::class.java)).also {
                    finish()
                }
            }
            is UserState.IsLoading -> isLoading(it.state)
        }
    }

    private fun setEmailError(err:String?){in_email.error = err}
    private fun setPasswordError(err:String?){in_password.error = err}

    private fun toast(message:String?) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun isLoading(state:Boolean) {
        if (state) {
            btn_register.isEnabled = false
            btn_login.isEnabled = false
            loading.isIndeterminate = true
        } else {
            loading.apply {
                isIndeterminate = false
                progress = 0
            }
            btn_register.isEnabled = true
            btn_login.isEnabled = true
        }
    }
}
