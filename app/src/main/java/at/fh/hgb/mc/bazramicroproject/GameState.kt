package at.fh.hgb.mc.bazramicroproject

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import at.fh.hgb.mc.bazramicroproject.interfaces.IAPIResponse

const val DECK_ID: String = "current deck id"
const val CURRENT_POINTS: String = "current points are"
const val BAZRA_COUNT: String = "current bazra count is"
const val CURRENT_NR_OF_CARDS: String = "current nr of cards are"
const val DATE_OF_CREATION: String = "date of creation is"
const val GAME_NAME: String = "game name is"
const val OG_NR_OF_DECKS: String = "ogNrOfDecks"
const val POINTS_TO_WIN: String = "pointsToWin"
const val GAME_TYPE: String = "gameType"
const val NR_OF_MATCHED_CARDS: String = "nrOfMatchedCards"
const val JACK_BAZRA_COUNT: String = "jackBazraCount"

data class GameState(
    var gameName: String?,
    val ogNrOfDecks: Int,
    val pointsToWin: Int,
    val gameType: GameType,
    var currentPoints: Int = 0,
    var bazraCount: Int = 0,
    var jackBazraCount: Int = 0,
    var currentNrOfCards: Int = ogNrOfDecks * 52,
    var nrOfMatchedCards: Int = 0,
    var dateOfCreation: Long = System.currentTimeMillis(),
    var deckId: String? = ""
) :Parcelable{


    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() as GameType,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString()
    ) {
    }

    fun readGameType(bundle: Bundle): GameType {
        val string = bundle.getString(GAME_TYPE)
        if (string.equals("SOLO")) {
            return GameType.SOLO
        } else if (string.equals("AI")) {
            return GameType.AI
        } else {
            return GameType.FRIENDS
        }
    }

    fun getExtra(i: Intent?): GameState {
        val bundle = i?.extras!!
        return GameState(
            bundle.getString(GAME_NAME),
            bundle.getInt(OG_NR_OF_DECKS),
            bundle.getInt(
                POINTS_TO_WIN
            ),
            readGameType(bundle),
            bundle.getInt(CURRENT_POINTS),
            bundle.getInt(BAZRA_COUNT),
            bundle.getInt(JACK_BAZRA_COUNT),
            bundle.getInt(CURRENT_NR_OF_CARDS),
            bundle.getInt(NR_OF_MATCHED_CARDS),
            bundle.getLong(DATE_OF_CREATION),
            bundle.getString(
                DECK_ID
            ),
        )
    }

    fun putExtra(i: Intent, result: IAPIResponse?) {
        i.putExtra(GAME_NAME, gameName)
        i.putExtra(OG_NR_OF_DECKS, ogNrOfDecks)
        i.putExtra(POINTS_TO_WIN, pointsToWin)
        i.putExtra(GAME_TYPE, gameType.toString())
        i.putExtra(CURRENT_POINTS, currentPoints)
        i.putExtra(BAZRA_COUNT, bazraCount)
        i.putExtra(JACK_BAZRA_COUNT,jackBazraCount)
        i.putExtra(CURRENT_NR_OF_CARDS, currentNrOfCards)
        i.putExtra(NR_OF_MATCHED_CARDS,nrOfMatchedCards)
        i.putExtra(DATE_OF_CREATION, dateOfCreation)
        if (result == null) {
            i.putExtra(DECK_ID, deckId)
        } else {
            i.putExtra(DECK_ID, result.deck_id)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(gameName)
        parcel.writeInt(ogNrOfDecks)
        parcel.writeInt(pointsToWin)
        parcel.writeInt(currentPoints)
        parcel.writeInt(bazraCount)
        parcel.writeInt(jackBazraCount)
        parcel.writeInt(currentNrOfCards)
        parcel.writeInt(nrOfMatchedCards)
        parcel.writeLong(dateOfCreation)
        parcel.writeString(deckId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GameState> {
        override fun createFromParcel(parcel: Parcel): GameState {
            return GameState(parcel)
        }

        override fun newArray(size: Int): Array<GameState?> {
            return arrayOfNulls(size)
        }
    }


}

enum class GameType {
    SOLO, FRIENDS, AI

}