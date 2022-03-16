package com.betulsofuoglu.sharenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betulsofuoglu.sharenote.R
import com.betulsofuoglu.sharenote.model.Post
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_row.view.*

class PostAdapter(val postList: ArrayList<Post>) : RecyclerView.Adapter<PostAdapter.PostHolder>() {

    class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row, parent, false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.itemView.recycler_row_username.text = postList[position].username
        holder.itemView.recycler_row_post.text=postList[position].sharedComment

        if(postList[position].imageUrl!=null){
            holder.itemView.recycler_row_imageview.visibility=View.VISIBLE
            Picasso.get().load(postList[position].imageUrl).into(holder.itemView.recycler_row_imageview)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}