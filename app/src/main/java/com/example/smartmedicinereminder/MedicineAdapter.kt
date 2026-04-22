package com.example.smartmedicinereminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    List<Medicine> list;
    DatabaseHelper dbHelper;

    public MedicineAdapter(List<Medicine> list, DatabaseHelper dbHelper) {
        this.list = list;
        this.dbHelper = dbHelper;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDosage, tvTime;
        Button btnDelete;
        CheckBox checkTaken;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDosage = itemView.findViewById(R.id.tvDosage);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            checkTaken = itemView.findViewById(R.id.checkTaken);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Medicine m = list.get(position);

        holder.tvName.setText(m.getName());
        holder.tvDosage.setText(m.getDosage());
        holder.tvTime.setText(m.getTime());

        // ✅ IMPORTANT: Reset listener first (prevents auto-trigger bug)
        holder.checkTaken.setOnCheckedChangeListener(null);

        // ✅ SET CHECKBOX BASED ON DATABASE STATUS
        holder.checkTaken.setChecked(m.getStatus() == 1);

        // ✅ WHEN USER MANUALLY CHECKS
        holder.checkTaken.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int status = isChecked ? 1 : 0;
            m.setStatus(status);
            dbHelper.updateStatus(m.getId(), status);
        });

        // 🗑 DELETE BUTTON
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {
                Medicine medicine = list.get(pos);

                dbHelper.deleteMedicine(medicine.getId());
                list.remove(pos);
                notifyItemRemoved(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}