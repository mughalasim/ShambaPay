package ke.co.shambapay.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ke.co.shambapay.R
import ke.co.shambapay.databinding.FragmentProfileBinding
import ke.co.shambapay.ui.UiGlobalState
import org.koin.android.ext.android.inject


class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private val state: UiGlobalState by inject()

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.widgetUser.setUp(state.user!!, state.settings!!)

        binding.bannerPassword.setUp(getString(R.string.txt_password_update))

    }

}