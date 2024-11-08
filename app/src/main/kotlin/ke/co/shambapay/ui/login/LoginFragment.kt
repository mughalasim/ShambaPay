package ke.co.shambapay.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ke.co.shambapay.databinding.FragmentLoginBinding
import ke.co.shambapay.ui.activities.MainActivity
import ke.co.shambapay.ui.base.BaseState
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModel()
    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.UpdateUI -> {
                    binding.widgetLoading.update(it.message, it.showLoading)
                }
                is BaseState.Success<*> -> {
                    activity?.startActivity(Intent(activity, MainActivity::class.java).apply {
                    /*If you want to add any intent extras*/ })
                    activity?.finish()
                }
                is BaseState.Logout -> {}
            }
        }

        viewModel.canLogIn.observe(viewLifecycleOwner) {
            binding.btnLogin.isEnabled = it
        }

        binding.etEmail.addTextChangedListener {
            viewModel.validateLogin(it.toString(), binding.etPassword.text.toString())
        }

        binding.etPassword.addTextChangedListener {
            viewModel.validateLogin(binding.etEmail.text.toString(), it.toString())
        }

        binding.btnLogin.setOnClickListener {
            viewModel.makeLoginRequest()
        }

        binding.btnPasswordReset.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToPasswordResetFragment()
            findNavController().navigate(action)
        }
    }

}