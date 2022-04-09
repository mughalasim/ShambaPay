package ke.co.shambapay.ui.capture

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
import ke.co.shambapay.databinding.FragmentCaptureBinding
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.base.BaseState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CaptureFragment: Fragment() {

    private val viewModel: CaptureViewModel by viewModel()
    lateinit var binding: FragmentCaptureBinding
    private val args: CaptureFragmentArgs by navArgs()
    private val globalState: UiGlobalState by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCaptureBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.widgetEmployee.setUp(args.employeeEntity)

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
            viewModel.updateEmployeeWork(args.employeeEntity.id)
        }

        binding.btnCancel.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.etUnit.addTextChangedListener {
            viewModel.validate(it.toString(), binding.spinnerJobType.selectedItemPosition)
        }

        binding.spinnerJobType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.validate(binding.etUnit.text.toString(), binding.spinnerJobType.selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerJobType.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1, globalState.getDropDownOptions()
        )

    }

}