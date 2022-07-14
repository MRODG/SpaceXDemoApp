package com.mariosodigie.apps.spacexdemoapp.launches.ui

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariosodigie.apps.spacexdemoapp.R
import com.mariosodigie.apps.spacexdemoapp.databinding.LaunchDetailItemBinding
import com.mariosodigie.apps.spacexdemoapp.launches.domain.model.LaunchDetails

class LaunchListAdapter(private val itemClickHandler: (LaunchDetails) -> Unit ) :
    ListAdapter<LaunchDetails, LaunchListAdapter.ViewHolder>(LaunchListDiffCallback) {

    class ViewHolder(private val binding: LaunchDetailItemBinding, itemClickHandler: (LaunchDetails) -> Unit): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LaunchDetails) = with(binding){
            itemName.text = item.rocketName
            itemDate.text = root.context.getString(R.string.launch_date_prefix, item.date)
            itemSuccess.apply {
                gravity = Gravity.CENTER_VERTICAL
                compoundDrawablePadding = context.resources.getDimensionPixelOffset(R.dimen.margin_s)
                if(item.success) setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_done_24, 0)
                else setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_clear_24, 0)
            }
            Glide.with(root.context).load(item.imageUrl).into(itemBadge);
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LaunchDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding, itemClickHandler)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val launchDetail = getItem(position)
        holder.bind(launchDetail)
    }
}

object LaunchListDiffCallback : DiffUtil.ItemCallback<LaunchDetails>() {
    override fun areItemsTheSame(oldItem: LaunchDetails, newItem: LaunchDetails): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: LaunchDetails, newItem: LaunchDetails): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}
