package ke.co.shambapay.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ke.co.shambapay.R
import ke.co.shambapay.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.banner.setUp(getString(R.string.app_name))


    }
}