package ke.co.shambapay.ui.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ke.co.shambapay.data.model.CompanyEntity
import ke.co.shambapay.databinding.FragmentCompaniesBinding
import ke.co.shambapay.domain.base.BaseState
import ke.co.shambapay.ui.adapter.CustomAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompanyListFragment: Fragment() {

    private val viewModel: CompanyListViewModel by viewModel()
    private val adapter =  CustomAdapter(mutableListOf<CompanyEntity>())
    lateinit var binding: FragmentCompaniesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCompaniesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.adapter = adapter

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener{
            override fun onItemClicked(data: Any?) {
//                val action = EmployeeListFragmentDirections.actionEmployeeListFragmentToCaptureFragment(data as EmployeeEntity)
//                findNavController().navigate(action)
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

        viewModel.getRecyclerData()

    }

}