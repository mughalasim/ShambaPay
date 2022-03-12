package ke.co.shambapay.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ke.co.shambapay.databinding.FragmentSettingsUpdateBinding

class SettingsUpdateFragment : Fragment() {

    lateinit var binding: FragmentSettingsUpdateBinding

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        binding = FragmentSettingsUpdateBinding.inflate(layoutInflater)
        return binding.root
    }

}