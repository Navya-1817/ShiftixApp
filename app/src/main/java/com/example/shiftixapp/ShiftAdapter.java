package com.example.shiftixapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ShiftViewHolder> {
    private List<Shift> shiftList = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public void setShiftList(List<Shift> shifts) {
        this.shiftList = shifts;
        android.util.Log.d("ShiftixApp", "ShiftAdapter updated with " + shifts.size() + " shifts");
        notifyDataSetChanged();
    }

    public List<Shift> getShiftList() {
        return shiftList;
    }

    @Override
    public ShiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
        return new ShiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShiftViewHolder holder, int position) {
        Shift shift = shiftList.get(position);
        holder.departmentTextView.setText(shift.getDepartment());
        String time = dateFormat.format(shift.getStartTime()) + " - " + dateFormat.format(shift.getEndTime());
        holder.timeTextView.setText(time);
        android.util.Log.d("ShiftixApp", "Binding shift: " + shift.getShiftId() + " at position " + position);
    }

    @Override
    public int getItemCount() {
        android.util.Log.d("ShiftixApp", "ShiftAdapter item count: " + shiftList.size());
        return shiftList.size();
    }

    static class ShiftViewHolder extends RecyclerView.ViewHolder {
        TextView departmentTextView, timeTextView;

        ShiftViewHolder(View itemView) {
            super(itemView);
            departmentTextView = itemView.findViewById(R.id.departmentTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}