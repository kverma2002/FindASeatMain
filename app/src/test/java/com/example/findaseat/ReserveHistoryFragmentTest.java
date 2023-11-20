package com.example.findaseat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.example.findaseat.Utils.BoolResPair;
import com.example.findaseat.Utils.Reservation;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class ReserveHistoryFragmentTest {

    @Mock
    private QueryDocumentSnapshot mockReservation;

    private ReserveHistoryFragment fragment;
    @Before
    public void setUp() throws Exception {
        fragment = new ReserveHistoryFragment();
    }
    //The following three tests are on AddReservationToTable() method.
    @Test
    public void testAddReservationToTable_NotCancelled() {
        // Mock the reservation data
        Date testdate = new Date(new Date().getTime() + 10000);
        Timestamp futureTimestamp = new Timestamp(testdate); // Future time
        when(mockReservation.getTimestamp("startTime")).thenReturn(futureTimestamp);
        when(mockReservation.getTimestamp("endTime")).thenReturn(futureTimestamp);
        when(mockReservation.getString("building")).thenReturn("testBuilding");
        when(mockReservation.getString("room")).thenReturn("testRoom");
        when(mockReservation.getBoolean("cancelled")).thenReturn(false);
        when(mockReservation.getId()).thenReturn("testID");

        // Call the method
        BoolResPair result = fragment.addReservationToTable(mockReservation);

        //Verify the result
        assertNotNull("A valid BoolResPair should be created", result);
        assertEquals("This reservation is not cancelled, so the first item should be true",true, (Boolean) result.getFirst());
        assertTrue("The second item should be a reservation object",result.getSecond() instanceof Reservation);
        if (result.getSecond() instanceof Reservation) {
            Reservation reservation = (Reservation) result.getSecond();
            assertEquals("The reservation object created should use the fields from the input Snapshot","testBuilding", reservation.getBuilding());
            assertEquals("The reservation object created should be marked not cancelled",false, reservation.isCancelled());
        }
    }

    @Test
    public void testAddReservationToTable_Cancelled() {
        // Mock the reservation data
        Date testdate = new Date(new Date().getTime() + 10000);
        Timestamp futureTimestamp = new Timestamp(testdate); // Future time
        when(mockReservation.getTimestamp("startTime")).thenReturn(futureTimestamp);
        when(mockReservation.getTimestamp("endTime")).thenReturn(futureTimestamp);
        when(mockReservation.getString("building")).thenReturn("testBuilding");
        when(mockReservation.getString("room")).thenReturn("testRoom");
        when(mockReservation.getBoolean("cancelled")).thenReturn(true);
        when(mockReservation.getId()).thenReturn("testID");

        // Call the method
        BoolResPair result = fragment.addReservationToTable(mockReservation);

        //Verify the result
        assertNotNull("A valid BoolResPair should be created", result);
        assertEquals("This reservation is cancelled, so the first item should be false",false, (Boolean) result.getFirst());
        assertTrue("The second item should be a reservation object",result.getSecond() instanceof Reservation);
        if (result.getSecond() instanceof Reservation) {
            Reservation reservation = (Reservation) result.getSecond();
            assertEquals("The reservation object created should use the fields from the input Snapshot","testBuilding", reservation.getBuilding());
            assertEquals("The reservation object created should be marked cancelled",true, reservation.isCancelled());
        }
    }

    @Test
    public void testAddReservationToTable_Passed() {
        // Mock the reservation data
        Date testdate = new Date(new Date().getTime() - 100000);//starttime before current time
        Timestamp futureTimestamp = new Timestamp(testdate); // Future time
        when(mockReservation.getTimestamp("startTime")).thenReturn(futureTimestamp);
        when(mockReservation.getTimestamp("endTime")).thenReturn(futureTimestamp);
        when(mockReservation.getString("building")).thenReturn("testBuilding");
        when(mockReservation.getString("room")).thenReturn("testRoom");
        when(mockReservation.getBoolean("cancelled")).thenReturn(false);
        when(mockReservation.getId()).thenReturn("testID");

        // Call the method
        BoolResPair result = fragment.addReservationToTable(mockReservation);

        //Verify the result
        assertNotNull("A valid BoolResPair should be created", result);
        assertEquals("This reservation is not cancelled, but startdate is before current time, so should be false",false, (Boolean) result.getFirst());
        assertTrue("The second item should be a reservation object",result.getSecond() instanceof Reservation);
        if (result.getSecond() instanceof Reservation) {
            Reservation reservation = (Reservation) result.getSecond();
            assertEquals("The reservation object created should use the fields from the input Snapshot","testBuilding", reservation.getBuilding());
            assertEquals("The reservation object created should be marked not cancelled",false, reservation.isCancelled());
        }
    }
    @Test
    public void testAddReservationToTable_NULL() {
        // Mock the reservation data
        mockReservation = null;
        // Call the method
        BoolResPair result = fragment.addReservationToTable(mockReservation);
        //Verify the result
        assertNull("Result should be null", result);
    }

}