package com.example.ticketreservationapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for EventFilterHelper class
 * 
 * Related User Story: #3 - Search and Filter System for Events by Date, Location, and Category
 * 
 * User Story Statement:
 * As a user,
 * I want to search for events by filtering by either date, location, or category,
 * so that it is easier to select an event as per my availability or liking.
 * 
 * Preconditions:
 * - The application is running
 * - The user has access to the app
 * 
 * Acceptance Criteria:
 * - Given this is a ticket reservation application
 * - When I want to find an event that fits my availability or liking
 * - Then I can filter the events list by date, location, or category
 */
public class EventFilterHelperTest {

    private EventFilterHelper filterHelper;
    private List<Event> testEvents;

    @Before
    public void setUp() {
        testEvents = new ArrayList<>();
        testEvents.add(new Event("Summer Music Festival", "Music", "04/15/2026", "7:00 PM", "Central Park, New York", 500, 75.00));
        testEvents.add(new Event("Basketball Championship", "Sports", "04/20/2026", "3:00 PM", "Madison Square Garden, New York", 200, 120.00));
        testEvents.add(new Event("Movie Premiere Night", "Movies", "04/10/2026", "8:00 PM", "AMC Theatre, Los Angeles", 150, 25.00));
        testEvents.add(new Event("Jazz Night", "Music", "04/18/2026", "9:00 PM", "Blue Note, Chicago", 100, 50.00));
        testEvents.add(new Event("Football Match", "Sports", "04/25/2026", "4:00 PM", "MetLife Stadium, New Jersey", 300, 85.00));
        testEvents.add(new Event("Adventure Travel Expo", "Travel", "04/12/2026", "10:00 AM", "Convention Center, San Francisco", 400, 15.00));
        
        filterHelper = new EventFilterHelper(testEvents);
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    public void constructor_withNullEvents_createsEmptyList() {
        EventFilterHelper helper = new EventFilterHelper(null);
        List<Event> result = helper.applyFilters();
        assertEquals(0, result.size());
    }

    @Test
    public void constructor_withEvents_storesAllEvents() {
        List<Event> result = filterHelper.applyFilters();
        assertEquals(6, result.size());
    }

    // ==================== SEARCH QUERY TESTS ====================

    @Test
    public void setSearchQuery_withNull_setsEmptyString() {
        filterHelper.setSearchQuery(null);
        assertEquals("", filterHelper.getSearchQuery());
    }

    @Test
    public void setSearchQuery_withWhitespace_trimsValue() {
        filterHelper.setSearchQuery("  music  ");
        assertEquals("music", filterHelper.getSearchQuery());
    }

    @Test
    public void applyFilters_withSearchQuery_filtersEventsByName() {
        filterHelper.setSearchQuery("Jazz");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(1, result.size());
        assertEquals("Jazz Night", result.get(0).getEventName());
    }

    @Test
    public void applyFilters_withSearchQuery_isCaseInsensitive() {
        filterHelper.setSearchQuery("MUSIC");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(1, result.size());
        assertEquals("Summer Music Festival", result.get(0).getEventName());
    }

    @Test
    public void applyFilters_withPartialSearchQuery_findsMatchingEvents() {
        filterHelper.setSearchQuery("ball");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(2, result.size());
    }

    @Test
    public void applyFilters_withEmptySearchQuery_returnsAllEvents() {
        filterHelper.setSearchQuery("");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(6, result.size());
    }

    // ==================== CATEGORY FILTER TESTS ====================
    // Acceptance Criteria: Filter events by category

    @Test
    public void setSelectedCategory_withNull_setsAll() {
        filterHelper.setSelectedCategory(null);
        assertEquals("All", filterHelper.getSelectedCategory());
    }

    @Test
    public void applyFilters_withCategoryMusic_filtersOnlyMusicEvents() {
        filterHelper.setSelectedCategory("Music");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(2, result.size());
        for (Event event : result) {
            assertEquals("Music", event.getCategory());
        }
    }

    @Test
    public void applyFilters_withCategorySports_filtersOnlySportsEvents() {
        filterHelper.setSelectedCategory("Sports");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(2, result.size());
        for (Event event : result) {
            assertEquals("Sports", event.getCategory());
        }
    }

    @Test
    public void applyFilters_withCategoryMovies_filtersOnlyMoviesEvents() {
        filterHelper.setSelectedCategory("Movies");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(1, result.size());
        assertEquals("Movies", result.get(0).getCategory());
    }

    @Test
    public void applyFilters_withCategoryTravel_filtersOnlyTravelEvents() {
        filterHelper.setSelectedCategory("Travel");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(1, result.size());
        assertEquals("Travel", result.get(0).getCategory());
    }

    @Test
    public void applyFilters_withCategoryAll_returnsAllEvents() {
        filterHelper.setSelectedCategory("All");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(6, result.size());
    }

    @Test
    public void applyFilters_withCategory_isCaseInsensitive() {
        filterHelper.setSelectedCategory("music");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(2, result.size());
    }

    // ==================== DATE FILTER TESTS ====================
    // Acceptance Criteria: Filter events by date

    @Test
    public void setSelectedDate_withNull_setsEmptyString() {
        filterHelper.setSelectedDate(null);
        assertEquals("", filterHelper.getSelectedDate());
    }

    @Test
    public void setSelectedDate_withWhitespace_trimsValue() {
        filterHelper.setSelectedDate("  04/15/2026  ");
        assertEquals("04/15/2026", filterHelper.getSelectedDate());
    }

    @Test
    public void applyFilters_withDate_filtersEventsByExactDate() {
        filterHelper.setSelectedDate("04/15/2026");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(1, result.size());
        assertEquals("Summer Music Festival", result.get(0).getEventName());
    }

    @Test
    public void applyFilters_withEmptyDate_returnsAllEvents() {
        filterHelper.setSelectedDate("");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(6, result.size());
    }

    @Test
    public void applyFilters_withNonExistingDate_returnsNoEvents() {
        filterHelper.setSelectedDate("12/31/2026");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(0, result.size());
    }

    // ==================== LOCATION FILTER TESTS ====================
    // Acceptance Criteria: Filter events by location

    @Test
    public void setSelectedLocation_withNull_setsEmptyString() {
        filterHelper.setSelectedLocation(null);
        assertEquals("", filterHelper.getSelectedLocation());
    }

    @Test
    public void setSelectedLocation_withWhitespace_trimsValue() {
        filterHelper.setSelectedLocation("  New York  ");
        assertEquals("new york", filterHelper.getSelectedLocation());
    }

    @Test
    public void applyFilters_withLocation_filtersEventsByVenue() {
        filterHelper.setSelectedLocation("New York");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(2, result.size());
    }

    @Test
    public void applyFilters_withLocation_isCaseInsensitive() {
        filterHelper.setSelectedLocation("CHICAGO");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(1, result.size());
        assertEquals("Jazz Night", result.get(0).getEventName());
    }

    @Test
    public void applyFilters_withPartialLocation_findsMatchingEvents() {
        filterHelper.setSelectedLocation("Convention");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(1, result.size());
        assertEquals("Adventure Travel Expo", result.get(0).getEventName());
    }

    @Test
    public void applyFilters_withEmptyLocation_returnsAllEvents() {
        filterHelper.setSelectedLocation("");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(6, result.size());
    }

    // ==================== COMBINED FILTER TESTS ====================
    // Acceptance Criteria: Combine multiple filters

    @Test
    public void applyFilters_withSearchAndCategory_combinesFilters() {
        filterHelper.setSearchQuery("Summer");
        filterHelper.setSelectedCategory("Music");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(1, result.size());
        assertEquals("Summer Music Festival", result.get(0).getEventName());
    }

    @Test
    public void applyFilters_withDateAndLocation_combinesFilters() {
        filterHelper.setSelectedDate("04/20/2026");
        filterHelper.setSelectedLocation("New York");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(1, result.size());
        assertEquals("Basketball Championship", result.get(0).getEventName());
    }

    @Test
    public void applyFilters_withCategoryAndLocation_combinesFilters() {
        filterHelper.setSelectedCategory("Sports");
        filterHelper.setSelectedLocation("New York");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(1, result.size());
        assertEquals("Basketball Championship", result.get(0).getEventName());
    }

    @Test
    public void applyFilters_withAllFilters_combinesAllFilters() {
        filterHelper.setSearchQuery("Basketball");
        filterHelper.setSelectedCategory("Sports");
        filterHelper.setSelectedDate("04/20/2026");
        filterHelper.setSelectedLocation("New York");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(1, result.size());
        assertEquals("Basketball Championship", result.get(0).getEventName());
    }

    @Test
    public void applyFilters_withConflictingFilters_returnsNoEvents() {
        filterHelper.setSelectedCategory("Music");
        filterHelper.setSelectedLocation("Los Angeles");
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(0, result.size());
    }

    // ==================== CLEAR FILTERS TESTS ====================

    @Test
    public void clearAllFilters_resetsAllFilters() {
        filterHelper.setSearchQuery("Jazz");
        filterHelper.setSelectedCategory("Music");
        filterHelper.setSelectedDate("04/18/2026");
        filterHelper.setSelectedLocation("Chicago");
        
        filterHelper.clearAllFilters();
        
        assertEquals("", filterHelper.getSearchQuery());
        assertEquals("All", filterHelper.getSelectedCategory());
        assertEquals("", filterHelper.getSelectedDate());
        assertEquals("", filterHelper.getSelectedLocation());
    }

    @Test
    public void clearAllFilters_returnsAllEventsAfterApply() {
        filterHelper.setSearchQuery("Jazz");
        filterHelper.setSelectedCategory("Music");
        
        filterHelper.clearAllFilters();
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(6, result.size());
    }

    // ==================== HAS ACTIVE FILTERS TESTS ====================

    @Test
    public void hasActiveFilters_withNoFilters_returnsFalse() {
        assertFalse(filterHelper.hasActiveFilters());
    }

    @Test
    public void hasActiveFilters_withSearchQuery_returnsTrue() {
        filterHelper.setSearchQuery("Jazz");
        assertTrue(filterHelper.hasActiveFilters());
    }

    @Test
    public void hasActiveFilters_withCategory_returnsTrue() {
        filterHelper.setSelectedCategory("Music");
        assertTrue(filterHelper.hasActiveFilters());
    }

    @Test
    public void hasActiveFilters_withCategoryAll_returnsFalse() {
        filterHelper.setSelectedCategory("All");
        assertFalse(filterHelper.hasActiveFilters());
    }

    @Test
    public void hasActiveFilters_withDate_returnsTrue() {
        filterHelper.setSelectedDate("04/15/2026");
        assertTrue(filterHelper.hasActiveFilters());
    }

    @Test
    public void hasActiveFilters_withLocation_returnsTrue() {
        filterHelper.setSelectedLocation("New York");
        assertTrue(filterHelper.hasActiveFilters());
    }

    @Test
    public void hasActiveFilters_afterClearFilters_returnsFalse() {
        filterHelper.setSearchQuery("Jazz");
        filterHelper.setSelectedCategory("Music");
        filterHelper.clearAllFilters();
        
        assertFalse(filterHelper.hasActiveFilters());
    }

    // ==================== GET ALL CATEGORIES TESTS ====================

    @Test
    public void getAllCategories_returnsAllUniqueCategories() {
        List<String> categories = filterHelper.getAllCategories();
        
        assertTrue(categories.contains("All"));
        assertTrue(categories.contains("Music"));
        assertTrue(categories.contains("Sports"));
        assertTrue(categories.contains("Movies"));
        assertTrue(categories.contains("Travel"));
        assertEquals(5, categories.size());
    }

    @Test
    public void getAllCategories_startsWithAll() {
        List<String> categories = filterHelper.getAllCategories();
        assertEquals("All", categories.get(0));
    }

    // ==================== GET ALL LOCATIONS TESTS ====================

    @Test
    public void getAllLocations_returnsAllUniqueLocations() {
        List<String> locations = filterHelper.getAllLocations();
        
        assertEquals(6, locations.size());
        assertTrue(locations.contains("Central Park, New York"));
        assertTrue(locations.contains("Madison Square Garden, New York"));
    }

    // ==================== GET ALL DATES TESTS ====================

    @Test
    public void getAllDates_returnsAllUniqueDates() {
        List<String> dates = filterHelper.getAllDates();
        
        assertEquals(6, dates.size());
        assertTrue(dates.contains("04/15/2026"));
        assertTrue(dates.contains("04/20/2026"));
    }

    // ==================== SET ALL EVENTS TESTS ====================

    @Test
    public void setAllEvents_withNull_setsEmptyList() {
        filterHelper.setAllEvents(null);
        List<Event> result = filterHelper.applyFilters();
        assertEquals(0, result.size());
    }

    @Test
    public void setAllEvents_updatesEventsList() {
        List<Event> newEvents = new ArrayList<>();
        newEvents.add(new Event("New Event", "Music", "05/01/2026", "8:00 PM", "Test Venue", 100, 50.00));
        
        filterHelper.setAllEvents(newEvents);
        List<Event> result = filterHelper.applyFilters();
        
        assertEquals(1, result.size());
        assertEquals("New Event", result.get(0).getEventName());
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    public void applyFilters_withEmptyEventsList_returnsEmptyList() {
        EventFilterHelper emptyHelper = new EventFilterHelper(new ArrayList<>());
        List<Event> result = emptyHelper.applyFilters();
        assertEquals(0, result.size());
    }

    @Test
    public void getAllCategories_withEmptyList_returnsOnlyAll() {
        EventFilterHelper emptyHelper = new EventFilterHelper(new ArrayList<>());
        List<String> categories = emptyHelper.getAllCategories();
        assertEquals(1, categories.size());
        assertEquals("All", categories.get(0));
    }

    @Test
    public void getAllLocations_withEmptyList_returnsEmptyList() {
        EventFilterHelper emptyHelper = new EventFilterHelper(new ArrayList<>());
        List<String> locations = emptyHelper.getAllLocations();
        assertEquals(0, locations.size());
    }

    @Test
    public void getAllDates_withEmptyList_returnsEmptyList() {
        EventFilterHelper emptyHelper = new EventFilterHelper(new ArrayList<>());
        List<String> dates = emptyHelper.getAllDates();
        assertEquals(0, dates.size());
    }
}
