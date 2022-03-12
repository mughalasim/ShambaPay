package ke.co.shambapay.ui.upload

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import ke.co.shambapay.databinding.FragmentUploadWorkBinding
import ke.co.shambapay.domain.base.BaseState
import org.koin.androidx.viewmodel.ext.android.viewModel

class UploadWorkFragment: Fragment() {

    private val viewModel: UploadViewModel by viewModel()
    lateinit var binding: FragmentUploadWorkBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadWorkBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.canUploadWork.observe(viewLifecycleOwner){
            binding.btnUpload.isEnabled = it
        }

        viewModel.state.observe(viewLifecycleOwner){
            when(it){

                is BaseState.Success<*> -> {
                    binding.etYear.setText("")
                    binding.etMonth.setText("")
                }

                is BaseState.UpdateUI -> {
                    binding.widgetLoading.update(it.message, it.showLoading)
                }
            }
        }

        binding.etYear.addTextChangedListener {
            viewModel.validateUploadWork(binding.etMonth.text.toString(), it.toString())
        }

        binding.etMonth.addTextChangedListener {
            viewModel.validateUploadWork(it.toString(), binding.etYear.text.toString())
        }

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    println(it)
                    viewModel.setInputStream(context?.contentResolver?.openInputStream(it))
                    binding.widgetLoading.update("Selected: ${it.lastPathSegment}", false)
                }

            }
        }

        binding.btnSelectFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            launcher.launch(Intent.createChooser(intent, "Open CSV for upload"))
        }


        binding.btnUpload.setOnClickListener {
            viewModel.uploadWork()
        }


    }


}