package com.lendsumapp.lendsum.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.databinding.UserListItemBinding

class UserSearchListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserSearchListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false), interaction)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserSearchListViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<User>) {
        differ.submitList(list)
    }

    class UserSearchListViewHolder constructor(itemView: View, private val interaction: Interaction?)
        : RecyclerView.ViewHolder(itemView) {

        val binding = UserListItemBinding.bind(itemView)

        fun bind(item: User){
            itemView.setOnClickListener {
                interaction?.onUserItemSelected(adapterPosition, item)
            }

            Glide.with(itemView)
                .applyDefaultRequestOptions(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .circleCrop()
                )
                .load(item.profilePicUri)
                .circleCrop()
                .into(binding.userListItemProfPic)

            binding.userListItemNameTv.text = item.username

        }
    }

    interface Interaction {
        fun onUserItemSelected(position: Int, item: User)
    }

    companion object{
        //private val TAG = UserSearchListAdapter::class.simpleName
    }
}