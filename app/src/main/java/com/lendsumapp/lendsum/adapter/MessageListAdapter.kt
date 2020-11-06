package com.lendsumapp.lendsum.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.Message
import com.lendsumapp.lendsum.databinding.MessageListItemBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import java.util.*


class MessageListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Message>() {

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.message == newItem.message
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_list_item, parent, false), interaction)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var lastItemPosition = position
        if(position > 0){
            lastItemPosition = position - 1
        }

        when (holder) {
            is MessageViewHolder -> {
                holder.bind(differ.currentList[position], differ.currentList[lastItemPosition], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Message>): Int {
        differ.submitList(list)
        return list.size
    }

    class MessageViewHolder constructor(itemView: View, private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        private val firebaseUser = FirebaseAuth.getInstance().currentUser
        private val binding = MessageListItemBinding.bind(itemView)

        fun bind(item: Message, previousItem: Message, position: Int){

            val previousTimestamp = previousItem.messageTimestamp
            val currentTimestamp = item.messageTimestamp

            val oldDate = AndroidUtils.convertTimestampToFullDate(previousTimestamp)
            val date = AndroidUtils.convertTimestampToFullDate(currentTimestamp)

            if (oldDate == date && position != 0){
                binding.messageTimestampTv.visibility = View.GONE
            }else{
                binding.messageTimestampTv.visibility = View.VISIBLE
            }

            binding.messageTimestampTv.text = date

            if(item.messageSender == firebaseUser?.uid){
                binding.messageHostTv.text = item.message
                binding.messageHostTv.visibility = View.VISIBLE
            }else{
                Glide.with(itemView.context)
                    .applyDefaultRequestOptions(
                        RequestOptions()
                            .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                            .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                            .circleCrop()
                    )
                    .load(item.guestPic)
                    .circleCrop()
                    .into(binding.messageGuestPic)

                binding.messageGuestTv.text = item.message
                binding.messageGuestTv.visibility = View.VISIBLE
            }

        }


    }

    interface Interaction {
        fun onMessageItemSelected(position: Int, item: Message)
    }

    companion object{
        private val TAG = MessageListAdapter::class.java.simpleName
    }
}