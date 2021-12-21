package at.fh.hgb.mc.bazramicroproject.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.fh.hgb.mc.bazramicroproject.interfaces.IonItemClick
import at.fh.hgb.mc.bazramicroproject.R

//ViewHolder for recyclerview items in SavedGamesActivity
class MyViewHolder(val root: View, val onItemClickListener: IonItemClick): RecyclerView.ViewHolder(root), View.OnClickListener {
    val decksTV: TextView
    val pointTV: TextView
    val nameTV: TextView
    val createDateTV: TextView
   // val deleteDateTV: TextView
    val gameTypeIMG: ImageView


    init {
        decksTV = root.findViewById(R.id.activity_saved_games_recyclerview_listitem_nrOfDecksOutputLabel)
        pointTV = root.findViewById(R.id.activity_saved_games_recyclerview_listitem_pointsToBeReachedOutputLabel)
        nameTV = root.findViewById(R.id.activity_saved_games_recyclerview_listitem_gameNameOutputLabel)
        createDateTV = root.findViewById(R.id.activity_saved_games_recyclerview_listitem_createdDateLabel)
        //deleteDateTV = root.findViewById(R.id.activity_saved_games_recyclerview_listitem_deleteDateLabel)
        gameTypeIMG = root.findViewById(R.id.activity_saved_games_recyclerview_listitem_gameTypeImageView)
        root.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        onItemClickListener.onItemClick(adapterPosition)
    }

}