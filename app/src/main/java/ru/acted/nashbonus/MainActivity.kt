package ru.acted.nashbonus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.acted.nashbonus.databinding.ActivityMainBinding
import ru.acted.nashbonus.views.NashaButtonView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}