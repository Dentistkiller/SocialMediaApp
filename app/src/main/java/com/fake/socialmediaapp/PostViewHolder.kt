package com.fake.socialmediaapp

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.BitmapFactory
import android.util.Base64
import com.fake.fakebook.Posts
import java.io.ByteArrayInputStream

class PostViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.picture)
    private val usernameText: TextView = itemView.findViewById(R.id.username)
    private val captionText: TextView = itemView.findViewById(R.id.caption)
    private val likesText: TextView = itemView.findViewById(R.id.likes)
    private val hatesText: TextView = itemView.findViewById(R.id.hates)
    private val likesButton: ImageButton = itemView.findViewById(R.id.likeButton)
    private val hatesButton: ImageButton = itemView.findViewById(R.id.hateButton)


    fun bind(post: Posts) {
        // Decode Base64 image
        try {
            val imageBytes = Base64.decode(post.imageBase64, Base64.DEFAULT)
            val imageStream = ByteArrayInputStream(imageBytes)
            val bitmap = BitmapFactory.decodeStream(imageStream)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.e("Base64Decode", "Failed to decode image", e)
        }

        usernameText.text = post.username
        captionText.text = post.caption
        likesText.text = "Likes: ${post.likes}"
        hatesText.text = "Hates: ${post.hates}"

        updateLikesDisplay(post)
        updateHatesDisplay(post)

        hatesButton.setOnClickListener {
            post.isHated = !post.isHated
            if (post.isHated) {
                post.HatesCount++
            } else {
                post.HatesCount--
            }
            updateHatesDisplay(post)
        }

        likesButton.setOnClickListener {
            post.isLiked = !post.isLiked
            if (post.isLiked) {
                post.likesCount++
            } else {
                post.likesCount--
            }
            updateLikesDisplay(post)
        }


        updateLikesDisplay(post)
        updateHatesDisplay(post)

        hatesButton.setOnClickListener {
            post.isHated = !post.isHated
            if (post.isHated) {
                post.HatesCount++ // Increment likes
                hatesButton.setImageResource(R.drawable.baseline_thumb_down_24) // this is your red heart icon
            } else {
                post.HatesCount-- // Decrement likes
                hatesButton.setImageResource(R.drawable.baseline_thumb_up_24) // Assume this is your white heart icon
            }
            updateLikesDisplay(post)
        }

        likesButton.setOnClickListener {
            post.isLiked = !post.isLiked
            if (post.isLiked) {
                post.likesCount++ // Increment likes
                likesButton.setImageResource(R.drawable.baseline_thumb_up_24) // Assume this is your red heart icon
            } else {
                post.likesCount-- // Decrement likes
                likesButton.setImageResource(R.drawable.baseline_thumb_up_24) // Assume this is your white heart icon
            }
            updateLikesDisplay(post)
        }
    }

    private fun updateLikesDisplay(item: Posts) {
        likesText.text = item.likesCount.toString()
        if (item.isLiked) {
            likesButton.setImageResource(R.drawable.baseline_thumb_up_24)
        } else {
            likesButton.setImageResource(R.drawable.baseline_thumb_up_24)
        }
    }

    private fun updateHatesDisplay(item: Posts) {
        hatesText.text = item.HatesCount.toString()
        if (item.isHated) {
            hatesButton.setImageResource(R.drawable.baseline_thumb_down_24)
        } else {
            hatesButton.setImageResource(R.drawable.baseline_thumb_up_24)
        }
    }

}