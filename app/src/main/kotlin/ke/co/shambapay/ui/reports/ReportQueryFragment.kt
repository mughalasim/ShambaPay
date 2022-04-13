package ke.co.shambapay.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ke.co.shambapay.data.model.ReportType
import ke.co.shambapay.databinding.FragmentReportQueryBinding
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.utils.showDatePicker
import ke.co.shambapay.utils.toMonthYearString
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

                }
                is BaseState.Logout -> {
                    globalState.logout(activity!!)
                }
            }
        }

        binding.spinnerReportType.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ReportType.values())

        binding.txtStartDate.setOnClickListener {
            activity?.showDatePicker {
                binding.txtStartDate.text = it.toMonthYearString()
                viewModel.setStartDate(it)
            }
        }

        binding.txtEndDate.setOnClickListener {
            activity?.showDatePicker {
                binding.txtEndDate.text = it.toMonthYearString()
                viewModel.setEndDate(it)
            }
        }

        binding.btnGenerate.setOnClickListener {
            val reportPosition = binding.spinnerReportType.selectedItemPosition

            if(reportPosition == 1 || reportPosition == 3){
                // Select an employee
                findNavController().navigate(
                    ReportQueryFragmentDirections.actionReportQueryFragmentToEmployeeListFragment(
                        companyId = globalState.settings!!.companyId,
                        reportInputData = viewModel.getReportInputData(reportPosition)
                    )
                )
            } else {
                // view report
                findNavController().navigate(
                    ReportQueryFragmentDirections.actionReportQueryFragmentToReportViewFragment(
                        reportInputData = viewModel.getReportInputData(reportPosition)
                    )
                )
            }
        }

    }

}