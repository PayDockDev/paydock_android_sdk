package com.paydock.androidsdk.Tools;

import org.junit.Assert;
import org.junit.Test;

public class DateHelperTest {

    @Test
    public void isFuture() throws Exception {
        String monthString = "01";
        String yearString = "19";
        Assert.assertTrue(DateHelper.isFuture(monthString, yearString));
    }

    @Test
    public void isFuture1() throws Exception {
        String monthString = "01";
        String yearString = "01";
        Assert.assertFalse(DateHelper.isFuture(monthString, yearString));
    }
    @Test
    public void isFuture2() throws Exception {
        String monthString = "234324";
        String yearString = "192343";
        Assert.assertFalse(DateHelper.isFuture(monthString, yearString));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void convertCardIOFormat() throws Exception {
        Integer monthInt = null;
        Integer yearInt = null;
        Assert.assertNull(DateHelper.convertCardIOFormat(monthInt, yearInt));
    }

    @Test
    public void convertCardIOFormat1() throws Exception {
        Integer monthInt = 123;
        Integer yearInt = 456654;
        Assert.assertNull(DateHelper.convertCardIOFormat(monthInt, yearInt));
    }

    @Test
    public void convertCardIOFormat2() throws Exception {
        Integer monthInt = 1;
        Integer yearInt = 2019;
        Assert.assertNotNull(DateHelper.convertCardIOFormat(monthInt, yearInt));
    }

    @Test
    public void convertCardIOFormat3() throws Exception {
        Integer monthInt = 10;
        Integer yearInt = 2019;
        Assert.assertNotNull(DateHelper.convertCardIOFormat(monthInt, yearInt));
    }

    @Test
    public void convertCardIOFormat4() throws Exception {
        Integer monthInt = 1;
        Integer yearInt = 456654;
        Assert.assertNull(DateHelper.convertCardIOFormat(monthInt, yearInt));
    }

    @Test
    public void convertCardIOFormat5() throws Exception {
        Integer monthInt = 1;
        Integer yearInt = 0;
        Assert.assertNull(DateHelper.convertCardIOFormat(monthInt, yearInt));
    }


}