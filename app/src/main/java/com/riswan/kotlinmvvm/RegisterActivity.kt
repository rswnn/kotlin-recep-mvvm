package com.riswan.kotlinmvvm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.riswan.kotlinmvvm.utils.Constants
import com.riswan.kotlinmvvm.viewmodels.UserState
import com.riswan.kotlinmvvm.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_register
import kotlinx.android.synthetic.main.activity_login.et_email
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_login.in_password as in_password1
import kotlinx.android.synthetic.main.activity_login.loading as loading1

class RegisterActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        userViewModel =  ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.getState().observer(this, Observer {
            handleUIState(it)
        })
        doRegister()
    }

    private fun doRegister() {
        btn_registerr.setOnClickListener {
            val name = et_name.text.toString().trim()
            val email = et_emailr.text.toString().trim()
            val password = et_passwordr.text.toString().trim()

            if (userViewModel.validate(name, email, password)) {
                userViewModel.register(name,email, password)
            }
        }
    }
    private fun handleUIState (it:UserState) {
        when(it) {
            is UserState.Reset -> {
                setNameError(null)
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
                it.name?.let {
                    setNameError(it)
                }
                it.email?.let {
                    setEmailError(it)
                }
                it.password?.let {
                    setPasswordError(it)
                }
            }

            is  UserState.Success -> {
                Constants.setToken(this@RegisterActivity, it.token)
                startActivity(Intent(this@RegisterActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }).also {
                    finish()
                }
            }
            is UserState.IsLoading -> isLoading(it.state)
        }
    }

    private fun setNameError(err:String?){in_name.error = err}
    private fun setEmailError(err:String?){in_emailr.error = err}
    private fun setPasswordError(err:String?){in_passwordr.error = err}

    private fun toast(message:String?) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun isLoading(state:Boolean) {
        if (state) {
            btn_registerr.isEnabled = false
            loadingr.isIndeterminate = true
        } else {
            loadingr.apply {
                isIndeterminate = false
                progress = 0
            }
            btn_registerr.isEnabled = true
        }
    }
}
