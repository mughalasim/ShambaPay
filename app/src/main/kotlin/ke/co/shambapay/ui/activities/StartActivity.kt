package ke.co.shambapay.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ke.co.shambapay.R
import ke.co.shambapay.databinding.ActivityMainBinding
import ke.co.shambapay.databinding.ActivityStartBinding

class StartActivity: AppCompatActivity() {

    lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.banner.setUp(getString(R.string.app_name))


    }
}