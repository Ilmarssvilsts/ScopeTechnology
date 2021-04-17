package com.example.maphw

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.maphw.data.Owner
import com.example.maphw.fragments.UserFragment


class MainActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels {
        OwnerViewModelFactory((application as MapApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // showInitialFragment(savedInstanceState)
       // val word = Owner("hghg")
       // userViewModel.insert(word)
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
}