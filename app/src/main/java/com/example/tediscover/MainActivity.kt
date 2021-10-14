package com.example.tediscover

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currUser = Firebase.auth.currentUser
        if (currUser != null) {
            startActivity(Intent(this, TedActivity::class.java))
            finish()
        }
    }
}