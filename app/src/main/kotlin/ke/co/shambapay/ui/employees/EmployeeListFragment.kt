package ke.co.shambapay.ui.employees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.databinding.FragmentEmployeesBinding
import ke.co.shambapay.domain.base.BaseState
import ke.co.shambapay.ui.adapter.CustomAdapter
import ke.co.shambapay.ui.login.LoginFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmployeeListFragment: Fragment() {

    private val viewModel: EmployeeListViewModel by viewModel()
    private val adapter =  CustomAdapter(mutableListOf<EmployeeEntity>())
    lateinit var binding: FragmentEmployeesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEmployeesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.adapter = adapter

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener{
            override fun onItemClicked(data: Any?) {
                val action = EmployeeListFragmentDirections.actionEmployeeListFragmentToCaptureFragment(data as EmployeeEntity)
                findNavController().navigate(action)
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

        viewModel.getRecyclerData("")

        binding.btnSearch.setOnClickListener {
            viewModel.getRecyclerData(binding.etSearch.toString())
        }

        binding.etSearch.addTextChangedListener {
            binding.btnSearch.isEnabled = !it.isNullOrEmpty()
            if (it.isNullOrEmpty()){
                viewModel.getRecyclerData("")
            }
        }

    }

}