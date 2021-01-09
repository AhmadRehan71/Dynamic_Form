package com.example.dynamicform

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val myList = intent.getSerializableExtra("result") as ArrayList<*>
        for(result in myList){
            txtResult.text = "${txtResult.text} \n $result"
        }
    }
}