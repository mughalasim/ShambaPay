package ke.co.shambapay.ui.sms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ke.co.shambapay.data.intent.BulkSMSData
import ke.co.shambapay.databinding.FragmentBulkSmsBinding
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.adapter.CustomAdapter
import ke.co.shambapay.ui.base.BaseState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class BulkSMSFragment : Fragment() {

    lateinit var binding: FragmentBulkSmsBinding
    private val viewModel: BulkSMSViewModel by viewModel()
    private val adapter =  CustomAdapter(mutableListOf<BulkSMSData>())
    private val globalState: UiGlobalState by inject()

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        binding = FragmentBulkSmsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.adapter = adapter
        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener{
            override fun onItemClicked(data: Any?) {

            }
        })

        viewModel.state.observe(viewLifecycleOwner){
            when(it){
                is BaseState.UpdateUI -> {
                    binding.widgetLoading.update(it.message, it.showLoading)
                    binding.btnSend.isEnabled = !it.showLoading
                }
                is BaseState.Success<*> -> {
                    binding.widgetLoading.update("", false)
                    val list = it.data as List<BulkSMSData>
                    adapter.updateData(list)
                    binding.btnSend.isEnabled = list.isNotEmpty()
                }
                is BaseState.Logout -> {
                    globalState.logout(activity!!)
                }
            }
        }

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.btnSend.setOnClickListener {
            activity?.let {
                viewModel.sendBulkSMS(it)
            }
        }

        viewModel.updateList()

    }

}