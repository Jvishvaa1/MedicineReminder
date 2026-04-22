package com.example.smartmedicinereminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicineAdapter(
    private val list: MutableList<Medicine>,
    private val dbHelper: DatabaseHelper
) : RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDosage: TextView = itemView.findViewById(R.id.tvDosage)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val checkTaken: CheckBox = itemView.findViewById(R.id.checkTaken)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val m = list[position]

        holder.tvName.text = m.name
        holder.tvDosage.text = m.dosage
        holder.tvTime.text = m.time

        // ✅ Prevent auto-trigger bug
        holder.checkTaken.setOnCheckedChangeListener(null)

        // ✅ Set checkbox state
        holder.checkTaken.isChecked = (m.status == 1)

        // ✅ When user clicks checkbox
        holder.checkTaken.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) 1 else 0
            m.status = status
            dbHelper.updateStatus(m.id, status)
        }

        // 🗑 Delete button
        holder.btnDelete.setOnClickListener {
            val pos = holder.adapterPosition

            if (pos != RecyclerView.NO_POSITION) {
                val medicine = list[pos]

                dbHelper.deleteMedicine(medicine.id)
                list.removeAt(pos)
                notifyItemRemoved(pos)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}