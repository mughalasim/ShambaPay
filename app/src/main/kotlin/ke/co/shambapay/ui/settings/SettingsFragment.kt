package ke.co.shambapay.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ke.co.shambapay.data.model.JobRateEntity
import ke.co.shambapay.databinding.FragmentSettingsBinding
import ke.co.shambapay.domain.base.BaseState
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.adapter.CustomAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModel()
    private val adapter = CustomAdapter(mutableListOf<JobRateEntity>())
    private val globalState: UiGlobalState by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bannerRates.setUp("Company rates")
        binding.llEditCompanyName.isVisible = false
        binding.txtCompanyName.isVisible = true
        binding.txtCompanyName.text = globalState.settings!!.companyName

        binding.recycler.adapter = adapter

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener{
            override fun onItemClicked(data: Any?) {
                if (globalState.isAdmin()){
                    findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSettingsUpdateFragment(data as JobRateEntity))
                }
            }
        })

        binding.refresh.setOnRefreshListener {
            binding.refresh.isRefreshing = false
            adapter.updateData(globalState.settings!!.rates.values.toList())
        }

        adapter.updateData(globalState.settings!!.rates.values.toList())

        binding.btnAddRates.isVisible = globalState.isAdmin()
        binding.btnAddRates.setOnClickListener {
            findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToSettingsUpdateFragment()
            )
        }

        binding.btnUploadEmployees.isVisible = globalState.isAdmin()
        binding.btnUploadEmployees.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToUploadEmployeesFragment())
        }

        binding.btnUploadWork.isVisible = globalState.isAdmin()
        binding.btnUploadWork.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToUploadWorkFragment())
        }

        binding.btnEditCompanyName.isVisible = globalState.isAdmin()
        binding.btnEditCompanyName.setOnClickListener {
            binding.llEditCompanyName.isVisible = true
            binding.txtCompanyName.isVisible = false
            binding.btnEditCompanyName.isVisible = false
            binding.etCompanyName.setText(globalState.settings!!.companyName)
        }

        binding.btnCancel.setOnClickListener {
            binding.llEditCompanyName.isVisible = false
            binding.txtCompanyName.isVisible = true
            binding.btnEditCompanyName.isVisible = true
        }

        binding.etCompanyName.addTextChangedListener {
            binding.btnUpdate.isEnabled = (it.toString().isNotEmpty() && (it.toString() != globalState.settings!!.companyName))
        }

        binding.btnUpdate.setOnClickListener {
            viewModel.setCompanyName(binding.etCompanyName.text.toString())
        }

        viewModel.state.observe(viewLifecycleOwner){
            when(it){
                is BaseState.UpdateUI -> {
                    binding.widgetLoading.update(it.message, it.showLoading)
                }
                is BaseState.Success<*> -> {
                    binding.widgetLoading.update("", false)
                    binding.llEditCompanyName.isVisible = false
                    binding.txtCompanyName.isVisible = true
                    binding.btnEditCompanyName.isVisible = true
                    binding.txtCompanyName.text = it.data as String
                }
            }
        }

    }

}