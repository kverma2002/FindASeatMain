package com.example.findaseat;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.fragment.app.Fragment;

import com.example.findaseat.Utils.User;

import org.junit.Test;

public class MainActivityTest {


    //The method under test is copied here
    public Fragment getFragmentForItem(int itemId, User user) {
        if (itemId == R.id.map) {
            return new MapFragment();
        } else if (itemId == R.id.profile) {
            if (user.getLoggedIn()) {
                return new ProfileFragment();
            } else {
                return new LogInFragment();
            }
        }
        return null; // this will not be triggered since the items in menu are fixed.
    }

    @Test
    public void SelectMap() {
        //If the map item in menu is clicked
        int itemId = R.id.map;
        User user = mock(User.class);

        Fragment resultFragment = getFragmentForItem(itemId, user);

        assertTrue(resultFragment instanceof MapFragment);
    }

    @Test
    public void SelectedProfileLoggedIn() {
        //If the profile item is clicked and user is logged in
        int itemId = R.id.profile;
        User user = mock(User.class);
        when(user.getLoggedIn()).thenReturn(true); //Mock that the user is logged in

        Fragment resultFragment = getFragmentForItem(itemId, user);

        assertTrue(resultFragment instanceof ProfileFragment);
    }

    @Test
    public void SelectedProfileNotLoggedIn() {
        //If the profile item is clicked but the user is not logged in yet
        int itemId = R.id.profile;
        User user = mock(User.class);
        when(user.getLoggedIn()).thenReturn(false); // Mock that user is not logged in

        Fragment resultFragment = getFragmentForItem(itemId, user);

        assertTrue(resultFragment instanceof LogInFragment);
    }
}