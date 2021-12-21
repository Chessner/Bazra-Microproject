package at.fh.hgb.mc.bazramicroproject.networking

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import at.fh.hgb.mc.bazramicroproject.TAG
import at.fh.hgb.mc.bazramicroproject.interfaces.VolleyCallback
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

const val SHUFFLE_CARDS: String = "https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count="
const val DRAW_CARD_S_PART1: String = "https://deckofcardsapi.com/api/deck/"
const val DRAW_CARD_S_PART2: String = "/draw/?count="

class NetworkingSingleton constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: NetworkingSingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: NetworkingSingleton(context).also {
                    INSTANCE = it
                }
            }
    }

    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    fun makeShuffleRequest(_nrOfDecks: Int, _context: Context, callback: VolleyCallback) {
        // Formulate the request and handle the response.
        val stringRequest = StringRequest(
            Request.Method.GET, SHUFFLE_CARDS + "${_nrOfDecks}",
            { response ->
                // Do something with the response
                val _response = ShuffleResponse().fromJSON(response)
                //binding.fragmentGameTextView.text = _response.toString()
                callback.onSuccess(_response)
            },
            { error ->
                // Handle error
                //binding.fragmentGameTextView.text = "ERROR: %s".format(error.toString())
                Log.e(TAG, "ERROR: %s".format(error.toString()))
            })
        getInstance(_context).addToRequestQueue(stringRequest)
    }

    fun makeReshuffleRequest(deckId: String, callback: VolleyCallback,context: Context){
        // Formulate the request and handle the response.
        val stringRequest = StringRequest(
            Request.Method.GET, DRAW_CARD_S_PART1 + deckId + "/shuffle/",
            { response ->
                // Do something with the response
                val _response = ShuffleResponse().fromJSON(response)
                //binding.fragmentGameTextView.text = _response.toString()
                callback.onSuccess(_response)
            },
            { error ->
                // Handle error
                //binding.fragmentGameTextView.text = "ERROR: %s".format(error.toString())
                Log.e(TAG, "ERROR: %s".format(error.toString()))
            })
        getInstance(context).addToRequestQueue(stringRequest)
    }

    fun makeDrawRequest(
        _deckId: String,
        _cardCount: Int,
        _context: Context,
        _callback: VolleyCallback
    ) {
        val stringRequest = StringRequest(Request.Method.GET,
            DRAW_CARD_S_PART1 + _deckId + DRAW_CARD_S_PART2 + "$_cardCount",
            { response ->
                val _response = DrawResponse().fromJSON(response)
                _callback.onSuccess(_response)
            },
            { error ->
                Log.e(TAG, "ERROR: %s".format(error.toString()))
            })
        getInstance(_context).addToRequestQueue(stringRequest)
    }

    fun getCardImage(card: DrawResponse.Card): Bitmap? {
        return try {
            val url = URL(card.image)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}
