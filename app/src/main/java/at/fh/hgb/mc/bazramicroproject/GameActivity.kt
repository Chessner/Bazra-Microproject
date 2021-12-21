package at.fh.hgb.mc.bazramicroproject

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import at.fh.hgb.mc.bazramicroproject.databinding.ActivityGameBinding
import at.fh.hgb.mc.bazramicroproject.networking.DrawResponse
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson

const val GAME_STATE: String = "currentGameState"
const val GOTTEN_CARDS: String = "gottenCards"
const val FIELD_STATE: String = "currentFieldState"
const val HAND_STATE: String = "currentHandState"
const val FIELD_STATE_LIST: String = "listOfFieldStates"
const val HAND_STATE_LIST: String = "listOfHandStates"
class GameActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarGame.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_game)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_game, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val sp = getSharedPreferences(BAZRA_PREFERENCES, MODE_PRIVATE)
        if(sp.getString(GOTTEN_CARDS, "") == "" ){
            val l = mutableListOf<DrawResponse.Card>()
            sp.edit().putString(GOTTEN_CARDS,Gson().toJson(l)).apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.game, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_game)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}