package at.fh.hgb.mc.bazramicroproject

import at.fh.hgb.mc.bazramicroproject.GameType
import at.fh.hgb.mc.bazramicroproject.networking.DrawResponse
import com.google.gson.internal.LinkedTreeMap

class DataStructureTranslator {
    //Converts ArrayList given by Gson to MutableList
    fun arrayListLinkedTreeMapToMutableListForGameState(array: ArrayList<LinkedTreeMap<String, Any>>): MutableList<GameState> {
        val list = mutableListOf<GameState>()
        for (arr in array) {
            val g: GameType = when (arr["gameType"] as String) {
                "FRIENDS" -> {
                    GameType.FRIENDS
                }
                "AI" -> {
                    GameType.AI
                }
                else -> {
                    GameType.SOLO
                }
            }
            list.add(
                GameState(
                    arr["gameName"] as String,
                    (arr["ogNrOfDecks"] as Double).toInt(),
                    (arr["pointsToWin"] as Double).toInt(),
                    g,
                    (arr["currentPoints"] as Double).toInt(),
                    (arr["bazraCount"] as Double).toInt(),
                    (arr["jackBazraCount"] as Double).toInt(),
                    (arr["currentNrOfCards"] as Double).toInt(),
                    (arr["nrOfMatchedCards"] as Double).toInt(),
                    (arr["dateOfCreation"] as Double).toLong(),
                    arr["deckId"] as String
                )
            )
        }
        return list
    }

    fun arrayListLinkedTreeMapToMutableListOfCards(array: ArrayList<LinkedTreeMap<String,String>>): MutableList<DrawResponse.Card>{
        val list = mutableListOf<DrawResponse.Card>()

        for(arr in array){
            list.add(
                DrawResponse.Card(
                    arr["image"] as String,
                    arr["value"] as String,
                    arr["suit"] as String,
                    arr["code"] as String
                )
            )
        }
        return list;
    }

    fun arrayListOfArrayListOfLinkedTreeMapToMutableListOfListsForCards(array: ArrayList<ArrayList<LinkedTreeMap<String, String>>>): MutableList<MutableList<DrawResponse.Card>> {
        val list = mutableListOf<MutableList<DrawResponse.Card>>()

        /*for (arr in array) {
            list.add(
                DrawResponse.Card(
                    arr["image"] as String,
                    arr["value"] as String,
                    arr["suit"] as String,
                    arr["code"] as String
                )
            )
        }*/

        for (arr in array) {
            val bufferList = mutableListOf<DrawResponse.Card>()
            for (a in arr) {
                bufferList.add(
                    DrawResponse.Card(
                        a["image"] as String,
                        a["value"] as String,
                        a["suit"] as String,
                        a["code"] as String
                    )
                )
            }
            list.add(bufferList)
        }
        return list
    }
}