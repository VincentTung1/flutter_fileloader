package com.vincent.flutter_fileloader.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vincent.flutter_fileloader.R

class LoadExcelActivity : AppCompatActivity() {

    companion object{

        fun start(context: Context, path : String){
            val i  = Intent(context,LoadExcelActivity :: class.java)
            i.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_excel)
    }
}