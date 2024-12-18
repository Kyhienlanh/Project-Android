package com.example.projectdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.notificationMessage.text = notification.message
        holder.notificationTimestamp.text = getFormattedTimestamp(notification.timestamp)
        holder.notificationTitle.text = notification.type
    }

    override fun getItemCount(): Int = notifications.size

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val notificationTitle: TextView = view.findViewById(R.id.notificationTitle)
        val notificationMessage: TextView = view.findViewById(R.id.notificationMessage)
        val notificationTimestamp: TextView = view.findViewById(R.id.notificationTimestamp)
    }

    private fun getFormattedTimestamp(timestamp: Long?): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return if (timestamp != null) {
            dateFormat.format(Date(timestamp))
        } else {
            "N/A"
        }
    }
}
