package com.donsdirectory.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.donsdirectory.mobile.R
import com.donsdirectory.mobile.model.Contact
import kotlinx.android.synthetic.main.item_kontact.view.*

/**
 * Created by Deepak Kumar on 25/05/2019
 */

class KontactsAdapter(
    private var contactsList: ArrayList<Contact>?
) : RecyclerView.Adapter<KontactsAdapter.KontactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KontactViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kontact, parent, false)
        return KontactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: KontactViewHolder, position: Int) {
        holder.bind(holder.adapterPosition)
    }

    override fun getItemCount() = contactsList?.size ?: 0

    fun updateList(list: ArrayList<Contact>) {
        this.contactsList = list
        notifyDataSetChanged()
    }

    inner class KontactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val contactNameView: TextView = view.findViewById(R.id.contact_name)
        private val contactMobileView: TextView = view.findViewById(R.id.contact_mobile)
        private val contactCompanyView: TextView = view.findViewById(R.id.contact_business)
        private val contactEmailView: TextView = view.findViewById(R.id.contact_email)

        init {
            itemView.setOnClickListener {
                it.contact_details.visibility = if (it.contact_details.visibility == VISIBLE) GONE else VISIBLE
            }
        }

            fun bind(position: Int) {
                val contact = contactsList?.get(position)

                contactNameView.text = contact?.contactName

                if (contact?.contactCompany != null)
                    contactCompanyView.text = contact.contactCompany

                if (contact?.contactNumber?.isNotEmpty()!!)
                    contactMobileView.text = contact.contactNumber

                if (contact.emailAddress != null)
                    contactEmailView.text = contact.emailAddress
            }
    }
}