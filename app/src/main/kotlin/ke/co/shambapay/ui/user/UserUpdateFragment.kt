package ke.co.shambapay.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ke.co.shambapay.R
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.databinding.FragmentUserUpdateBinding
import ke.co.shambapay.domain.base.BaseState
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserUpdateFragment: Fragment() {

    private val viewModel: UserUpdateViewModel by viewModel()
    lateinit var binding: FragmentUserUpdateBinding
    private val args: UserUpdateFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUserUpdateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.user == null){
            // New registration
            binding.btnDelete.isVisible = false
            binding.btnConfirm.text = getString(R.string.txt_add_, getString(R.string.txt_user, args.userType.name.lowercase()))
            binding.txtTitle.text = getString(R.string.txt_add_, getString(R.string.txt_user, args.userType.name.lowercase()))
        } else {
            binding.btnDelete.isVisible = true
            binding.btnConfirm.text = getString(R.string.txt_update_, getString(R.string.txt_user, args.userType.name.lowercase()))
            binding.txtTitle.text = getString(R.string.txt_update_, getString(R.string.txt_user, args.userType.name.lowercase()))
            binding.etEmail.isEnabled = false
        }

        viewModel.setEntity(args.user)
        binding.llCompanyName.isVisible = args.userType == UserType.OWNER

        viewModel.state.observe(viewLifecycleOwner){
            when(it){
                is BaseState.UpdateUI -> {
                    binding.widgetLoading.update(it.message, it.showLoading)
                }
                is BaseState.Success<*> -> {
                    activity?.onBackPressed()
                }
            }
        }

        viewModel.canSubmit.observe(viewLifecycleOwner){
            binding.btnConfirm.isEnabled = it
        }

        binding.included.etFirstName.addTextChangedListener {
            validateWithViewModel()
        }

        binding.included.etLastName.addTextChangedListener {
            validateWithViewModel()
        }

        binding.included.etTelephone.addTextChangedListener {
            validateWithViewModel()
        }

        binding.etEmail.addTextChangedListener {
            validateWithViewModel()
        }

        binding.etCompanyName.addTextChangedListener {
            validateWithViewModel()
        }

        binding.btnCancel.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.btnConfirm.setOnClickListener {
            viewModel.updateUser(
                firstName = binding.included.etFirstName.text.toString(),
                lastName = binding.included.etLastName.text.toString(),
                telephone = binding.included.etTelephone.text.toString(),
                email = binding.etEmail.text.toString(),
                companyName = binding.etCompanyName.text.toString(),
                companyId = args.companyId,
                userType = args.userType,
            )
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteUser()
        }

        setupView(args.user)

    }

    private fun validateWithViewModel(){
        viewModel.validate(
            firstName = binding.included.etFirstName.text.toString(),
            lastName = binding.included.etLastName.text.toString(),
            telephone = binding.included.etTelephone.text.toString(),
            email = binding.etEmail.text.toString(),
            companyName = binding.etCompanyName.text.toString(),
            userType = args.userType
        )
    }

    private fun setupView (entity: UserEntity?){
        binding.included.etFirstName.setText(entity?.firstName)
        binding.included.etLastName.setText(entity?.lastName)
        binding.included.etTelephone.setText(entity?.fetchPhoneNumber())
        binding.etEmail.setText(entity?.email)
    }

}