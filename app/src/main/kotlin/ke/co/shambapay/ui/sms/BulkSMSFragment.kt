package ke.co.shambapay.ui.sms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ke.co.shambapay.databinding.FragmentBulkSmsBinding
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.adapter.CustomAdapter
import org.koin.android.ext.android.inject

class BulkSMSFragment : Fragment() {

    lateinit var binding: FragmentBulkSmsBinding
    private val adapter =  CustomAdapter(mutableListOf<BulkSMSFragment>())
    private val globalState: UiGlobalState by inject()

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        binding = FragmentBulkSmsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.adapter = adapter


    }

}