package ke.co.shambapay.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.databinding.FragmentUsersBinding
import ke.co.shambapay.domain.base.BaseState
import ke.co.shambapay.ui.adapter.CustomAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserListFragment: Fragment() {

    private val viewModel: UserListViewModel by viewModel()
    private val adapter =  CustomAdapter(mutableListOf<UserEntity>())
    lateinit var binding: FragmentUsersBinding
    private val args: UserListFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUsersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.adapter = adapter
        binding.txtTitle.setText(args.companyName)

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener{
            override fun onItemClicked(data: Any?) {
//                val action = EmployeeListFragmentDirections.actionEmployeeListFragmentToCaptureFragment(data as UserEntity)
//                findNavController().navigate(action)
            }
        })

        viewModel.users.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }

        viewModel.state.observe(viewLifecycleOwner) {
            when(it){
                is BaseState.UpdateUI -> {
                    binding.widgetLoading.update(it.message, it.showLoading)
                }
                is BaseState.Success<*> -> {}

            }
        }

        viewModel.getAllUsers(args.companyId)

        binding.btnEmployees.setOnClickListener {
            val action = UserListFragmentDirections.actionUserListFragmentToEmployeeListFragment(args.companyId)
            findNavController().navigate(action)
        }

        binding.btnRegister.setOnClickListener {
//            val action = UserListFragmentDirections.actionUserListFragmentToEmployeeListFragment(args.companyId)
//            findNavController().navigate(action)
        }

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

    }

}