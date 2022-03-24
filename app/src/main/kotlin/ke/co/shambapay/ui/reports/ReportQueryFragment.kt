package ke.co.shambapay.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ke.co.shambapay.data.model.ReportType
import ke.co.shambapay.databinding.FragmentReportQueryBinding
import org.koin.android.ext.android.inject

class ReportQueryFragment : Fragment() {

    lateinit var binding: FragmentReportQueryBinding
    private val viewModel: ReportViewModel by inject()

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        binding = FragmentReportQueryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.canSubmit.observe(viewLifecycleOwner){
            binding.btnGenerate.isEnabled = it
        }

        binding.spinnerReportType.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ReportType.values())


        binding.etYear.addTextChangedListener {
            viewModel.validate(it.toString(), binding.etMonth.text.toString())
        }

        binding.etMonth.addTextChangedListener {
            viewModel.validate(binding.etYear.text.toString(), it.toString())
        }

        binding.btnGenerate.setOnClickListener {
            findNavController().navigate(ReportQueryFragmentDirections.actionReportQueryFragmentToReportViewFragment(
                year = binding.etYear.text.toString().toInt(),
                month = binding.etMonth.text.toString().toInt(),
                reportType = ReportType.values()[binding.spinnerReportType.selectedItemPosition],
                employee = null
            ))
        }

    }

}