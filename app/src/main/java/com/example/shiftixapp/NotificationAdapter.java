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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notificationList = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public void setNotificationList(List<Notification> notifications) {
        this.notificationList = notifications;
        android.util.Log.d("ShiftixApp", "NotificationAdapter updated with " + notifications.size() + " notifications");
        notifyDataSetChanged();
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.messageTextView.setText(notification.getMessage());
        holder.createdAtTextView.setText("Created: " + dateFormat.format(notification.getCreatedAt()));
        holder.statusTextView.setText(notification.isRead() ? "Read" : "Unread");
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, createdAtTextView, statusTextView;

        NotificationViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            createdAtTextView = itemView.findViewById(R.id.createdAtTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);

        }
    }
}