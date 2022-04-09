package ke.co.shambapay.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ke.co.shambapay.R
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.databinding.FragmentUsersBinding
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.adapter.CustomAdapter
import ke.co.shambapay.ui.base.BaseState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserListFragment: Fragment() {

    private val viewModel: UserListViewModel by viewModel()
    private val adapter =  CustomAdapter(mutableListOf<UserEntity>())
    lateinit var binding: FragmentUsersBinding
    private val args: UserListFragmentArgs by navArgs()
    private val globalState: UiGlobalState by inject()

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
                val entity = data as UserEntity
                if (entity.userType == UserType.SUPERVISOR){
                    findNavController().navigate(UserListFragmentDirections.
                    actionUserListFragmentToUserUpdateFragment(entity, UserType.SUPERVISOR, entity.companyId))
                }
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
                is BaseState.Success<*> -> {
                    globalState.logout(activity!!)
                }
                is BaseState.Logout -> {
                    globalState.logout(activity!!)
                }
            }
        }

        viewModel.getAllUsers(args.companyId)

        binding.btnEmployees.setOnClickListener {
            val action = UserListFragmentDirections.actionUserListFragmentToEmployeeListFragment(args.companyId)
            findNavController().navigate(action)
        }

        binding.btnAdd.text = getString(R.string.txt_add_, UserType.SUPERVISOR.name.lowercase())
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(UserListFragmentDirections.
            actionUserListFragmentToUserUpdateFragment(null, UserType.SUPERVISOR, args.companyId))
        }

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.btnSetDefault.isVisible = args.canSetAsDefault
        binding.btnSetDefault.setOnClickListener {
            viewModel.setDefaultCompanyId(args.companyId)
        }
    }

}