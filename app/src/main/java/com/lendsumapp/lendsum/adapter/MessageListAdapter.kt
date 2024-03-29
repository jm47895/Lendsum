package com.lendsumapp.lendsum.adapter

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.Message
import com.lendsumapp.lendsum.databinding.MessageListItemBinding
import com.lendsumapp.lendsum.util.AndroidUtils


class MessageListAdapter(private val interaction: Interaction? = null, profPicUri: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val uri = profPicUri
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
        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_list_item, parent, false), interaction, uri)
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

    class MessageViewHolder constructor(itemView: View, private val interaction: Interaction?, profPicUri: String) : RecyclerView.ViewHolder(itemView) {

        val uri = profPicUri
        private val firebaseUser = FirebaseAuth.getInstance().currentUser
        private val binding = MessageListItemBinding.bind(itemView)

        fun bind(item: Message, previousItem: Message, position: Int){

            val previousTimestamp = previousItem.messageTimestamp
            val currentTimestamp = item.messageTimestamp
            var isPhotoNeeded = true

            val oldDate = AndroidUtils.convertTimestampToFullDate(previousTimestamp)
            val date = AndroidUtils.convertTimestampToFullDate(currentTimestamp)

            if (oldDate == date && position != 0){
                AndroidUtils.goneView(binding.messageTimestampTv)
                AndroidUtils.goneView(binding.messageGuestPic)
                isPhotoNeeded = false
            }else{
                AndroidUtils.showView(binding.messageTimestampTv)
                AndroidUtils.showView(binding.messageTimestampTv)
            }

            binding.messageTimestampTv.text = date

            if(item.messageSender == firebaseUser?.uid){
                binding.messageHostTv.text = item.message
                AndroidUtils.showView(binding.messageHostTv)
                AndroidUtils.hideView(binding.messageGuestTv)
            }else{

                binding.messageGuestTv.text = item.message
                binding.messageGuestTv.setTextColor(Color.parseColor("#FFFFFF"))
                AndroidUtils.showView(binding.messageGuestTv)
                AndroidUtils.hideView(binding.messageHostTv)

                
                if(isPhotoNeeded) {
                    Glide.with(itemView)
                        .applyDefaultRequestOptions(
                            RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                //.placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                                //.error(R.drawable.com_facebook_profile_picture_blank_portrait)
                                .circleCrop()
                        )
                        .load(uri)
                        .circleCrop()
                        .into(binding.messageGuestPic)
                }
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