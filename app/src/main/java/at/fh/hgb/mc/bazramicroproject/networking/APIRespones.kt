package at.fh.hgb.mc.bazramicroproject.networking
import android.os.Parcel
import android.os.Parcelable
import at.fh.hgb.mc.bazramicroproject.interfaces.IAPIResponse
import com.google.gson.Gson

data class ShuffleResponse(override val success: Boolean, override val deck_id: String, val shuffled: Boolean, val remaining: Int): IAPIResponse{
    constructor() : this(false,"",false,0)


    override fun fromJSON(s: String): ShuffleResponse {
        return Gson().fromJson(s,ShuffleResponse::class.java)
    }

}

data class DrawResponse(override val success: Boolean, val cards: MutableList<Card>, override val deck_id: String, val remaining: Int): IAPIResponse{
    constructor(): this(false, mutableListOf(),"",0)



    class Card(val image: String, val value: String, val suit: String, val code: String): Parcelable{
        constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString()
        ) {
        }

        fun fromJSON(s: String): Card {
            return Gson().fromJson(s,Card::class.java)
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(image)
            parcel.writeString(value)
            parcel.writeString(suit)
            parcel.writeString(code)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Card> {
            override fun createFromParcel(parcel: Parcel): Card {
                return Card(parcel)
            }

            override fun newArray(size: Int): Array<Card?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun fromJSON(s: String): DrawResponse{
        return Gson().fromJson(s,DrawResponse::class.java)
    }
}
