package com.example.maphw

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.maphw.data.Owner
import com.example.maphw.fragments.UserFragment
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    public val userViewModel: UserViewModel by viewModels {
        OwnerViewModelFactory((application.getApplicationContext() as MapApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showInitialFragment(savedInstanceState)

    }

    private fun showInitialFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val fragmentManager = supportFragmentManager
            if (fragmentManager.findFragmentByTag(UserFragment::class.java.name) == null) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.user_list_main_container, UserFragment())
                    .commit()
            }
        }
    }
    /*val word = Owner(1,"434","434","434")
    userViewModel.insert(word)*/
}