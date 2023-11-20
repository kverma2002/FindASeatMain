package com.example.findaseat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.widget.Spinner;

import androidx.fragment.app.FragmentActivity;

import com.example.findaseat.Utils.Reservation;
import com.example.findaseat.Utils.Seat;
import com.example.findaseat.Utils.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;


@RunWith(MockitoJUnitRunner.class)
public class SeatFragmentTest {
    @Mock
    private QueryDocumentSnapshot mockReservation;
    SeatFragment fragment;
    @Mock
    private FragmentActivity mockActivity;
    @Mock
    private User mockUser;
    private Seat seat;
    private Timestamp currentResStartTime;
    private Timestamp currentResEndTime;
    //Below are Unix timestamp, number of seconds after 1970,1,1, 0:00:00, UTC
    long Time120000 = 1700136000L;
    long Time140000 =  1700143200L;
    long Time160000 = 1700150400L;
    long Time130000 = 1700139600L;
    long Time133000 = 1700141400L;
    long Time143000 = 1700145000L;
    long Time163000 = 1700152200L;
    long Time150000 = 1700146800L;
    private Spinner startSpinner;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        //Mock the fragment using spy()
        fragment = Mockito.spy(new SeatFragment());
        startSpinner = mock(Spinner.class);
        fragment.startTimeSpinner = startSpinner;

        Mockito.doReturn(mockActivity).when(fragment).getActivity();
        Mockito.doReturn(mockUser).when(mockActivity).getApplicationContext();
        seat = new Seat(12L,"testseat",true,"room1",1);
        Field field = SeatFragment.class.getDeclaredField("seat");
        field.setAccessible(true);
        field.set(fragment, seat);


        //Below is the reservation time that all test cases will be compared to (all on same day Nov. 16)
        currentResStartTime = new Timestamp(Time140000,0); //Nov 16, 14:00:00
        currentResEndTime = new Timestamp(Time160000,0); //Nov 16, 16:00:00

        Mockito.doNothing().when(fragment).showToast(anyString());
    }

    @Test
    public void testNoOverlap() {

        when(mockReservation.getString("user")).thenReturn("anothertest@example.com");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockReservation.getTimestamp("startTime")).thenReturn(currentResStartTime);
        when(mockReservation.getTimestamp("endTime")).thenReturn(currentResEndTime);
        when(mockReservation.getLong("seatId")).thenReturn(12L);

        // Set up the start and end time of the reservation the user is making
        Timestamp startTime1 = new Timestamp(Time120000,0);
        Timestamp endTime1 = new Timestamp(Time140000,0);

        Timestamp startTime2 = new Timestamp(Time160000,0);
        Timestamp endTime2 = new Timestamp(Time163000,0);

        Timestamp startTime3 = new Timestamp(Time120000,0);
        Timestamp endTime3 = new Timestamp(Time130000,0);

        boolean result1 = fragment.checkOverlap(mockReservation, startTime1, endTime1);
        boolean result2 = fragment.checkOverlap(mockReservation, startTime2, endTime2);
        boolean result3 = fragment.checkOverlap(mockReservation, startTime3, endTime3);
        // Should have no overlap
        assertFalse(result1);
        assertFalse(result2);
        assertFalse(result3);
        Mockito.verify(mockReservation, atLeastOnce()).getString(anyString());
        Mockito.verify(mockUser, atLeastOnce()).getEmail();


    }

    @Test
    public void testOverlap() {
        when(mockReservation.getString("user")).thenReturn("anothertest@example.com");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockReservation.getTimestamp("startTime")).thenReturn(currentResStartTime);
        when(mockReservation.getTimestamp("endTime")).thenReturn(currentResEndTime);
        when(mockReservation.getLong("seatId")).thenReturn(12L);
        // Set up overlapping reservation times
        Timestamp startTime1 = new Timestamp(Time130000, 0);
        Timestamp endTime1 = new Timestamp(Time143000, 0);

        Timestamp startTime2 = new Timestamp(Time150000,0);
        Timestamp endTime2 = new Timestamp(Time160000,0);

        Timestamp startTime3 = new Timestamp(Time150000,0);
        Timestamp endTime3 = new Timestamp(Time163000,0);

        Timestamp startTime4 = new Timestamp(Time140000,0);
        Timestamp endTime4 = new Timestamp(Time150000,0);

        // Call the method under test
        boolean result1 = fragment.checkOverlap(mockReservation, startTime1, endTime1);
        boolean result2 = fragment.checkOverlap(mockReservation, startTime2, endTime2);
        boolean result3 = fragment.checkOverlap(mockReservation, startTime3, endTime3);
        boolean result4 = fragment.checkOverlap(mockReservation, startTime4, endTime4);

        // Assert that there is an overlap
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
        assertTrue(result4);
    }

    @Test
    public void testSameUser() {
        //If the user already has a reservation, then the user should not make another one
        when(mockReservation.getString("user")).thenReturn("test@example.com");
        when(mockUser.getEmail()).thenReturn("test@example.com"); // same as above
        when(mockReservation.getTimestamp("startTime")).thenReturn(currentResStartTime);
        when(mockReservation.getTimestamp("endTime")).thenReturn(currentResEndTime);
        //New reservation time is valid, but due to same user, it should be marked overlap, thus return true
        Timestamp startTime1 = new Timestamp(Time120000,0);
        Timestamp endTime1 = new Timestamp(Time140000,0);

        boolean result1 = fragment.checkOverlap(mockReservation, startTime1, endTime1);
        assertTrue(result1);
    }

    @Test
    public void testDifferentSeat() {

        when(mockReservation.getString("user")).thenReturn("anothertest@example.com");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockReservation.getTimestamp("startTime")).thenReturn(currentResStartTime);
        when(mockReservation.getTimestamp("endTime")).thenReturn(currentResEndTime);
        when(mockReservation.getLong("seatId")).thenReturn(15L);// Seat is different, not 12L.

        //Since the reservations being checked are not at the same seat, we simply skip it.
        Timestamp startTime1 = new Timestamp(Time120000,0);
        Timestamp endTime1 = new Timestamp(Time140000,0);

        boolean result1 = fragment.checkOverlap(mockReservation, startTime1, endTime1);
        assertFalse(result1);
    }

    @Test
    public void testGetTime() throws Exception{

        String mockStartTime = "8:00";
        when(startSpinner.getSelectedItem()).thenReturn(mockStartTime);
        Calendar expectedTime = Calendar.getInstance();
        Date currentDate = expectedTime.getTime();
        expectedTime.setTime(currentDate);
        expectedTime.set(Calendar.HOUR_OF_DAY, 8);
        expectedTime.set(Calendar.MINUTE, 0);
        expectedTime.set(Calendar.SECOND, 0);

        Timestamp expectedTimestamp = new Timestamp(expectedTime.getTime());

        Timestamp result = fragment.getTime(0); // Should give 8:30 today
        assertEquals(result.getSeconds(),expectedTimestamp.getSeconds());

        mockStartTime = "19:00";
        when(startSpinner.getSelectedItem()).thenReturn(mockStartTime);
        expectedTime.set(Calendar.HOUR_OF_DAY, 19);
        expectedTime.add(Calendar.MINUTE, 120);
        expectedTimestamp = new Timestamp(expectedTime.getTime());
        result = fragment.getTime(120);
        assertEquals(result.getSeconds(),expectedTimestamp.getSeconds());

    }

    @Test
    public void testClose() throws Exception {
        String closetime = "15:30";
        //Checking if the close() method can return a Timestamp
        //using the time expressed by the string
        Calendar expectedTime = Calendar.getInstance();
        Date currentDate = expectedTime.getTime();
        expectedTime.setTime(currentDate);
        expectedTime.set(Calendar.HOUR_OF_DAY, 15);
        expectedTime.set(Calendar.MINUTE, 30);
        expectedTime.set(Calendar.SECOND, 0);
        Timestamp expectedTimestamp = new Timestamp(expectedTime.getTime());

        Timestamp result = fragment.close(closetime);

        assertEquals(expectedTimestamp.getSeconds(), result.getSeconds());

        closetime = "22:30";
        expectedTime.set(Calendar.HOUR_OF_DAY, 22);
        expectedTime.set(Calendar.MINUTE, 30);
        expectedTimestamp = new Timestamp(expectedTime.getTime());
        result = fragment.close(closetime);
        assertEquals(result.getSeconds(),expectedTimestamp.getSeconds());
    }

    @Test
    public void testCreateReservation(){
        QueryDocumentSnapshot mockSnapshot = mock(QueryDocumentSnapshot.class);
        when(mockSnapshot.getString("building")).thenReturn("testBuilding");
        when(mockSnapshot.getString("room")).thenReturn("testRoom");
        when(mockSnapshot.getBoolean("cancelled")).thenReturn(false);
        when(mockSnapshot.getId()).thenReturn("testId");

        //The time are passed but in this method we only test the creation of reservation object
        //we don't check validity of reservation time here.
        Timestamp startTimestamp = new Timestamp(Time133000,0); // Nov 16, 13:30, UTC
        Timestamp endTimestamp = new Timestamp(Time150000,0); // Nov 16, 15:00, UTC

        when(mockSnapshot.getTimestamp("startTime")).thenReturn(startTimestamp);
        when(mockSnapshot.getTimestamp("endTime")).thenReturn(endTimestamp);


        Reservation result = fragment.createReservation(mockSnapshot);

        // Assert
        assertEquals("testBuilding", result.getBuilding());
        assertEquals("testRoom", result.getRoom());
        assertEquals("11-16", result.getDate());
        //The timestamp created using UTC time, we are in PST, which is UTC - 8
        assertEquals("05:30", result.getStartTime());
        assertEquals("07:00", result.getEndTime());

        assertEquals("testId", result.getId());
        assertFalse(result.isCancelled());
    }

}