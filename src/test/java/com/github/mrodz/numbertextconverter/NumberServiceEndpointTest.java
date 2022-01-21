package com.github.mrodz.numbertextconverter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberServiceEndpointTest {
    NumberServiceEndpoint n = new NumberServiceEndpoint();

    @Test
    public void five() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("5");
        assertEquals("five", conversion.getResult());
    }

    @Test
    public void seventeen() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("17");
        assertEquals("seventeen", conversion.getResult());
    }

    @Test
    public void tenThousand() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("10");
        assertEquals("ten", conversion.getResult());
    }

    @Test
    public void thirtyThree() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("33");
        assertEquals("thirty three", conversion.getResult());
    }
}