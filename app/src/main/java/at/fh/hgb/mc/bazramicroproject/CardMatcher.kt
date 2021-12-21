package at.fh.hgb.mc.bazramicroproject

import android.widget.ImageView
import at.fh.hgb.mc.bazramicroproject.networking.DrawResponse


class CardMatcher(
    private val playedCard: DrawResponse.Card,
    var fieldCards: MutableList<DrawResponse.Card>,
    var fieldCardsImgView: MutableList<ImageView>,
) {
    var viewsToRemove = mutableListOf<ImageView>()
    var isBazra = false
    var isJackBazra = false
    private var matchingCards: MutableList<DrawResponse.Card>? = null
    var matched: Boolean = false

    private var aceAce = mutableListOf<String>()

    private var twoTwo = mutableListOf<String>()
    private var twoAces = mutableListOf<String>()

    private var threeThree = mutableListOf<String>()
    private var threeAces = mutableListOf<String>()
    private var threeAceTwo = mutableListOf<String>()

    private var fourFour = mutableListOf<String>()
    private var fourAces = mutableListOf<String>()
    private var fourAceThree = mutableListOf<String>()
    private var fourTwoTwo = mutableListOf<String>()

    private var fiveFive = mutableListOf<String>()
    private var fiveFourAce = mutableListOf<String>()
    private var fiveTwoTwoOne = mutableListOf<String>()
    private var fiveThreeTwo = mutableListOf<String>()

    private var sixSix = mutableListOf<String>()
    private var sixThreeThree = mutableListOf<String>()
    private var sixTwoTwoTwo = mutableListOf<String>()
    private var sixFourTwo = mutableListOf<String>()
    private var sixFiveAce = mutableListOf<String>()

    private var sevenSeven = mutableListOf<String>()
    private var sevenSixAce = mutableListOf<String>()
    private var sevenFiveTwo = mutableListOf<String>()
    private var sevenFourThree = mutableListOf<String>()
    private var sevenThreeThreeAce = mutableListOf<String>()
    private var sevenTwoTwoTwoAce = mutableListOf<String>()

    private var eightEight = mutableListOf<String>()
    private var eightSevenAce = mutableListOf<String>()
    private var eightSixTwo = mutableListOf<String>()
    private var eightFiveThree = mutableListOf<String>()
    private var eightFourFour = mutableListOf<String>()
    private var eightThreeThreeTwo = mutableListOf<String>()
    private var eightTwoTwoTwoTwo = mutableListOf<String>()

    private var nineNine = mutableListOf<String>()
    private var nineFiveFour = mutableListOf<String>()
    private var nineEightAce = mutableListOf<String>()
    private var nineSevenTwo = mutableListOf<String>()
    private var nineSixThree = mutableListOf<String>()
    private var nineFourFourAce = mutableListOf<String>()
    private var nineFourThreeTwo = mutableListOf<String>()
    private var nineThreeThreeThree = mutableListOf<String>()

    private var tenTen = mutableListOf<String>()
    private var tenNineAce = mutableListOf<String>()
    private var tenEightTwo = mutableListOf<String>()
    private var tenSevenThree = mutableListOf<String>()
    private var tenSixFour = mutableListOf<String>()
    private var tenFiveFive = mutableListOf<String>()
    private var tenThreeThreeThreeAce = mutableListOf<String>()
    private var tenFourThreeThree = mutableListOf<String>()
    private var tenFourFourTwo = mutableListOf<String>()
    private var tenFiveFourAce = mutableListOf<String>()

    private var jackJack = mutableListOf<String>()
    private var queenQueen = mutableListOf<String>()
    private var kingKing = mutableListOf<String>()

    fun match() {
        matchAdders()

        if (!matched) {
            fieldCards.add(playedCard)
        }
    }


    private fun matchAdders() {
        when (playedCard.value) {
            "ACE" -> {
                matchACE()
                checkBazra()
            }
            "2" -> {
                match2()
                checkBazra()
            }
            "3" -> {
                match3()
                checkBazra()
            }
            "4" -> {
                match4()
                checkBazra()
            }
            "5" -> {
                match5()
                checkBazra()
            }
            "6" -> {
                match6()
                checkBazra()
            }
            "7" -> {
                if(playedCard.suit != "DIAMONDS"){
                    //the 7 is not a diamonds 7 and henceforth a normal card
                    match7()
                    checkBazra()
                } else {
                    //the card is a diamond 7, meaning a special card. It can either be used for a bazra or as a jack.

                    //check for bazras
                    val pseudoCards = mutableListOf<DrawResponse.Card>()
                    pseudoCards.apply {
                        add(DrawResponse.Card("","ACE","SPADES",""))
                        add(DrawResponse.Card("","2","SPADES",""))
                        add(DrawResponse.Card("","3","SPADES",""))
                        add(DrawResponse.Card("","4","SPADES",""))
                        add(DrawResponse.Card("","5","SPADES",""))
                        add(DrawResponse.Card("","6","SPADES",""))
                        add(DrawResponse.Card("","7","SPADES",""))
                        add(DrawResponse.Card("","8","SPADES",""))
                        add(DrawResponse.Card("","9","SPADES",""))
                        add(DrawResponse.Card("","10","SPADES",""))
                        add(DrawResponse.Card("","QUEEN","SPADES",""))
                        add(DrawResponse.Card("","KING","SPADES",""))
                    }
                    var run = true
                    var i = 0
                    var matcher: CardMatcher? = null
                    while(run && i < pseudoCards.size){
                        matcher = CardMatcher(pseudoCards[i],fieldCards,fieldCardsImgView)
                        run = !matcher.isBazra
                        i++
                    }
                    if(!run){
                        //a bazra has been found
                        isBazra = matcher!!.isBazra
                        fieldCards = matcher.fieldCards
                        fieldCardsImgView = matcher.fieldCardsImgView
                        viewsToRemove = matcher.viewsToRemove
                    } else {
                        //no possible bazra found -> use it as a jack
                        matchJACK()
                    }

                }
            }
            "8" -> {
                match8()
                checkBazra()
            }
            "9" -> {
                match9()
                checkBazra()
            }
            "10" -> {
                match10()
                checkBazra()
            }
            "JACK" -> {
                //Checks if there is only jacks on the field. -> Bazra
                var onlyJack = true
                for(card in fieldCards){
                    if(card.value != "JACK") onlyJack = false
                }
                isBazra = onlyJack
                matchJACK()
            }
            "QUEEN" -> {
                matchQueen()
                checkBazra()
            }
            "KING" -> {
                matchKING()
                checkBazra()
            }
        }
    }

    private fun matchKING() {
        kingKing.add("KING")
        val list = mutableListOf<MutableList<String>>()
        list.add(kingKing)
        matchList(list)
    }

    private fun matchQueen() {
        queenQueen.add("QUEEN")
        val list = mutableListOf<MutableList<String>>()
        list.add(queenQueen)
        matchList(list)
    }

    private fun matchJACK() {
        jackJack.add("JACK")
        val list = mutableListOf<MutableList<String>>()
        kingKing.add("KING")
        queenQueen.add("QUEEN")
        tenTen.add("10")
        nineNine.add("9")
        eightEight.add("8")
        sevenSeven.add("7")
        sixSix.add("6")
        fiveFive.add("5")
        fourFour.add("4")
        threeThree.add("3")
        twoTwo.add("2")
        aceAce.add("ACE")

        list.add(kingKing)
        list.add(queenQueen)
        list.add(jackJack)
        list.add(tenTen)
        list.add(nineNine)
        list.add(eightEight)
        list.add(sevenSeven)
        list.add(sixSix)
        list.add(fiveFive)
        list.add(fourFour)
        list.add(threeThree)
        list.add(twoTwo)
        list.add(aceAce)
        matchList(list)
    }

    private fun match10() {
        tenTen.add("10")
        tenNineAce.add("9")
        tenNineAce.add("ACE")
        tenEightTwo.add("8")
        tenEightTwo.add("2")
        tenSevenThree.add("7")
        tenSevenThree.add("3")
        tenSixFour.add("6")
        tenSixFour.add("4")
        tenFiveFive.add("5")
        tenFiveFive.add("5")
        tenFiveFourAce.add("5")
        tenFiveFourAce.add("4")
        tenFiveFourAce.add("ACE")
        tenFourFourTwo.add("4")
        tenFourFourTwo.add("4")
        tenFourFourTwo.add("2")
        tenFourThreeThree.add("4")
        tenFourThreeThree.add("3")
        tenFourThreeThree.add("3")
        tenThreeThreeThreeAce.add("3")
        tenThreeThreeThreeAce.add("3")
        tenThreeThreeThreeAce.add("3")
        tenThreeThreeThreeAce.add("ACE")

        val list = mutableListOf<MutableList<String>>()
        list.add(tenTen)
        list.add(tenNineAce)
        list.add(tenEightTwo)
        list.add(tenSevenThree)
        list.add(tenSixFour)
        list.add(tenFiveFive)
        list.add(tenFiveFourAce)
        list.add(tenFourFourTwo)
        list.add(tenFourThreeThree)
        list.add(tenThreeThreeThreeAce)
        matchList(list)
    }

    private fun match9() {
        nineNine.add("9")
        nineEightAce.add("8")
        nineEightAce.add("ACE")
        nineSevenTwo.add("7")
        nineSevenTwo.add("2")
        nineSixThree.add("6")
        nineSixThree.add("3")
        nineFiveFour.add("5")
        nineFiveFour.add("4")
        nineFourFourAce.add("4")
        nineFourFourAce.add("4")
        nineFourFourAce.add("ACE")
        nineFourThreeTwo.add("4")
        nineFourThreeTwo.add("3")
        nineFourThreeTwo.add("2")
        nineThreeThreeThree.add("3")
        nineThreeThreeThree.add("3")
        nineThreeThreeThree.add("3")

        val list = mutableListOf<MutableList<String>>()
        list.add(nineNine)
        list.add(nineEightAce)
        list.add(nineSevenTwo)
        list.add(nineSixThree)
        list.add(nineFiveFour)
        list.add(nineFourFourAce)
        list.add(nineFourThreeTwo)
        list.add(nineThreeThreeThree)
        matchList(list)
    }

    private fun match8() {
        eightEight.add("8")
        eightSevenAce.add("7")
        eightSevenAce.add("ACE")
        eightSixTwo.add("6")
        eightSixTwo.add("2")
        eightFiveThree.add("5")
        eightFiveThree.add("3")
        eightFourFour.add("4")
        eightFourFour.add("4")
        eightThreeThreeTwo.add("3")
        eightThreeThreeTwo.add("3")
        eightThreeThreeTwo.add("2")
        eightTwoTwoTwoTwo.add("2")
        eightTwoTwoTwoTwo.add("2")
        eightTwoTwoTwoTwo.add("2")
        eightTwoTwoTwoTwo.add("2")

        val list = mutableListOf<MutableList<String>>()
        list.add(eightEight)
        list.add(eightSevenAce)
        list.add(eightSixTwo)
        list.add(eightFiveThree)
        list.add(eightFourFour)
        list.add(eightThreeThreeTwo)
        list.add(eightTwoTwoTwoTwo)
        matchList(list)
    }

    private fun match7() {
        sevenSeven.add("7")
        sevenSixAce.add("6")
        sevenSixAce.add("ACE")
        sevenFiveTwo.add("5")
        sevenFiveTwo.add("2")
        sevenFourThree.add("4")
        sevenFourThree.add("3")
        sevenTwoTwoTwoAce.add("2")
        sevenTwoTwoTwoAce.add("2")
        sevenTwoTwoTwoAce.add("2")
        sevenTwoTwoTwoAce.add("ACE")
        sevenThreeThreeAce.add("3")
        sevenThreeThreeAce.add("3")
        sevenThreeThreeAce.add("ACE")

        val list = mutableListOf<MutableList<String>>()
        list.add(sevenSeven)
        list.add(sevenSixAce)
        list.add(sevenFiveTwo)
        list.add(sevenFourThree)
        list.add(sevenTwoTwoTwoAce)
        list.add(sevenThreeThreeAce)
        matchList(list)
    }

    private fun match6() {
        sixSix.add("6")
        sixFiveAce.add("5")
        sixFiveAce.add("ACE")
        sixFourTwo.add("4")
        sixFourTwo.add("2")
        sixThreeThree.add("3")
        sixThreeThree.add("3")
        sixTwoTwoTwo.add("2")
        sixTwoTwoTwo.add("2")
        sixTwoTwoTwo.add("2")
        val list = mutableListOf<MutableList<String>>()
        list.add(sixSix)
        list.add(sixFiveAce)
        list.add(sixTwoTwoTwo)
        list.add(sixThreeThree)
        list.add(sixFourTwo)
        matchList(list)
    }

    private fun match5() {
        fiveFive.add("5")
        fiveFourAce.add("4")
        fiveFourAce.add("ACE")
        fiveThreeTwo.add("3")
        fiveThreeTwo.add("2")
        fiveTwoTwoOne.add("2")
        fiveTwoTwoOne.add("2")
        fiveTwoTwoOne.add("ACE")

        val list = mutableListOf<MutableList<String>>()
        list.add(fiveFive)
        list.add(fiveFourAce)
        list.add(fiveThreeTwo)
        list.add(fiveTwoTwoOne)
        matchList(list)
    }

    private fun match4() {
        fourFour.add("4")
        fourAces.apply {
            add("4")
            add("4")
            add("4")
            add("4")
        }
        fourAceThree.add("3")
        fourAceThree.add("ACE")
        fourTwoTwo.add("2")
        fourTwoTwo.add("2")

        val list = mutableListOf<MutableList<String>>()
        list.add(fourFour)
        list.add(fourAces)
        list.add(fourTwoTwo)
        list.add(fourAceThree)
        matchList(list)
    }

    private fun match3() {
        threeThree.add("3")

        threeAces.apply {
            add("ACE")
            add("ACE")
            add("ACE")
        }

        threeAceTwo.apply {
            add("ACE")
            add("2")
        }

        val list = mutableListOf<MutableList<String>>()
        list.add(threeThree)
        list.add(threeAces)
        list.add(threeAceTwo)
        matchList(list)
    }

    private fun match2() {
        twoTwo.add("2")

        twoAces.apply {
            add("ACE")
            add("ACE")
        }

        val list = mutableListOf<MutableList<String>>()
        list.add(twoTwo)
        list.add(twoAces)
        matchList(list)
    }

    private fun matchACE() {
        aceAce.add("ACE")
        val list = mutableListOf<MutableList<String>>()
        list.add(aceAce)
        matchList(list)
    }

    private fun checkBazra(): Boolean{
        isBazra = fieldCards.size == 0 && matched
        if(isBazra && playedCard.value == "JACK") isJackBazra = true

        return isBazra
    }

    private fun matchList(listOfValues: MutableList<MutableList<String>>) {
        for (list in listOfValues) {

            var run = true

            while (run) {
                val res = fieldCardsContains(list)
                if (res.boolean) {
                    matched = true
                    //delete found cards
                    deleteAtIndexes(res.indexes)
                }
                run = res.boolean
            }
        }
    }

    private fun deleteAtIndexes(indexes: MutableList<Int>) {

        for (j in 0 until indexes.size) {
            var max = 0
            var maxI = 0
            var i = 0
            while (i < indexes.size) {
                if (indexes[i] > max) {
                    max = indexes[i]
                    maxI = i
                }
                i++
            }
            indexes[maxI] = -1
            viewsToRemove.add(fieldCardsImgView[max])
            fieldCards.removeAt(max)
            fieldCardsImgView.removeAt(max)
        }
    }

    private fun fieldCardsContains(
        value: String,
        list: MutableList<DrawResponse.Card>
    ): BooleanInt {
        for (i in 0 until list.size) {
            if (value == list[i].value) return BooleanInt(true, i)
        }
        return BooleanInt(false, -1)
    }

    private class BooleanInt(val boolean: Boolean, val int: Int)
    private class BooleanIndexes(val boolean: Boolean, val indexes: MutableList<Int>)

    private fun fieldCardsContains(values: MutableList<String>): BooleanIndexes {
        matchingCards = mutableListOf()
        for (card in fieldCards) {
            matchingCards!!.add(card)
        }

        val toDelIndexes = mutableListOf<Int>()

        var i = 0

        var run = true
        while (i < values.size && run) {
            val b = fieldCardsContains(values[i], matchingCards!!)
            if (b.boolean) {
                toDelIndexes.add(b.int)

                matchingCards!![b.int] = DrawResponse.Card("", "", "", "")

            }
            run = b.boolean

            i++
        }

        //Check if all given values have been found
        if (toDelIndexes.size == values.size) {
            return BooleanIndexes(true, toDelIndexes)
        }
        return BooleanIndexes(false, toDelIndexes)
    }
}

