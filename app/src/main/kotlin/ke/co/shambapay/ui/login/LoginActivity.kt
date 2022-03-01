package ke.co.shambapay.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import ke.co.shambapay.R
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.databinding.ActivityLoginBinding
import ke.co.shambapay.ui.adapter.CustomAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity: AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()
    private val adapter =  CustomAdapter(mutableListOf<EmployeeEntity>())
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.banner.setUp(getString(R.string.app_name))

        binding.etEmail.addTextChangedListener {
            viewModel.
        }

        viewModel.state.observe(this){
            when(it){
                is LoginViewModel.State.Loading -> {
                    binding.btnLogin.isEnabled = !it.show
                    binding.progressBar.isVisible = it.show
                    binding.txtMessage.text = ""
                }
                is LoginViewModel.State.ShowMessage -> {
                    binding.txtMessage.text = it.message
                }
                is LoginViewModel.State.LoggedInUser ->{

                }
            }
        }

    }

}