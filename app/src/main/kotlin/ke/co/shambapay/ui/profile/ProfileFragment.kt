package ke.co.shambapay.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import ke.co.shambapay.R
import ke.co.shambapay.databinding.FragmentProfileBinding
import ke.co.shambapay.domain.base.BaseState
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.activities.MainActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModel()
    private val globalState: UiGlobalState by inject()


    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.widgetUser.setUp(globalState.user!!, globalState.settings!!)

        binding.bannerPassword.setUp(getString(R.string.txt_password_update))

        binding.etNewPassword.addTextChangedListener {
            viewModel.validatePassword(it.toString(), binding.etConfirmPassword.text.toString())
        }

        binding.etConfirmPassword.addTextChangedListener {
            viewModel.validatePassword(binding.etNewPassword.text.toString(), it.toString())
        }

        viewModel.state.observe(viewLifecycleOwner){
            when(it){
                is BaseState.UpdateUI ->{
                    binding.widgetLoading.update(it.message, it.showLoading)
                }
                is BaseState.Success<*> -> {
                    binding.etConfirmPassword.setText("")
                    binding.etNewPassword.setText("")
                }
            }
        }

        viewModel.canSubmit.observe(viewLifecycleOwner){
            binding.btnUpdate.isEnabled = it
        }

        binding.btnUpdate.setOnClickListener {
            viewModel.updatePassword(binding.etNewPassword.text.toString())
        }

    }

}