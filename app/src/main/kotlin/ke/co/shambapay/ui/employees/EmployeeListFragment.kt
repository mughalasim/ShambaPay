package ke.co.shambapay.ui.employees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.databinding.FragmentEmployeesBinding
import ke.co.shambapay.domain.base.BaseState
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.adapter.CustomAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmployeeListFragment: Fragment() {

    private val viewModel: EmployeeListViewModel by viewModel()
    private val adapter =  CustomAdapter(mutableListOf<EmployeeEntity>())
    lateinit var binding: FragmentEmployeesBinding
    private val globalState: UiGlobalState by inject()
    private val args: EmployeeListFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEmployeesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.adapter = adapter

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener{
            override fun onItemClicked(data: Any?) {
                if (globalState.isAdmin()){
                   // TODO - Add navigation to EmployeeUpdate Fragment
                } else {
                    val action = EmployeeListFragmentDirections.actionEmployeeListFragmentToCaptureFragment(data as EmployeeEntity)
                    findNavController().navigate(action)
                }
            }
        })

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }

        viewModel.state.observe(viewLifecycleOwner) {
            when(it){
                is BaseState.UpdateUI -> {
                    binding.widgetLoading.update(it.message, it.showLoading)
                }
                is BaseState.Success<*> -> {}

            }
        }

        viewModel.getEmployees(args.companyId, "")

        binding.btnSearch.setOnClickListener {
            viewModel.getEmployees(args.companyId, binding.etSearch.text.toString())
        }

        binding.etSearch.addTextChangedListener {
            binding.btnSearch.isEnabled = !it.isNullOrEmpty()
            if (it.isNullOrEmpty()){
                viewModel.getEmployees(args.companyId, "")
            }
        }

        binding.btnAdd.isVisible = globalState.isAdmin()
        binding.btnBack.isVisible = globalState.isAdmin()

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.btnAdd.setOnClickListener {
            // TODO - Navigate to the EmployeeUpdate Fragment
        }

    }

}