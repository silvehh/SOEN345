package com.example.ticketreservationapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to filter events by date, location (venue), and category.
 */
public class EventFilterHelper {

    private List<Event> allEvents;
    private String searchQuery = "";
    private String selectedCategory = "All";
    private String selectedDate = "";
    private String selectedLocation = "";

    public EventFilterHelper(List<Event> events) {
        this.allEvents = events != null ? events : new ArrayList<>();
    }

    public void setAllEvents(List<Event> events) {
        this.allEvents = events != null ? events : new ArrayList<>();
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query != null ? query.toLowerCase().trim() : "";
    }

    public void setSelectedCategory(String category) {
        this.selectedCategory = category != null ? category : "All";
    }

    public void setSelectedDate(String date) {
        this.selectedDate = date != null ? date.trim() : "";
    }

    public void setSelectedLocation(String location) {
        this.selectedLocation = location != null ? location.toLowerCase().trim() : "";
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }

    public void clearAllFilters() {
        this.searchQuery = "";
        this.selectedCategory = "All";
        this.selectedDate = "";
        this.selectedLocation = "";
    }

    public List<Event> applyFilters() {
        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : allEvents) {
            if (matchesAllFilters(event)) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    private boolean matchesAllFilters(Event event) {
        return matchesSearchQuery(event)
                && matchesCategory(event)
                && matchesDate(event)
                && matchesLocation(event);
    }

    private boolean matchesSearchQuery(Event event) {
        if (searchQuery.isEmpty()) return true;
        return event.getEventName().toLowerCase().contains(searchQuery);
    }

    private boolean matchesCategory(Event event) {
        if (selectedCategory.equals("All") || selectedCategory.isEmpty()) return true;
        return event.getCategory().equalsIgnoreCase(selectedCategory);
    }

    private boolean matchesDate(Event event) {
        if (selectedDate.isEmpty()) return true;
        return event.getDate().equals(selectedDate);
    }

    private boolean matchesLocation(Event event) {
        if (selectedLocation.isEmpty()) return true;
        return event.getVenue().toLowerCase().contains(selectedLocation);
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        for (Event event : allEvents) {
            String category = event.getCategory();
            if (category != null && !categories.contains(category)) {
                categories.add(category);
            }
        }
        return categories;
    }

    public List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        for (Event event : allEvents) {
            String venue = event.getVenue();
            if (venue != null && !locations.contains(venue)) {
                locations.add(venue);
            }
        }
        return locations;
    }

    public List<String> getAllDates() {
        List<String> dates = new ArrayList<>();
        for (Event event : allEvents) {
            String date = event.getDate();
            if (date != null && !dates.contains(date)) {
                dates.add(date);
            }
        }
        return dates;
    }

    public boolean hasActiveFilters() {
        return !searchQuery.isEmpty()
                || !selectedCategory.equals("All")
                || !selectedDate.isEmpty()
                || !selectedLocation.isEmpty();
    }
}
