package ke.co.shambapay.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import ke.co.shambapay.databinding.FragmentRegisterBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment: Fragment() {

    private val viewModel: RegisterViewModel by viewModel()
    lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner){
            when(it){
                is RegisterViewModel.State.UpdateUI -> {
                    binding.btnRegister.isEnabled = !it.showLoading
                    binding.progressBar.isVisible = it.showLoading
                    binding.txtMessage.text = it.message
                    binding.txtMessage.isVisible = it.message.isNotEmpty()
                }
                is RegisterViewModel.State.Success -> {

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