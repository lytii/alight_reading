package com.alight.reading

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alight.reading.dagger.ActivityComponent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_nav_host)
        setTheme(R.style.DarkTheme)
        ActivityComponent.buildDagger(this)
    }
}
