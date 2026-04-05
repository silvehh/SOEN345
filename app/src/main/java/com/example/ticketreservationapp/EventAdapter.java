package com.example.ticketreservationapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EventAdapter(List<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.tvEventName.setText(event.getEventName());
        holder.tvCategory.setText(event.getCategory());
        holder.tvEventMeta.setText(event.getVenue() + " · " + event.getTime());
        
        // Simple date parsing for display
        if (event.getDate() != null && event.getDate().contains("/")) {
            String[] parts = event.getDate().split("/");
            if (parts.length >= 2) {
                holder.tvEventDay.setText(parts[1]);
                String month = getMonthName(parts[0]);
                holder.tvEventMonth.setText(month);
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    private String getMonthName(String monthNum) {
        switch (monthNum) {
            case "01": case "1": return "JAN";
            case "02": case "2": return "FEB";
            case "03": case "3": return "MAR";
            case "04": case "4": return "APR";
            case "05": case "5": return "MAY";
            case "06": case "6": return "JUN";
            case "07": case "7": return "JUL";
            case "08": case "8": return "AUG";
            case "09": case "9": return "SEP";
            case "10": return "OCT";
            case "11": return "NOV";
            case "12": return "DEC";
            default: return "MON";
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateList(List<Event> newList) {
        this.eventList = newList;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventMeta, tvCategory, tvEventMonth, tvEventDay;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventMeta = itemView.findViewById(R.id.tvEventMeta);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvEventMonth = itemView.findViewById(R.id.tvEventMonth);
            tvEventDay = itemView.findViewById(R.id.tvEventDay);
        }
    }
}
