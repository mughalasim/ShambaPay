package ke.co.shambapay.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.databinding.FragmentLoginBinding
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
                is LoginViewModel.State.UpdateUI -> {
                    binding.btnLogin.isEnabled = !it.showLoading
                    binding.progressBar.isVisible = it.showLoading
                    binding.txtMessage.text = it.message
                    binding.txtMessage.isVisible = it.message.isNotEmpty()
                }
                is LoginViewModel.State.Success -> {
                    when(it.userType){
                        UserType.ADMIN -> {
                            val action = LoginFragmentDirections.actionLoginFragmentToEmployeeListFragment()
                            findNavController().navigate(action)
                        }

                        UserType.OWNER -> {
                            val action = LoginFragmentDirections.actionLoginFragmentToEmployeeListFragment()
                            findNavController().navigate(action)
                        }

                        UserType.SUPERVISOR -> {
                            val action = LoginFragmentDirections.actionLoginFragmentToEmployeeListFragment()
                            findNavController().navigate(action)
                        }

                    }

                }
            }
        }

        viewModel.canLogIn.observe(viewLifecycleOwner) {
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

    }

}