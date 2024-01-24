package com.example.saloris

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.saloris.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding


     @SuppressLint("SuspiciousIndentation")
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = ActivityMainBinding.inflate(layoutInflater)
         setContentView(binding.root)


        binding.startBtn.setOnClickListener {

        val intent = Intent(this,HeartActivity::class.java)
            startActivity(intent)
        }
     }
}