package at.fh.hgb.mc.bazramicroproject.adapters

import android.icu.text.DateFormat.getDateTimeInstance
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import at.fh.hgb.mc.bazramicroproject.GameState
import at.fh.hgb.mc.bazramicroproject.GameType
import at.fh.hgb.mc.bazramicroproject.R
import at.fh.hgb.mc.bazramicroproject.interfaces.IonItemClick
import at.fh.hgb.mc.bazramicroproject.view_holders.MyViewHolder
import java.text.SimpleDateFormat

class MyRecyclerAdapter(private val data: MutableList<GameState>, val _onItemClickListener: IonItemClick) :
    RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.activity_saved_games_recyclerview_listitem,parent,false)
        return MyViewHolder(v, _onItemClickListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.run {
            decksTV.text = data[position].ogNrOfDecks.toString()
            pointTV.text = data[position].pointsToWin.toString()
            nameTV.text = data[position].gameName
            val formatter = getDateTimeInstance()
            createDateTV.text = formatter.format(data[position].dateOfCreation)
            //deleteDateTV.text = formatter.format(data[position].dateOfCreation)
            var img: Int? = null
            img = when(data[position].gameType){
                GameType.FRIENDS ->{
                    R.drawable.ic_baseline_people_24
                }
                GameType.SOLO -> {
                    R.drawable.ic_baseline_person_24
                }
                GameType.AI ->{
                    R.drawable.ic_baseline_computer_24
                }
            }
            gameTypeIMG.setImageResource(img)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}