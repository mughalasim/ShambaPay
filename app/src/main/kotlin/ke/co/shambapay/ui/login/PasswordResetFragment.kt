package ke.co.shambapay.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import ke.co.shambapay.databinding.FragmentPasswordResetBinding
import ke.co.shambapay.ui.base.BaseState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PasswordResetFragment : Fragment() {

    lateinit var binding: FragmentPasswordResetBinding
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        binding = FragmentPasswordResetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.etEmail.addTextChangedListener {
            viewModel.validateEmail(it.toString())
        }

        viewModel.canLogIn.observe(viewLifecycleOwner){
            binding.btnPasswordReset.isEnabled = it
        }

        binding.btnPasswordReset.setOnClickListener {
            viewModel.makePasswordRequest()
        }

        viewModel.state.observe(viewLifecycleOwner){
            when(it){
                is BaseState.Success<*> ->{
                    binding.etEmail.setText("")
                    activity?.onBackPressed()
                }
                is BaseState.UpdateUI ->{
                    binding.widgetLoading.update(it.message, it.showLoading)
                }
                is BaseState.Logout -> {}
            }
        }

    }

}