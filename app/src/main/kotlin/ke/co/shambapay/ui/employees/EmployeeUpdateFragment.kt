package ke.co.shambapay.ui.employees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ke.co.shambapay.R
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.databinding.FragmentEmployeeUpdateBinding
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.base.BaseState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmployeeUpdateFragment: Fragment() {

    private val viewModel: EmployeeUpdateViewModel by viewModel()
    lateinit var binding: FragmentEmployeeUpdateBinding
    private val args: EmployeeUpdateFragmentArgs by navArgs()
    private val globalState: UiGlobalState by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEmployeeUpdateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.employee == null){
            // New registration
            binding.btnDelete.isVisible = false
            binding.btnConfirm.text = getString(R.string.txt_add_, getString(R.string.txt_employee))
            binding.txtTitle.text = getString(R.string.txt_add_, getString(R.string.txt_employee))
        } else {
            binding.btnDelete.isVisible = true
            binding.btnConfirm.text = getString(R.string.txt_update_, getString(R.string.txt_employee))
            binding.txtTitle.text = getString(R.string.txt_update_, getString(R.string.txt_employee))
            viewModel.setEntity(args.employee!!)
        }

        viewModel.setCompanyId(args.companyId)

        viewModel.state.observe(viewLifecycleOwner){
            when(it){
                is BaseState.UpdateUI -> {
                    binding.widgetLoading.update(it.message, it.showLoading)
                }
                is BaseState.Success<*> -> {
                    activity?.onBackPressed()
                }
                is BaseState.Logout -> {
                    globalState.logout(activity!!)
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

        binding.etNid.addTextChangedListener {
            validateWithViewModel()
        }

        binding.etNhif.addTextChangedListener {
            validateWithViewModel()
        }

        binding.etNssf.addTextChangedListener {
            validateWithViewModel()
        }

        binding.btnConfirm.setOnClickListener {
            viewModel.updateEmployee(
                firstName = binding.included.etFirstName.text.toString(),
                lastName = binding.included.etLastName.text.toString(),
                telephone = binding.included.etTelephone.text.toString(),
                nationalId = binding.etNid.text.toString(),
                nhif = binding.etNhif.text.toString(),
                nssf = binding.etNssf.text.toString()
            )
        }

        binding.btnCancel.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteEmployee()
        }

        setupView(args.employee)

    }

    private fun validateWithViewModel(){
        viewModel.validate(
            firstName = binding.included.etFirstName.text.toString(),
            lastName = binding.included.etLastName.text.toString(),
            telephone = binding.included.etTelephone.text.toString()
        )
    }

    private fun setupView (entity: EmployeeEntity?){
        binding.included.etFirstName.setText(entity?.firstName)
        binding.included.etLastName.setText(entity?.lastName)
        binding.included.etTelephone.setText(entity?.fetchPhoneNumber())
        binding.etNid.setText(entity?.fetchNationalIdEmpty())
        binding.etNhif.setText(entity?.nhif)
        binding.etNssf.setText(entity?.nssf)
    }
}