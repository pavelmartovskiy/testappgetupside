package com.mp.testapp.getupside.presentation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mp.testapp.getupside.R
import kotlinx.android.synthetic.main.i_food_point.view.*

class FoodPointListAdapter : ListAdapter<FoodPointItem, ListHolder>(AsyncDifferConfig.Builder(ItemCallback()).build()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ListHolder(parent)

    override fun onBindViewHolder(holder: ListHolder, position: Int) = holder.bind(currentList[position])

    override fun getItemCount() = currentList.size

}

class ItemCallback : DiffUtil.ItemCallback<FoodPointItem>() {
    override fun areItemsTheSame(oldItem: FoodPointItem, newItem: FoodPointItem) = oldItem.name == newItem.name
    override fun areContentsTheSame(oldItem: FoodPointItem, newItem: FoodPointItem) = oldItem == newItem
}


class ListHolder(
        container: ViewGroup
) : RecyclerView.ViewHolder(LayoutInflater.from(container.context).inflate(R.layout.i_food_point, container, false)) {
    fun bind(item: FoodPointItem) {
        itemView.apply {
            tvName.text = item.name
            tvAddress.text = item.address
            tvPhone.text = item.phone
            tvLocation.text = item.position
            tvDistance.text = item.distance
        }
    }
}