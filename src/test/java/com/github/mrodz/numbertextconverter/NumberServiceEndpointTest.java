package com.github.mrodz.numbertextconverter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NumberServiceEndpointTest {
    NumberServiceEndpoint n = new NumberServiceEndpoint();

    @Test
    public void crash1() {
        assertThrows(NumberFormatException.class, () -> n.getConversion("1.5"));
    }

    @Test
    public void crash2() {
        assertThrows(NumberFormatException.class, () -> n.getConversion("This is Not a Number"));
    }

    @Test
    public void allZeroes() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("000000000");
        assertEquals("zero", conversion.getResult());
    }

    @Test
    public void leadingZeroes() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("00000000005550");
        assertEquals("five thousand five hundred fifty", conversion.getResult());
    }

    @Test
    public void negativeWithLeadingZeroes() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("-0000000000123");
        assertEquals("negative one hundred twenty three", conversion.getResult());
    }

    @Test
    public void negativeSevenHundredSeventySeven() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("-777");
        assertEquals("negative seven hundred seventy seven", conversion.getResult());
    }

    @Test
    public void negativeTenTrillion() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("-10000000000000");
        assertEquals("negative ten trillion", conversion.getResult());
    }

    @Test
    public void one() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("1");
        assertEquals("one", conversion.getResult());
    }

    @Test
    public void zero() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("0");
        assertEquals("zero", conversion.getResult());
    }

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
    public void twenty() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("20");
        assertEquals("twenty", conversion.getResult());
    }

    @Test
    public void twentyOne() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("21");
        assertEquals("twenty one", conversion.getResult());
    }

    @Test
    public void ten() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("10");
        assertEquals("ten", conversion.getResult());
    }

    @Test
    public void thirtyThree() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("33");
        assertEquals("thirty three", conversion.getResult());
    }

    @Test
    public void fourHundred() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("400");
        assertEquals("four hundred", conversion.getResult());
    }

    @Test
    public void fourHundredTwenty() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("420");
        assertEquals("four hundred twenty", conversion.getResult());
    }

    @Test
    public void oneThousand() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("1000");
        assertEquals("one thousand", conversion.getResult());
    }

    @Test
    public void twoThousand() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("2000");
        assertEquals("two thousand", conversion.getResult());
    }

    @Test
    public void threeThousand() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("3000");
        assertEquals("three thousand", conversion.getResult());
    }

    @Test
    public void fourThousand() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("4000");
        assertEquals("four thousand", conversion.getResult());
    }

    @Test
    public void fiveThousand() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("5000");
        assertEquals("five thousand", conversion.getResult());
    }

    @Test
    public void sixThousand() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("6000");
        assertEquals("six thousand", conversion.getResult());
    }

    @Test
    public void sevenThousand() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("7000");
        assertEquals("seven thousand", conversion.getResult());
    }

    @Test
    public void eightThousand() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("8000");
        assertEquals("eight thousand", conversion.getResult());
    }

    @Test
    public void nineThousand() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("9000");
        assertEquals("nine thousand", conversion.getResult());
    }

    @Test
    public void tenThousand() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("10000");
        assertEquals("ten thousand", conversion.getResult());
    }

    @Test
    public void fourHundredFour() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("404");
        assertEquals("four hundred four", conversion.getResult());
    }

    @Test
    public void bigNumber1() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("10000002");
        assertEquals("ten million two", conversion.getResult());
    }

    @Test
    public void bigNumber2() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("69420");
        assertEquals("sixty nine thousand four hundred twenty", conversion.getResult());
    }

    @Test
    public void bigNumber3() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("1000000000001");
        assertEquals("one trillion one", conversion.getResult());
    }

    @Test
    public void bigNumber4() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("123456765415789917208123");
        assertEquals("one hundred twenty three sextillion four hundred fifty six quintillion seven hundred sixty five quadrillion four hundred fifteen trillion seven hundred eighty nine billion nine hundred seventeen million two hundred eight thousand one hundred twenty three", conversion.getResult());
    }

    @Test
    public void bigNumber5() {
        NumberServiceEndpoint.ConvertedNumber conversion = n.getConversion("9728738668283111");
        assertEquals("nine quadrillion seven hundred twenty eight trillion seven hundred thirty eight billion six hundred sixty eight million two hundred eighty three thousand one hundred eleven", conversion.getResult());
    }
}