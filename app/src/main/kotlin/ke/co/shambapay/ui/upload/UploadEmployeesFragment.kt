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
import ke.co.shambapay.databinding.FragmentRegisterBinding
import ke.co.shambapay.databinding.FragmentUploadEmployeesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.InputStream

class UploadEmployeesFragment: Fragment() {

    private val viewModel: UploadViewModel by viewModel()
    lateinit var binding: FragmentUploadEmployeesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadEmployeesBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.canUploadEmployees.observe(viewLifecycleOwner){
            binding.btnUpload.isEnabled = it
        }

        viewModel.state.observe(viewLifecycleOwner){
            when(it){

                is UploadViewModel.State.Success -> {

                }

                is UploadViewModel.State.UpdateUI ->{
                    binding.progressBar.isVisible = it.showLoading
                    binding.txtMessage.text = it.message
                    binding.txtMessage.isVisible = it.message.isNotEmpty()
                }
            }
        }

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    println(it)
                    viewModel.setInputStream(context?.contentResolver?.openInputStream(it))
                    binding.txtMessage.text = "Selected: ${it.lastPathSegment}"
                }

            }
        }

        binding.btnSelectFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            launcher.launch(Intent.createChooser(intent, "Open CSV for upload"))
        }

        binding.btnUpload.setOnClickListener {
            viewModel.uploadEmployees()
        }


    }


}