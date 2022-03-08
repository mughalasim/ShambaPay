package ke.co.shambapay.ui.capture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ke.co.shambapay.R
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.databinding.FragmentCaptureBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CaptureFragment: Fragment() {

    private val viewModel: CaptureViewModel by viewModel()
    lateinit var binding: FragmentCaptureBinding
    private val args: CaptureFragmentArgs by navArgs()

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
                is CaptureViewModel.State.UpdateUI -> {
                    binding.btnCapture.isEnabled = !it.showLoading
                    binding.progressBar.isVisible = it.showLoading
                    binding.txtMessage.text = it.message
                    binding.txtMessage.isVisible = it.message.isNotEmpty()
                }
                is CaptureViewModel.State.Success -> {

                }
            }
        }

        viewModel.canSubmit.observe(viewLifecycleOwner){
            binding.btnCapture.isVisible = it
        }

        binding.btnCancel.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.etUnit.addTextChangedListener {
//            viewModel.validate(it.toString(), binding.spinnerJobType.selectedItem.toString())
        }


//        binding.spinnerJobType.adapter = ArrayAdapter(requireContext(),
//            androidx.appcompat.R.id.list_item, arrayOf("A", "B", "C"))

    }

}