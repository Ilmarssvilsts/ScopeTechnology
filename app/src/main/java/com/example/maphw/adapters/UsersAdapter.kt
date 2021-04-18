package com.example.maphw.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maphw.R
import com.example.maphw.api.models.User
import com.example.maphw.data.Owner
import com.squareup.picasso.Picasso

class UsersAdapter(
        private var usersList: MutableList<Owner>,
        private val rowLayout: Int = 0,
        private var context: Context?,
        private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    lateinit var recyclerView: RecyclerView

    fun updateUsers(
        usersList: MutableList<Owner>
    ) {
        this.usersList = usersList
        notifyDataSetChanged()
    }

    class UsersViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var userName: TextView
        internal var userSurname: TextView
        internal var userImg: ImageView

        init {
            userName = v.findViewById(R.id.userName)
            userSurname = v.findViewById(R.id.userSurname)
            userImg = v.findViewById(R.id.userImg)
        }

        fun bind(position: Int, clickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                clickListener.onItemClicked(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return UsersViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(position, itemClickListener)
        holder.userName.text = usersList[position].name
        holder.userSurname.text = usersList[position].surname
        //Picasso sometimes does not like http urls
        Picasso.get().load(usersList[position].photo?.replace("http:", "https:"))
            .into(holder.userImg)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }
}
