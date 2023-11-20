package com.example.findaseat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import com.example.findaseat.Utils.Building;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BuildingFragmentTest {


    @Mock
    private DocumentSnapshot mockDocumentSnapshot;

    private BuildingFragment fragment;

    @Before
    public void setUp() {
        fragment = new BuildingFragment();

    }

    @Test
    public void testGetBuildingInfo() {
        // Call your method here
        Building testBuilding = new Building();
        testBuilding.setDescription("testDescription");
        testBuilding.setAddress("testAddress");
        testBuilding.setName("testName");
        when(mockDocumentSnapshot.toObject(Building.class)).thenReturn(testBuilding);
        when(mockDocumentSnapshot.getLong("open")).thenReturn(16L);
        when(mockDocumentSnapshot.getId()).thenReturn("testBuilding");
        Building result = fragment.getBuildingInfo(mockDocumentSnapshot);


        // Assert that the correct building object is returned.
        assertNotNull(result);
        assertEquals(16L, result.getOpenTime());
        assertEquals("testBuilding", fragment.id);
        assertEquals("testDescription",result.getDescription());
        assertEquals("testAddress",result.getAddress());
        assertEquals("testName",result.getName());
    }
}