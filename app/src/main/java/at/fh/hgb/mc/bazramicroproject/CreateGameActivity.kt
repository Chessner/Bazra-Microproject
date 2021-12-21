package at.fh.hgb.mc.bazramicroproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import at.fh.hgb.mc.bazramicroproject.databinding.ActivityCreateGameBinding
import at.fh.hgb.mc.bazramicroproject.interfaces.IAPIResponse
import at.fh.hgb.mc.bazramicroproject.interfaces.VolleyCallback
import at.fh.hgb.mc.bazramicroproject.networking.NetworkingSingleton

class CreateGameActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityCreateGameBinding
    lateinit var toast: Toast


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toast = Toast(this)

        binding.activityCreateGamePlayAloneButton.setOnClickListener(this)
        binding.activityCreateGamePlayFriendsButton.setOnClickListener(this)
        binding.activityCreateGamePlayAiButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var i: Intent? = null
        var state: GameState? = null
        when (v?.id) {
            R.id.activity_create_game_playAloneButton -> {
                i = Intent(this, GameActivity::class.java)
                state = GameState(
                    binding.activityCreateGameGameNameInput.text.toString(),
                    Integer.parseInt(binding.activityCreateGameNrOfDecksInput.text.toString()),
                    Integer.parseInt(binding.activityCreateGameNrOfPointsInput.text.toString()),
                    GameType.SOLO
                )
            }
            R.id.activity_create_game_playFriendsButton -> {
                toast.cancel()
                toast = Toast.makeText(
                    this,
                    R.string.notImplemented_toast,
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
            R.id.activity_create_game_playAiButton -> {
                toast.cancel()
                toast = Toast.makeText(
                    this,
                    R.string.notImplemented_toast,
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
            else -> {
                Log.i(TAG, "CreateGameActivity::onClick() unexpected view encountered.")
            }
        }
        if (i != null) {
            //Make a shuffle request = get a new deck of cards from the api. primarily used for deck_id
            NetworkingSingleton.getInstance(this).makeShuffleRequest(state!!.ogNrOfDecks, this,
                object : VolleyCallback {
                    override fun onSuccess(result: IAPIResponse) {
                        state.deckId = result.deck_id
                        state.putExtra(i, result)

                        startActivity(i)
                    }
                })

        }
    }
}

