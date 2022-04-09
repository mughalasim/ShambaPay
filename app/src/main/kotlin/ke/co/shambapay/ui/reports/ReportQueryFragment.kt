package ke.co.shambapay.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ke.co.shambapay.data.model.ReportType
import ke.co.shambapay.databinding.FragmentReportQueryBinding
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.base.BaseState
import org.joda.time.DateTime
import org.koin.android.ext.android.inject

class ReportQueryFragment : Fragment() {

    lateinit var binding: FragmentReportQueryBinding
    private val viewModel: ReportViewModel by inject()
    private val globalState: UiGlobalState by inject()

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        binding = FragmentReportQueryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.canSubmit.observe(viewLifecycleOwner){
            binding.btnGenerate.isEnabled = it
        }

        viewModel.state.observe(viewLifecycleOwner){ state ->
            when(state){
                is BaseState.UpdateUI -> {
                    binding.widgetLoading.update(state.message, state.showLoading)
                }
                is BaseState.Success<*> -> {
                    binding.widgetLoading.update("", false)
                    when(state.data){
                        is ReportViewModel.ViewModelOutput.Employee -> {
                            binding.spinnerEmployee.adapter =
                                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, state.data.list)
                        }
                    }
                }
                is BaseState.Logout -> {
                    globalState.logout(activity!!)
                }
            }
        }

        binding.spinnerReportType.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ReportType.values())

        binding.spinnerReportType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.llSelectEmployee.isVisible = (position == 1 || position == 3)
            }

        }


        binding.etYear.addTextChangedListener {
            viewModel.validate(it.toString(), binding.etMonth.text.toString())
        }

        binding.etMonth.addTextChangedListener {
            viewModel.validate(binding.etYear.text.toString(), it.toString())
        }

        binding.btnGenerate.setOnClickListener {
            findNavController().navigate(ReportQueryFragmentDirections.actionReportQueryFragmentToReportViewFragment(
                date = DateTime.now().withDate(binding.etYear.text.toString().toInt(), binding.etMonth.text.toString().toInt(), 1),
                reportType = ReportType.values()[binding.spinnerReportType.selectedItemPosition],
                employee = viewModel.getEmployee(binding.spinnerEmployee.selectedItemPosition)
            ))
        }

        viewModel.fetchEmployees()

    }

}