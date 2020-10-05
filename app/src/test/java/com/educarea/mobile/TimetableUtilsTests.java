package com.educarea.mobile;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class TimetableUtilsTests {

    @Test
    public void weekIsEvenEven1(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 9, 5);
        TimetableUtils utils = new TimetableUtils();
        Assert.assertTrue(utils.weekIsEven(calendar));
    }

    @Test
    public void weekIsEventNoEven1(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 9, 12);
        TimetableUtils utils = new TimetableUtils();
        Assert.assertFalse(utils.weekIsEven(calendar));
    }

    @Test
    public void weekIsEvenEven2(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, 1, 5);
        TimetableUtils utils = new TimetableUtils();
        Assert.assertTrue(utils.weekIsEven(calendar));
    }

    @Test
    public void weekIsEventNoEven2(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, 1, 9);
        TimetableUtils utils = new TimetableUtils();
        Assert.assertFalse(utils.weekIsEven(calendar));
    }
}
