package at.fh.hgb.mc.bazramicroproject.ui.game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import at.fh.hgb.mc.bazramicroproject.*
import at.fh.hgb.mc.bazramicroproject.databinding.FragmentGameBinding
import at.fh.hgb.mc.bazramicroproject.GameState
import at.fh.hgb.mc.bazramicroproject.interfaces.IAPIResponse
import at.fh.hgb.mc.bazramicroproject.interfaces.VolleyCallback
import at.fh.hgb.mc.bazramicroproject.networking.DrawResponse
import at.fh.hgb.mc.bazramicroproject.networking.NetworkingSingleton
import coil.api.load
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap

const val CARDS_TO_DRAW_FOR_HAND: Int = 4
const val CARDS_TO_DRAW_FOR_FIELD: Int = 4

class GameFragment : Fragment(), View.OnLongClickListener, View.OnClickListener,
    NavController.OnDestinationChangedListener {
    private lateinit var state: GameState
    private var _binding: FragmentGameBinding? = null
    private lateinit var currentCardsOnHand: MutableList<DrawResponse.Card>
    private lateinit var currentCardsOnField: MutableList<DrawResponse.Card>
    private lateinit var currentCardsOnFieldImgView: MutableList<ImageView>
    private lateinit var currentCardsOnHandImgView: MutableList<ImageView>
    private var handCardFocused: ImageView? = null
    private lateinit var gottenCards: MutableList<DrawResponse.Card>
    private var haventReceivedCardsFromIntent = true

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        //Inflate fragment
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        val root: View = binding.root

        findNavController().addOnDestinationChangedListener(this)

        //Instantiate the cards imageView list
        currentCardsOnHandImgView = mutableListOf()
        currentCardsOnFieldImgView = mutableListOf()
        currentCardsOnHand = mutableListOf()
        currentCardsOnField = mutableListOf()
        gottenCards = mutableListOf()

        //Get the given GameState
        getGameState()

        //Get cards
        giveOutCards()


        return root
    }

    //Get GameState given by intent.putExtra
    private fun getGameState() {
        val i: Intent? = this.activity?.intent

        val dummyState = GameState("", 0, 0, GameType.SOLO)
        state = dummyState.getExtra(i)

        val fieldStateString = i?.getStringExtra(FIELD_STATE)
        if (fieldStateString != null) {
            haventReceivedCardsFromIntent = false
            val fieldStateList = Gson().fromJson<ArrayList<LinkedTreeMap<String, String>>>(
                fieldStateString,
                java.util.ArrayList::class.java
            )
            currentCardsOnField =
                DataStructureTranslator().arrayListLinkedTreeMapToMutableListOfCards(fieldStateList)
        }

        val handStateString = i?.getStringExtra(HAND_STATE)
        if(handStateString != null) {
            haventReceivedCardsFromIntent = false
            val handStateList = Gson().fromJson<ArrayList<LinkedTreeMap<String, String>>>(
                handStateString,
                java.util.ArrayList::class.java
            )
            currentCardsOnHand =
                DataStructureTranslator().arrayListLinkedTreeMapToMutableListOfCards(handStateList)
        }
    }


    private fun giveOutCards() {
        if(haventReceivedCardsFromIntent) {
            giveOutHandCards()
            giveOutFieldCards()
        } else {
            drawHandCards()
            drawFieldCards()
        }
    }

    private fun giveOutFieldCards() {
        state.currentNrOfCards -= CARDS_TO_DRAW_FOR_FIELD
        NetworkingSingleton.getInstance(this.requireContext()).makeDrawRequest(
            state.deckId.toString(),
            CARDS_TO_DRAW_FOR_FIELD,
            this.requireContext(),
            object : VolleyCallback {
                override fun onSuccess(result: IAPIResponse) {
                    val resultOfResponse = result as DrawResponse
                    for (i in 0..3) {
                        currentCardsOnField.add(resultOfResponse.cards[i])
                    }
                    //After the cards were drawn from the deck, draw the on the screen
                    drawFieldCards()
                }
            })
    }

    //Uses Networking Singleton to fetch a given number of cards, currently hardcoded to 8.
    private fun giveOutHandCards() {
        if (state.currentNrOfCards == 0 && gameEnd()) {
            gameWon()
        } else {

            var count = CARDS_TO_DRAW_FOR_HAND
            var buffer = state.currentNrOfCards - CARDS_TO_DRAW_FOR_HAND
            if (buffer < 0) {
                count += buffer
                buffer = 0
            }

            NetworkingSingleton.getInstance(this.requireContext()).makeDrawRequest(
                state.deckId.toString(),
                count,
                this.requireContext(),
                object : VolleyCallback {
                    override fun onSuccess(result: IAPIResponse) {
                        val resultOfResponse = result as DrawResponse
                        for (i in 0 until count) {
                            currentCardsOnHand.add(resultOfResponse.cards[i])
                        }


                        //After the cards were drawn from the deck, draw the on the screen
                        drawHandCards()
                    }
                })

            state.currentNrOfCards = buffer
        }
    }

    private fun gameEnd(): Boolean {
        //update points
        state.currentPoints += 30 //highest number of cards since playing alone...
        state.currentPoints += state.bazraCount * 10
        state.currentPoints += state.jackBazraCount * 30

        if (state.currentPoints >= state.pointsToWin) {
            //WIN
            return true
        } else {
            //RESHUFFLE and continue
            NetworkingSingleton.getInstance(requireContext())
                .makeReshuffleRequest(state.deckId!!, object : VolleyCallback {
                    override fun onSuccess(result: IAPIResponse) {}
                }, requireContext())
            state.nrOfMatchedCards = 0
            state.bazraCount = 0
            state.jackBazraCount = 0
            state.currentNrOfCards = 0

            binding.fragmentGamePointsText.text = state.currentPoints.toString()

            return false
        }
    }

    private fun gameWon() {
        Toast.makeText(
            requireContext(),
            "YOU WON! returning to main menu...",
            Toast.LENGTH_LONG
        ).show()

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val i = Intent(requireContext(), MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }, 2000)
    }


    private fun drawHandCards() {
        val parentLayout = binding.fragmentGameRelativeLayout

        for (index in 0 until currentCardsOnHand.size) {
            val card = currentCardsOnHand[index]
            val view = ImageView(this.requireContext())

            view.id = View.generateViewId()

            val lp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

            when (index) {
                0 -> {
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, parentLayout.id)
                    lp.addRule(RelativeLayout.ALIGN_LEFT, parentLayout.id)
                    view.layoutParams = lp
                }
                1 -> {
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, parentLayout.id)
                    lp.addRule(RelativeLayout.RIGHT_OF, currentCardsOnHandImgView[0].id)
                    view.layoutParams = lp
                }
                2 -> {
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, parentLayout.id)
                    lp.addRule(RelativeLayout.RIGHT_OF, currentCardsOnHandImgView[1].id)
                    view.layoutParams = lp
                }
                3 -> {
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, parentLayout.id)
                    lp.addRule(RelativeLayout.RIGHT_OF, currentCardsOnHandImgView[2].id)
                    view.layoutParams = lp
                }
            }
            view.load(card.image)

            if (currentCardsOnHandImgView.size > index) {
                parentLayout.removeView(currentCardsOnHandImgView[index])
                currentCardsOnHandImgView[index] = view
            } else {
                currentCardsOnHandImgView.add(view)
            }
            parentLayout.addView(view)

            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }


    }

    private fun drawFieldCards() {
        val parentLayout = binding.fragmentGameRelativeLayout

        for (index in 0 until currentCardsOnField.size) {
            val card = currentCardsOnField[index]
            val view = ImageView(this.requireContext())

            view.id = View.generateViewId()
            val lp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

            when (index % 4) {
                0 -> {
                    if (index == 0) {
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, parentLayout.id)
                    } else {
                        lp.addRule(RelativeLayout.BELOW, currentCardsOnFieldImgView[index - 4].id)
                    }
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, parentLayout.id)
                }
                1 -> {
                    if (index == 1) {
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, parentLayout.id)
                    } else {
                        lp.addRule(RelativeLayout.BELOW, currentCardsOnFieldImgView[index - 4].id)
                    }
                    lp.addRule(RelativeLayout.RIGHT_OF, currentCardsOnFieldImgView[index - 1].id)
                }
                2 -> {
                    if (index == 2) {
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, parentLayout.id)
                    } else {
                        lp.addRule(RelativeLayout.BELOW, currentCardsOnFieldImgView[index - 4].id)
                    }
                    lp.addRule(RelativeLayout.RIGHT_OF, currentCardsOnFieldImgView[index - 1].id)
                }
                3 -> {
                    if (index == 3) {
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, parentLayout.id)
                    } else {
                        lp.addRule(RelativeLayout.BELOW, currentCardsOnFieldImgView[index - 4].id)
                    }

                    lp.addRule(RelativeLayout.RIGHT_OF, currentCardsOnFieldImgView[index - 1].id)
                    view.layoutParams = lp
                }
            }
            view.layoutParams = lp
            view.load(card.image)

            if (currentCardsOnFieldImgView.size > index) {
                parentLayout.removeView(currentCardsOnFieldImgView[index])
                currentCardsOnFieldImgView[index] = view
            } else {
                currentCardsOnFieldImgView.add(view)
            }
            parentLayout.addView(view)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        findNavController().removeOnDestinationChangedListener(this)
    }

    private fun putGameStateInPref() {
        //Put the GameState in the preferences for SettingsFragment
        val gson = Gson()
        val sp = this.context?.getSharedPreferences(BAZRA_PREFERENCES, Context.MODE_PRIVATE)
        val spE = sp?.edit()
        spE?.putString(GAME_STATE, gson.toJson(state))
        spE?.putString(FIELD_STATE, gson.toJson(currentCardsOnField))
        spE?.putString(HAND_STATE, gson.toJson(currentCardsOnHand))
        spE?.apply()
    }

    private fun playCard(
        cardImg: ImageView,
        cardImgIndex: Int,
        card: DrawResponse.Card,
        cardIndex: Int
    ) {
        Log.i(TAG, "GameFragment::playCard()")

        // Find cards that match the played card and more.
        findAndRemoveMatchingCards(card)

        // Remove the played card from the hand.
        currentCardsOnHand.removeAt(cardIndex)
        currentCardsOnHandImgView.removeAt(cardImgIndex)
        binding.fragmentGameRelativeLayout.removeView(cardImg)

        // Check if the there are no more cards on hand.
        if (currentCardsOnHand.size == 0) {
            giveOutHandCards()
        }

        // Redraw the cards on the hand and on the field.
        drawHandCards()
        drawFieldCards()
    }

    private fun findAndRemoveMatchingCards(card: DrawResponse.Card) {
        val matcher = CardMatcher(
            card,
            currentCardsOnField,
            currentCardsOnFieldImgView,
        )
        matcher.match() //match the cards

        // Remove the views of the cards that have been matched to the played card
        for (v in matcher.viewsToRemove) {
            binding.fragmentGameRelativeLayout.removeView(v)
        }
        currentCardsOnField = matcher.fieldCards
        currentCardsOnFieldImgView = matcher.fieldCardsImgView

        // Update the GameState with the result of the matcher.
        updateGameState(matcher)
    }

    private fun updateGameState(matcher: CardMatcher) {
        if (matcher.matched) {
            state.nrOfMatchedCards += matcher.viewsToRemove.size + 1
            if (matcher.isBazra) {
                if (matcher.isJackBazra) {
                    //state.currentPoints += 30
                    state.jackBazraCount++
                } else {
                    //state.currentPoints += 10
                    state.bazraCount++
                }
            }

            updateDebugOutput(matcher.isBazra)
        }
    }

    private fun updateDebugOutput(isBazra: Boolean) {
        if (isBazra) {
            binding.fragmentGameIsBazraText.text = "Bazra"
        } else {
            binding.fragmentGameIsBazraText.text = "No Bazra"
        }

        binding.fragmentGamePointsText.text = state.currentPoints.toString()
    }

    private fun playCard(cardImg: ImageView) {
        when (cardImg.id) {
            currentCardsOnHandImgView[0].id -> {
                playCard(cardImg, 0, currentCardsOnHand[0], 0)
            }
            currentCardsOnHandImgView[1].id -> {
                playCard(cardImg, 1, currentCardsOnHand[1], 1)
            }
            currentCardsOnHandImgView[2].id -> {
                playCard(cardImg, 2, currentCardsOnHand[2], 2)
            }
            currentCardsOnHandImgView[3].id -> {
                playCard(cardImg, 3, currentCardsOnHand[3], 3)
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        Log.i(TAG, "GameFragment::onLongClick()")
        when (v?.id) {
            currentCardsOnHandImgView[0].id -> {
                playCard(currentCardsOnHandImgView[0], 0, currentCardsOnHand[0], 0)
            }
            currentCardsOnHandImgView[1].id -> {
                playCard(currentCardsOnHandImgView[1], 1, currentCardsOnHand[1], 1)
            }
            currentCardsOnHandImgView[2].id -> {
                playCard(currentCardsOnHandImgView[2], 2, currentCardsOnHand[2], 2)
            }
            currentCardsOnHandImgView[3].id -> {
                playCard(currentCardsOnHandImgView[3], 3, currentCardsOnHand[3], 3)
            }
        }
        return true
    }

    override fun onClick(v: View?) {
        Log.i(TAG, "GameFragment::onClick")
        //Params for focused card
        val w = kotlin.math.floor(v?.width?.times(1.5)!!).toInt()
        val h = kotlin.math.floor(v.height.times(1.5)).toInt()
        val lp = RelativeLayout.LayoutParams(w, h)
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        if (handCardFocused?.id == v.id) {
            handCardFocused = null
            playCard(v as ImageView)

        } else {
            //unfocus view/readjust to old params
            val lpUnfocus = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            lpUnfocus.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

            //find the correct currently focused hand card and set params
            if (0 < currentCardsOnHandImgView.size && handCardFocused?.id == currentCardsOnHandImgView[0].id) {
                lpUnfocus.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            } else if (1 < currentCardsOnHandImgView.size && handCardFocused?.id == currentCardsOnHandImgView[1].id) {
                lpUnfocus.addRule(RelativeLayout.RIGHT_OF, currentCardsOnHandImgView[0].id)
            } else if (2 < currentCardsOnHandImgView.size && handCardFocused?.id == currentCardsOnHandImgView[2].id) {
                lpUnfocus.addRule(RelativeLayout.RIGHT_OF, currentCardsOnHandImgView[1].id)
            } else if (3 < currentCardsOnHandImgView.size && handCardFocused?.id == currentCardsOnHandImgView[3].id) {
                lpUnfocus.addRule(RelativeLayout.RIGHT_OF, currentCardsOnHandImgView[2].id)
            }
            handCardFocused?.layoutParams = lpUnfocus
        }


        //Set the correct layoutparams for the clicked hand card
        if (0 < currentCardsOnHandImgView.size && v.id == currentCardsOnHandImgView[0].id) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            handCardFocused = currentCardsOnHandImgView[0]
        } else if (1 < currentCardsOnHandImgView.size && v.id == currentCardsOnHandImgView[1].id) {
            lp.addRule(RelativeLayout.RIGHT_OF, currentCardsOnHandImgView[0].id)
            handCardFocused = currentCardsOnHandImgView[1]
        } else if (2 < currentCardsOnHandImgView.size && v.id == currentCardsOnHandImgView[2].id) {
            lp.addRule(RelativeLayout.RIGHT_OF, currentCardsOnHandImgView[1].id)
            handCardFocused = currentCardsOnHandImgView[2]
        } else if (3 < currentCardsOnHandImgView.size && v.id == currentCardsOnHandImgView[3].id) {
            lp.addRule(RelativeLayout.RIGHT_OF, currentCardsOnHandImgView[2].id)
            handCardFocused = currentCardsOnHandImgView[3]
        }
        v.layoutParams = lp
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == R.id.nav_settings) {
            Log.i(
                TAG,
                "GameFragment::onDestinationChanged() Destination changed to settings fragment"
            )
            putGameStateInPref()
        }
    }


}