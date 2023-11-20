package com.example.findaseat;

import static org.junit.Assert.assertEquals;

import com.example.findaseat.Utils.MilitaryTimeMapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MilitaryTimeMapperTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    @Test
    public void testGetMilitaryTime_ValidInputs() {
        assertEquals("00:00", MilitaryTimeMapper.getMilitaryTime(0));
        assertEquals("07:30", MilitaryTimeMapper.getMilitaryTime(15));
        assertEquals("23:30", MilitaryTimeMapper.getMilitaryTime(47));
    }

    @Test()
    public void testGetMilitaryTime_InvalidInputLow() {
        exceptionRule.expect(IllegalArgumentException.class);
        int hour = -1;
        exceptionRule.expectMessage("Invalid military time hour: " + hour);
        MilitaryTimeMapper.getMilitaryTime(hour);
    }

}