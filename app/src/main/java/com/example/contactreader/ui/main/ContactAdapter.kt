package com.example.contactreader.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactreader.R

class ContactAdapter :
    ListAdapter<ContactModel, ContactAdapter.ContactViewHolder>(ContactsDiffUtil()) {

    class ContactViewHolder private constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.text1)
        val phoneText: TextView = itemView.findViewById(R.id.text2)
        val image: AppCompatImageView = itemView.findViewById(R.id.contact_image)

        fun bind(contactModel: ContactModel) {
            with(itemView) {
                nameText.text = contactModel.name
                phoneText.text = contactModel.phone
            }
        }

        companion object {
            fun from(parent: ViewGroup): ContactViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.contact_item, parent, false)
                return ContactViewHolder(view)
            }
        }


    }

    class ContactsDiffUtil : DiffUtil.ItemCallback<ContactModel>() {
        override fun areItemsTheSame(oldItem: ContactModel, newItem: ContactModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ContactModel, newItem: ContactModel): Boolean {
            return oldItem.name == newItem.name && oldItem.phone == newItem.phone
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

data class ContactModel(
    val id: Long,
    val name: String? = "John Doe",
    val phone: String? = "0000000000",
    val image: String? = null
)