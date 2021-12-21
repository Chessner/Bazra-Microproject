package at.fh.hgb.mc.bazramicroproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.fh.hgb.mc.bazramicroproject.adapters.MyRecyclerAdapter
import at.fh.hgb.mc.bazramicroproject.databinding.ActivitySavedGamesBinding
import at.fh.hgb.mc.bazramicroproject.interfaces.IonItemClick
import at.fh.hgb.mc.bazramicroproject.networking.DrawResponse
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap

class SavedGamesActivity : AppCompatActivity(), IonItemClick {
    private lateinit var binding: ActivitySavedGamesBinding
    private lateinit var toast: Toast
    private lateinit var data: MutableList<GameState>
    private lateinit var fieldCardData: MutableList<MutableList<DrawResponse.Card>>
    private lateinit var handCardData: MutableList<MutableList<DrawResponse.Card>>
    private lateinit var bufferCardData: ArrayList<ArrayList<LinkedTreeMap<String,String>>>
    private lateinit var currData: ArrayList<LinkedTreeMap<String, Any>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toast = Toast(this)

        //Open the shared preferences and get the list of all the saved gamestates
        val sp = getSharedPreferences(BAZRA_PREFERENCES, MODE_PRIVATE)
        val gStateString = sp.getString(GAMESTATE_LIST, null)
        val gson = Gson()

        if (gStateString != null) {
            //list of gamestates is not empty
            currData = gson.fromJson<ArrayList<LinkedTreeMap<String, Any>>>(
                gStateString,
                ArrayList::class.java
            )
            //convert the data given by Gson to sth usable
            data = DataStructureTranslator().arrayListLinkedTreeMapToMutableListForGameState(currData)
        } else {
            //list of gamestates is empty -> create a list. Doesn't save the list to preferences. Only used for the recyclerview
            data = mutableListOf()
        }

        val fieldStateString = sp.getString(FIELD_STATE_LIST, null)

        if (fieldStateString != null) {
            //list of gamestates is not empty
            bufferCardData = gson.fromJson<ArrayList<ArrayList<LinkedTreeMap<String, String>>>>(
                fieldStateString,
                ArrayList::class.java
            )
            //convert the data given by Gson to sth usable
            fieldCardData = DataStructureTranslator().arrayListOfArrayListOfLinkedTreeMapToMutableListOfListsForCards(bufferCardData)
        } else {
            //list of gamestates is empty -> create a list. Doesn't save the list to preferences. Only used for the recyclerview
            fieldCardData = mutableListOf()
        }


        val handStateString = sp.getString(HAND_STATE_LIST, null)

        if (handStateString != null) {
            //list of gamestates is not empty
            bufferCardData = gson.fromJson<ArrayList<ArrayList<LinkedTreeMap<String, String>>>>(
                handStateString,
                ArrayList::class.java
            )
            //convert the data given by Gson to sth usable
            handCardData = DataStructureTranslator().arrayListOfArrayListOfLinkedTreeMapToMutableListOfListsForCards(bufferCardData)
        } else {
            //list of gamestates is empty -> create a list. Doesn't save the list to preferences. Only used for the recyclerview
            handCardData = mutableListOf()
        }




        val rv: RecyclerView = findViewById(R.id.activity_saved_games_recyclerView)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = MyRecyclerAdapter(data, this)

    }




    @SuppressLint("ShowToast")
    override fun onItemClick(pos: Int) {
        var i: Intent? = null

        when (data[pos].gameType) {
            GameType.SOLO -> {
                i = Intent(this, GameActivity::class.java)
                data[pos].putExtra(i,null)

                i.putExtra(FIELD_STATE,Gson().toJson(fieldCardData[pos]))
                i.putExtra(HAND_STATE, Gson().toJson(handCardData[pos]))

            }
            GameType.FRIENDS -> {
                toast.cancel()
                toast = Toast.makeText(
                    this,
                    R.string.notImplemented_toast,
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
            GameType.AI -> {
                toast.cancel()
                toast = Toast.makeText(
                    this,
                    R.string.notImplemented_toast,
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
        if (i != null) startActivity(i)
    }
}