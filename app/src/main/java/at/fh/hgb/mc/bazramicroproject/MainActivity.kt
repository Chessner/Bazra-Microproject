package at.fh.hgb.mc.bazramicroproject

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import at.fh.hgb.mc.bazramicroproject.databinding.ActivityMainBinding

const val TAG: String = "Bazra"
const val MAIN_TAG: String = "MainActivity"
const val GAMESTATE_LIST = "list of GameStates"
const val BAZRA_PREFERENCES = "Bazra_Preferences"
class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityMainCreateGameButton.setOnClickListener(this)
        binding.activityMainMySavedGamesButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var i: Intent? = null;
        when(v?.id){
            R.id.activity_main_createGameButton -> {
                i = Intent(this,CreateGameActivity::class.java)
            }
            R.id.activity_main_mySavedGamesButton -> {
                i = Intent(this,SavedGamesActivity::class.java)
            }
            else -> {
                Log.i(MAIN_TAG, "MainActivity::onClick() unexpected View encountered.")
                Log.i(TAG, "MainActivity::onClick() unexpected View encountered.")
            }
        }
        if(i != null) startActivity(i)
    }
}