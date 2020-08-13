package com.exd.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import com.exd.myapplication.dagger.ActivityComponent
import com.exd.myapplication.dagger.DaggerActivityComponent

class MainActivity : AppCompatActivity() {

    private fun buildComponent() {
        DaggerActivityComponent.builder()
            .bindContext(this.applicationContext)
            .build()
            .let { ActivityComponent.instance = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_nav_host)
        setTheme(R.style.DarkTheme)
        buildComponent()
    }
}
