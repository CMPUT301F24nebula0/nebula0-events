//package com.example.pickme_nebula0;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//
//import android.media.Image;
//import android.widget.ArrayAdapter;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//
//public class AdminRoleTest {
//
//    private AdminRole admin;
//    private ArrayList<User> usersList;
//    private ArrayList<String> imagesList;
//    private ArrayList<Event> eventsList;
//    private ArrayList<Facility> facilitiesList;
//    private User user;
//    private Event event;
//    private Facility facility;
//
//    @Before
//    public void setUp() {
//        // Initializing lists
//        usersList = new ArrayList<>();
//        imagesList = new ArrayList<>();
//        eventsList = new ArrayList<>();
//        facilitiesList = new ArrayList<>();
//
//        // Initializing adapters (can be mocked if needed)
//        ArrayAdapter<User> userAdapter = new ArrayAdapter<>(null, 0, usersList);
//        ArrayAdapter<String> imageAdapter = new ArrayAdapter<>(null, 0, imagesList);
//        ArrayAdapter<Event> eventAdapter = new ArrayAdapter<>(null, 0, eventsList);
//        ArrayAdapter<Facility> facilityAdapter = new ArrayAdapter<>(null, 0, facilitiesList);
//
//        // Creating test data
//        user = new User("userID", "firstName", "lastName", "email", "phoneNumber");
//        event = new Event();
//        facility = new Facility();
//
//        // Adding test data to lists
//        usersList.add(user);
//        imagesList.add("imageID");
//        eventsList.add(event);
//        facilitiesList.add(facility);
//
//        // Initializing admin role
//        admin = new AdminRole("adminID", usersList, userAdapter, imagesList, imageAdapter, eventsList, eventAdapter, facilitiesList, facilityAdapter);
//    }
//
//
//    @Test
//    public void testRemoveEvent() {
//        // Removing event
//        admin.removeEvent(event);
//        assertFalse(eventsList.contains(event));
//    }
//    @Test
//    public void testRemoveUser() {
//        // Removing profile
//        admin.removeUser(user);
//        assertFalse(usersList.contains(user));
//    }
//    @Test
//    public void testRemoveImage() {
//        String imageID = "imageID";
//        admin.removeImage(imageID);
//        assertFalse(imagesList.contains(imageID));
//    }
//    @Test
//    public void testRemoveHashedQRCodeData() {
//        String hashData = "hashedQRCodeData";
//        imagesList.add(hashData);
//        admin.removeHashedQRCodeData(hashData);
//        assertFalse(imagesList.contains(hashData));
//    }
//    @Test
//    public void testBrowseEvents() {
//        // Just invoking the method here, ensuring no exceptions are thrown
//        admin.browseEvents();
//        assertEquals(1, eventsList.size());
//    }
//
//    @Test
//    public void testBrowseUsers() {
//        admin.browseUsers();
//        assertEquals(1, usersList.size());
//    }
//
//    @Test
//    public void testBrowseImages() {
//        admin.browseImages();
//        assertEquals(1, imagesList.size());
//    }
//
//    @Test
//    public void testRemoveFacility() {
//        facilitiesList.add(facility);
//        admin.removeFacility();
//        assertFalse(facilitiesList.contains(facility));
//    }
//}
//
//
//
//
//
//
//
//
//
//
