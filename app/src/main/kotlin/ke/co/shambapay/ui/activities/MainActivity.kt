package ke.co.shambapay.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ke.co.shambapay.R
import ke.co.shambapay.data.model.SettingsEntity
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.databinding.ActivityMainBinding
import ke.co.shambapay.domain.utils.QueryBuilder
import ke.co.shambapay.ui.UiGlobalState
import org.koin.android.ext.android.inject

class MainActivity: AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val state: UiGlobalState by inject()
    private val activity = this
    private val firebaseRef = FirebaseDatabase.getInstance().getReference(QueryBuilder.getSettings(state.user!!.companyId))

    private val settingsListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                val settingsEntity: SettingsEntity? = snapshot.getValue(SettingsEntity::class.java)
                if (settingsEntity != null){
                    state.settings = settingsEntity
                }
            } catch (e: Exception){
                state.logout(activity)
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(state.user?.userType as UserType) {
            UserType.ADMIN -> {
                binding.bottomNav.inflateMenu(R.menu.bottom_menu_admin)
            }

            UserType.OWNER -> {
                binding.bottomNav.inflateMenu(R.menu.bottom_menu_owner)
            }

            UserType.SUPERVISOR -> {
                binding.bottomNav.inflateMenu(R.menu.bottom_menu_supervisor)
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        binding.txtLogout.setOnClickListener {
            state.logout(this)
        }
    }

    override fun onResume() {
        super.onResume()
        addDataListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeDataListeners()
    }

    private fun addDataListeners(){
        firebaseRef.addValueEventListener(settingsListener)
    }

    private fun removeDataListeners(){
        firebaseRef.removeEventListener(settingsListener)
    }
}