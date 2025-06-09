package com.example.shiftixapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SwapRequestAdapter extends RecyclerView.Adapter<SwapRequestAdapter.SwapRequestViewHolder> {
    private List<SwapRequest> swapRequestList = new ArrayList<>();
    private List<Shift> shiftList = new ArrayList<>();
    private boolean isAdmin;
    private OnSwapRequestActionListener actionListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public interface OnSwapRequestActionListener {
        void onApprove(String requestId);
        void onReject(String requestId);
    }

    public SwapRequestAdapter(boolean isAdmin, OnSwapRequestActionListener listener) {
        this.isAdmin = isAdmin;
        this.actionListener = listener;
    }

    public void setSwapRequestList(List<SwapRequest> requests, List<Shift> shifts) {
        this.swapRequestList = requests;
        this.shiftList = shifts;
        notifyDataSetChanged();
    }

    @Override
    public SwapRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swap_request, parent, false);
        return new SwapRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SwapRequestViewHolder holder, int position) {
        SwapRequest request = swapRequestList.get(position);
        Shift shiftFrom = findShift(request.getShiftFromId());
        Shift shiftTo = findShift(request.getShiftToId());

        holder.shiftFromTextView.setText(shiftFrom != null ? shiftFrom.getDepartment() + " (" + dateFormat.format(shiftFrom.getStartTime()) + ")" : "Unknown");
        holder.shiftToTextView.setText(shiftTo != null ? shiftTo.getDepartment() + " (" + dateFormat.format(shiftTo.getStartTime()) + ")" : "Unknown");
        holder.statusTextView.setText("Status: " + request.getStatus());

        if (isAdmin && request.getStatus().equals("Pending")) {
            holder.adminControls.setVisibility(View.VISIBLE);
            holder.approveButton.setOnClickListener(v -> actionListener.onApprove(request.getRequestId()));
            holder.rejectButton.setOnClickListener(v -> actionListener.onReject(request.getRequestId()));
        } else {
            holder.adminControls.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return swapRequestList.size();
    }

    private Shift findShift(String shiftId) {
        for (Shift shift : shiftList) {
            if (shift.getShiftId().equals(shiftId)) {
                return shift;
            }
        }
        return null;
    }

    static class SwapRequestViewHolder extends RecyclerView.ViewHolder {
        TextView shiftFromTextView, shiftToTextView, statusTextView;
        LinearLayout adminControls;
        Button approveButton, rejectButton;

        SwapRequestViewHolder(View itemView) {
            super(itemView);
            shiftFromTextView = itemView.findViewById(R.id.shiftFromTextView);
            shiftToTextView = itemView.findViewById(R.id.shiftToTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            adminControls = itemView.findViewById(R.id.adminControls);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}