package com.galaxybookpublication.views

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.galaxybookpublication.R
import com.galaxybookpublication.databinding.ActivityChangePasswordBinding
import com.galaxybookpublication.models.repo.SharedPreferenceHelper
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.authToken
import com.galaxybookpublication.viewmodels.ChangePasswordViewModel

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var changePasswordBinding: ActivityChangePasswordBinding
    private val changePasswordViewModel by lazy { ViewModelProvider(this)[ChangePasswordViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changePasswordBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        val preference = SharedPreferenceHelper.customPreference(this)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Change Password"

        changePasswordBinding.btnChange.setOnClickListener {
            changePasswordViewModel.changePassword(
                "Bearer ${preference.authToken}",
                changePasswordBinding.tvOldPassword.text.toString().trim(),
                changePasswordBinding.tvNewPassword.text.toString().trim(),
                changePasswordBinding.tvConfirmPassword.text.toString().trim()
            )
        }

        changePasswordViewModel.changePasswordLiveData.observe(this, Observer {
            if (it.success) {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                finish()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}