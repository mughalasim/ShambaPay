package ke.co.shambapay.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ke.co.shambapay.R
import ke.co.shambapay.data.model.JobType
import ke.co.shambapay.databinding.FragmentSettingsUpdateBinding
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.base.BaseState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsUpdateFragment : Fragment() {

    lateinit var binding: FragmentSettingsUpdateBinding
    private val viewModel: SettingsUpdateViewModel by viewModel()
    private val args: SettingsUpdateFragmentArgs by navArgs()
    private val globalState: UiGlobalState by inject()

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        binding = FragmentSettingsUpdateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.spinnerJobType.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1, JobType.values()
        )

        viewModel.state.observe(viewLifecycleOwner){
            when(it){
                is BaseState.UpdateUI -> {
                    binding.btnCapture.isEnabled = !it.showLoading
                    binding.widgetLoading.update(it.message, it.showLoading)
                }
                is BaseState.Success<*> -> {
                    activity?.onBackPressed()
                }
                is BaseState.Logout -> {
                    globalState.logout(activity!!)
                }
            }
        }

        viewModel.canSubmit.observe(viewLifecycleOwner){
            binding.btnCapture.isVisible = it
        }

        binding.btnCapture.setOnClickListener {
            viewModel.setJobRate()
        }

        binding.btnCancel.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.etMeasured.addTextChangedListener {
            viewModel.validate(binding.spinnerJobType.selectedItemPosition, it.toString(), binding.etRate.text.toString())
        }

        binding.etRate.addTextChangedListener {
            viewModel.validate(binding.spinnerJobType.selectedItemPosition, binding.etMeasured.text.toString(), it.toString())
        }

        binding.spinnerJobType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.validate(position, binding.etMeasured.text.toString(), binding.etRate.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        args.jobRateEntity?.let {
            viewModel.setRateId(it.rateId)
            binding.etRate.setText(it.rate.toString())
            binding.etMeasured.setText(it.measurement)
            binding.btnCapture.setText(getString(R.string.txt_update))
        }

        binding.btnDelete.isVisible = args.jobRateEntity != null
        binding.btnDelete.setOnClickListener {
            viewModel.deleteJobRate()
        }

        binding.spinnerJobType.setSelection(JobType.valueOf(args.jobRateEntity?.jobType?.name ?: JobType.PICKING.name).ordinal)

    }

}