package com.github.mrodz.numbertextconverter;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.mrodz.numbertextconverter.NumberServiceEndpoint.*;

class NumberServiceEndpointTest {
    NumberServiceEndpoint n = new NumberServiceEndpoint();

    @Test
    public void crash1() {
        assertEquals(HttpStatus.BAD_REQUEST, n.getConversion("1.5").getStatusCode());
    }

    @Test
    public void crash2() {
        assertEquals(HttpStatus.BAD_REQUEST, n.getConversion("This is Not a Number").getStatusCode());
    }

    @Test
    public void allZeroes() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("000000000").getBody());
        assertEquals("zero", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void leadingZeroes() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("00000000005550").getBody());
        assertEquals("five thousand five hundred fifty", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void negativeWithLeadingZeroes() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("-0000000000123").getBody());
        assertEquals("negative one hundred twenty three", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void negativeSevenHundredSeventySeven() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("-777").getBody());
        assertEquals("negative seven hundred seventy seven", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void negativeTenTrillion() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("-10000000000000").getBody());
        assertEquals("negative ten trillion", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void one() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("1").getBody());
        assertEquals("one", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void zero() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("0").getBody());
        assertEquals("zero", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void five() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("5").getBody());
        assertEquals("five", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void seventeen() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("17").getBody());
        assertEquals("seventeen", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void twenty() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("20").getBody());
        assertEquals("twenty", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void twentyOne() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("21").getBody());
        assertEquals("twenty one", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void ten() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("10").getBody());
        assertEquals("ten", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void thirtyThree() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("33").getBody());
        assertEquals("thirty three", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void fourHundred() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("400").getBody());
        assertEquals("four hundred", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void fourHundredTwenty() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("420").getBody());
        assertEquals("four hundred twenty", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void oneThousand() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("1000").getBody());
        assertEquals("one thousand", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void twoThousand() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("2000").getBody());
        assertEquals("two thousand", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void threeThousand() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("3000").getBody());
        assertEquals("three thousand", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void fourThousand() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("4000").getBody());
        assertEquals("four thousand", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void fiveThousand() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("5000").getBody());
        assertEquals("five thousand", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void sixThousand() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("6000").getBody());
        assertEquals("six thousand", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void sevenThousand() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("7000").getBody());
        assertEquals("seven thousand", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void eightThousand() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("8000").getBody());
        assertEquals("eight thousand", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void nineThousand() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("9000").getBody());
        assertEquals("nine thousand", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void tenThousand() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("10000").getBody());
        assertEquals("ten thousand", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void fourHundredFour() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("404").getBody());
        assertEquals("four hundred four", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void bigNumber1() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("10000002").getBody());
        assertEquals("ten million two", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void bigNumber2() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("69420").getBody());
        assertEquals("sixty nine thousand four hundred twenty", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void bigNumber3() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("1000000000001").getBody());
        assertEquals("one trillion one", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void bigNumber4() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("123456765415789917208123").getBody());
        assertEquals("one hundred twenty three sextillion four hundred fifty six quintillion seven hundred sixty five quadrillion four hundred fifteen trillion seven hundred eighty nine billion nine hundred seventeen million two hundred eight thousand one hundred twenty three", Objects.requireNonNull(conversion).getResult());
    }

    @Test
    public void bigNumber5() {
        ConvertedNumber conversion = ((ConvertedNumber) n.getConversion("9728738668283111").getBody());
        assertEquals("nine quadrillion seven hundred twenty eight trillion seven hundred thirty eight billion six hundred sixty eight million two hundred eighty three thousand one hundred eleven", Objects.requireNonNull(conversion).getResult());
    }
}