package com.exd.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
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
        buildComponent()
    }
}

class MainHost : NavHostFragment() {

}