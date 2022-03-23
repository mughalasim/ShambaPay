package ke.co.shambapay.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import ke.co.shambapay.data.model.ReportType
import ke.co.shambapay.databinding.FragmentReportQueryBinding
import ke.co.shambapay.domain.base.BaseState
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


        viewModel.state.observe(viewLifecycleOwner){
            when(it){
                is BaseState.UpdateUI -> {

                }
                is BaseState.Success<*> -> {

                }
            }
        }

        viewModel.canSubmit.observe(viewLifecycleOwner){
            binding.btnGenerate.isEnabled = it
        }

        binding.spinnerReportType.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ReportType.values())


        binding.etYear.addTextChangedListener {
            viewModel.validate(binding.spinnerReportType.selectedItemPosition, it.toString(), binding.etMonth.text.toString())
        }

        binding.etMonth.addTextChangedListener {
            viewModel.validate(binding.spinnerReportType.selectedItemPosition, binding.etYear.text.toString(), it.toString())
        }

        binding.spinnerReportType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.validate(binding.spinnerReportType.selectedItemPosition, binding.etYear.text.toString(), binding.etMonth.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

//        binding.btnGenerate.setOnClickListener {
//            val action = SettingsFragmentDirections.actionSettingsFragmentToSettingsUpdateFragment()
//            findNavController().navigate(action)
//        }

    }

}