package ke.co.shambapay.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.JobRateEntity
import ke.co.shambapay.data.model.SettingsEntity
import ke.co.shambapay.databinding.FragmentSettingsBinding
import ke.co.shambapay.domain.base.BaseState
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.adapter.CustomAdapter
import ke.co.shambapay.ui.employees.EmployeeListFragmentDirections
import ke.co.shambapay.ui.employees.EmployeeListViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    private val adapter =  CustomAdapter(mutableListOf<JobRateEntity>())
    private val globalState: UiGlobalState by inject()

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bannerRates.setUp("Company rates")
        binding.txtCompanyName.text = globalState.settings!!.companyName

        binding.recycler.adapter = adapter

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener{
            override fun onItemClicked(data: Any?) {
                val action = SettingsFragmentDirections.actionSettingsFragmentToSettingsUpdateFragment(data as JobRateEntity)
                findNavController().navigate(action)
            }
        })

        binding.refresh.setOnRefreshListener {
            binding.refresh.isRefreshing = false
            adapter.updateData(globalState.settings!!.rates.values.toList())
        }

        adapter.updateData(globalState.settings!!.rates.values.toList())

        binding.btnAddRates.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToSettingsUpdateFragment()
            findNavController().navigate(action)
        }

    }

}