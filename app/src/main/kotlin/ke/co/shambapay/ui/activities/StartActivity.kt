package ke.co.shambapay.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ke.co.shambapay.BuildConfig
import ke.co.shambapay.R
import ke.co.shambapay.databinding.ActivityStartBinding

class StartActivity: AppCompatActivity() {

    lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtVersion.text = getString(R.string.txt_version, BuildConfig.VERSION_NAME)

    }
}