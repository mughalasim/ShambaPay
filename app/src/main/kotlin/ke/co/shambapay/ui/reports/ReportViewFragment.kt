package ke.co.shambapay.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ke.co.shambapay.data.model.ReportEntity
import ke.co.shambapay.data.model.ReportType
import ke.co.shambapay.databinding.FragmentReportViewBinding
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.adapter.CustomAdapter
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.utils.PDFConverter
import org.koin.android.ext.android.inject

class ReportViewFragment : Fragment() {

    lateinit var binding: FragmentReportViewBinding
    private val adapter =  CustomAdapter(mutableListOf<ReportEntity>())
    private val args: ReportViewFragmentArgs by navArgs()
    private val viewModel: ReportViewModel by inject()
    private val globalState: UiGlobalState by inject()

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        binding = FragmentReportViewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.banner.setUp("Report: " + args.reportInputData.reportType.name.lowercase().replace("_", " ") +
                " ${args.reportInputData.startDate.toString("d/MM/yyyy")} to ${args.reportInputData.endDate.toString("d/MM/yyyy")}")

        binding.recycler.adapter = adapter

        viewModel.state.observe(viewLifecycleOwner){
            when(it){
                is BaseState.UpdateUI -> {
                    binding.widgetLoading.update(it.message, it.showLoading)
                }
                is BaseState.Success<*> -> {
                    binding.widgetLoading.update("", false)
                    when(it.data){
                        is ReportViewModel.ViewModelOutput.Report -> {
                            adapter.updateData(it.data.list as MutableList<ReportEntity>)
                        }
                    }
                }
                is BaseState.Logout -> {
                    globalState.logout(activity!!)
                }
            }
        }

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener{
            override fun onItemClicked(data: Any?) {}
        })

        binding.txtBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.txtSave.setOnClickListener {
            val dateString = args.reportInputData.startDate.toString("yyyy_MM")
            val fileName = when (args.reportInputData.reportType){
                ReportType.PAYROLL_SUMMARY -> {
                    "Payroll_Report_$dateString"
                }
                ReportType.EMPLOYEE_PERFORMANCE -> {
                    "Employee_Performance_Report_${dateString}_${args.reportInputData.employee?.fetchFullNameUnderScore()}"
                }
                ReportType.BANK_PAYMENT_DETAILS -> {
                    "Bank_Payment_Report_$dateString"
                }
                ReportType.PAYSLIP -> {
                    "Employee_Payslip_${dateString}_${args.reportInputData.employee?.fetchFullNameUnderScore()}"
                }
            }
            PDFConverter().createPdf(context!!, binding.container, activity!!, fileName)
        }

        viewModel.fetchReport(args.reportInputData)

    }

}