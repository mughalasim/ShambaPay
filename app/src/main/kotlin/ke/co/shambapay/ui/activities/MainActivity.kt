package ke.co.shambapay.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import ke.co.shambapay.R
import ke.co.shambapay.data.model.SettingsEntity
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.databinding.ActivityMainBinding
import ke.co.shambapay.domain.Failures
import ke.co.shambapay.domain.QueryBuilder
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.ui.UiGlobalState
import org.koin.android.ext.android.inject

class MainActivity: AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val state: UiGlobalState by inject()

    private val eventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.e("TEST", snapshot.toString())
            try {
                val settingsEntity: SettingsEntity? = snapshot.getValue(SettingsEntity::class.java)
                if (settingsEntity != null){
                    state.settings = settingsEntity
                }
            } catch (e: Exception){
                logOut()
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.banner.setUp(getString(R.string.app_name))

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

        FirebaseDatabase.getInstance().getReference(QueryBuilder.getSettings(state.user!!.companyId)).addValueEventListener(eventListener)

    }

    fun logOut(){
        FirebaseAuth.getInstance().signOut()
        state.clear()
        startActivity(Intent(this, StartActivity::class.java).apply {
            /*If you want to add any intent extras*/ })
        finish()
    }
}