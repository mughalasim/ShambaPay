package ke.co.shambapay.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import ke.co.shambapay.R
import ke.co.shambapay.databinding.ActivityLoginBinding
import ke.co.shambapay.ui.employees.EmployeeListFragment
import ke.co.shambapay.ui.upload.UploadEmployeesFragment
import ke.co.shambapay.ui.upload.UploadWorkFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity: AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()
//    private val adapter =  CustomAdapter(mutableListOf<EmployeeEntity>())
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.banner.setUp(getString(R.string.app_name))

        viewModel.state.observe(this){
            when(it){
                is LoginViewModel.State.UpdateUI -> {
                    binding.btnLogin.isEnabled = !it.showLoading
                    binding.progressBar.isVisible = it.showLoading
                    binding.txtMessage.text = it.message
                    binding.txtMessage.isVisible = it.message.isNotEmpty()
                }
                is LoginViewModel.State.LoggedInUser -> {

                }
            }
        }

        viewModel.canLogIn.observe(this){
            binding.btnLogin.isVisible = it
        }

        binding.etEmail.addTextChangedListener {
            viewModel.validate(it.toString(), binding.etPassword.text.toString())
        }

        binding.etPassword.addTextChangedListener {
            viewModel.validate(binding.etEmail.text.toString(), it.toString())
        }

        binding.btnLogin.setOnClickListener {
            viewModel.makeLoginRequest()
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.rootContainer.id, UploadWorkFragment())
            .commitAllowingStateLoss()

    }

}