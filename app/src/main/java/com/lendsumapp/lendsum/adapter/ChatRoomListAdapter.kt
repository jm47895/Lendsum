package com.lendsumapp.lendsum.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.databinding.ChatRoomListItemBinding
import com.lendsumapp.lendsum.util.AndroidUtils

class ChatRoomListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChatRoom>() {

        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem.chatRoomId == newItem.chatRoomId
        }

        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ChatRoomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_room_list_item, parent, false), interaction)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatRoomViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<ChatRoom>) {
        differ.submitList(list)
    }

    class ChatRoomViewHolder constructor(itemView: View, private val interaction: Interaction?) : RecyclerView.ViewHolder(itemView) {

        private val firebaseUser = FirebaseAuth.getInstance().currentUser
        val binding = ChatRoomListItemBinding.bind(itemView)

        fun bind(item: ChatRoom){
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            lateinit var guestUser: User

            val participants = item.participants
            participants?.let {
                for (user in participants){
                    guestUser = user
                }
            }

            Glide.with(itemView)
                .applyDefaultRequestOptions(
                    RequestOptions()
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .circleCrop()
                )
                .load(guestUser.profilePicUri)
                .circleCrop()
                .into(binding.chatRoomListItemProfPic)

            binding.chatRoomListItemNameTv.text = guestUser.name
            binding.chatRoomListItemDateTv.text = AndroidUtils.convertTimestampToShortDate(item.lastTimestamp)
            binding.chatRoomListItemLastMsgTv.text = item.lastMessage
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: ChatRoom)
    }
}