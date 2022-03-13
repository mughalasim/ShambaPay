package ke.co.shambapay.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import ke.co.shambapay.databinding.FragmentRegisterBinding
import ke.co.shambapay.domain.base.BaseState
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment: Fragment() {

    private val viewModel: RegisterViewModel by viewModel()
    lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner){
            when(it){
                is BaseState.UpdateUI -> {
                    binding.btnRegister.isEnabled = !it.showLoading
                    binding.widgetLoading.update(it.message, it.showLoading)
                }
                is BaseState.Success<*> -> {

                }
            }
        }

        viewModel.canRegister.observe(viewLifecycleOwner){
            binding.btnRegister.isVisible = it
        }

        binding.etEmail.addTextChangedListener {
            updateViewModel()
        }

        binding.etCompanyName.addTextChangedListener {
            updateViewModel()
        }

        binding.etFirstName.addTextChangedListener {
            updateViewModel()
        }

        binding.etLastName.addTextChangedListener {
            updateViewModel()
        }

        binding.etTelephone.addTextChangedListener {
            updateViewModel()
        }

        binding.btnRegister.setOnClickListener {
            viewModel.registerUser()
        }
    }

    private fun updateViewModel(){
        viewModel.validate(
            binding.etEmail.text.toString(),
            binding.etFirstName.text.toString(),
            binding.etLastName.text.toString(),
            binding.etCompanyName.text.toString(),
            binding.etTelephone.text.toString(),
        )
    }
}