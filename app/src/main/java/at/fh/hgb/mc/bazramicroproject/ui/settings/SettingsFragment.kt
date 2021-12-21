package at.fh.hgb.mc.bazramicroproject.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import at.fh.hgb.mc.bazramicroproject.*
import at.fh.hgb.mc.bazramicroproject.databinding.FragmentSettingsBinding
import at.fh.hgb.mc.bazramicroproject.GameState
import at.fh.hgb.mc.bazramicroproject.networking.DrawResponse
import at.fh.hgb.mc.bazramicroproject.ui.game.CARDS_TO_DRAW_FOR_HAND
import com.google.gson.Gson
import kotlin.system.exitProcess

class SettingsFragment : Fragment(), View.OnClickListener {

    //private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private lateinit var state: GameState
    private lateinit var handCards: MutableList<DrawResponse.Card>
    private lateinit var fieldCards: MutableList<DrawResponse.Card>
    private lateinit var deckId: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //settingsViewModel =
          //  ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setOnClickListeners()

        val sp = this.context?.getSharedPreferences(BAZRA_PREFERENCES, Context.MODE_PRIVATE)
        val gson = Gson()
        state = gson.fromJson(sp?.getString(GAME_STATE, null), GameState::class.java)

        fieldCards = mutableListOf()
        handCards = mutableListOf()

        fieldCards = gson.fromJson(
            sp?.getString(FIELD_STATE, null),
            MutableList::class.java
        ) as MutableList<DrawResponse.Card>
        handCards = gson.fromJson(
            sp?.getString(HAND_STATE, null),
            MutableList::class.java
        ) as MutableList<DrawResponse.Card>

        return root
    }

    private fun setOnClickListeners() {
        binding.fragmentSettingsQuitToHomeScreenButton.setOnClickListener(this)
        binding.fragmentSettingsSaveGameButton.setOnClickListener(this)
        binding.fragmentSettingsQuitToAppHomeScreenButton.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fragment_settings_quitToHomeScreenButton -> {
                activity?.finishAffinity()
                exitProcess(0)
            }
            R.id.fragment_settings_quitToAppHomeScreenButton -> {
                val i = Intent(this@SettingsFragment.context, MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }


            R.id.fragment_settings_saveGameButton -> {
                val gson = Gson()

                if (binding.editTextTextPersonName.text.toString() != "") {
                    val s = binding.editTextTextPersonName.text.toString()
                    if (s != "") {
                        state.gameName = s
                    }
                }

                val sp = this.context?.getSharedPreferences(BAZRA_PREFERENCES, Context.MODE_PRIVATE)
                val l = sp?.getString(GAMESTATE_LIST, null)
                val fL = sp?.getString(FIELD_STATE_LIST,null)
                val hL = sp?.getString(HAND_STATE_LIST,null)
                val spE = sp?.edit()
                if (l == null) {
                    val list = mutableListOf<GameState>()
                    list.add(state)
                    val json = gson.toJson(list)
                    spE?.putString(GAMESTATE_LIST, json)
                   // spE?.apply()
                } else {
                    //val list = gson.fromJson<MutableList<GameState>>(sp?.getString(GAMESTATE_LIST,null),GameState::class.java)
                    val list = gson.fromJson<MutableList<GameState>>(l, MutableList::class.java)
                    list.add(state)
                    spE?.putString(GAMESTATE_LIST, gson.toJson(list))
                   // spE?.apply()
                }

                if(fL == null){
                    val list  = mutableListOf<MutableList<DrawResponse.Card>>()
                    list.add(fieldCards)
                    val json = gson.toJson(list)
                    spE?.putString(FIELD_STATE_LIST, json)

                } else{
                    val list = gson.fromJson<MutableList<MutableList<DrawResponse.Card>>>(fL, MutableList::class.java)
                    list.add(fieldCards)
                    spE?.putString(FIELD_STATE_LIST,gson.toJson(list))
                   // spE?.apply()
                }

                if(hL == null){
                    val list  = mutableListOf<MutableList<DrawResponse.Card>>()
                    list.add(handCards)
                    val json = gson.toJson(list)
                    spE?.putString(HAND_STATE_LIST, json)
                   // spE?.apply()
                } else{
                    val list = gson.fromJson<MutableList<MutableList<DrawResponse.Card>>>(fL, MutableList::class.java)
                    list.add(handCards)
                    spE?.putString(HAND_STATE_LIST,gson.toJson(list))
                    //spE?.apply()
                }

                spE?.apply()

            }
        }
    }
}