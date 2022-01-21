package com.github.mrodz.numbertextconverter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

@RestController
public class NumberServiceEndpoint {
    public static class ConvertedNumber {
        private final BigInteger number;
        private final String result;

        public ConvertedNumber(BigInteger number) {
            this.number = number;
            //            System.out.println("\t\t$\t" + temp);
            this.result = getStringRepresentation(number);
        }

        HashMap<Integer, String> decimalNumberToStringMapping = new HashMap<>();

        {
            decimalNumberToStringMapping.put(0, "zero");
            decimalNumberToStringMapping.put(1, "one");
            decimalNumberToStringMapping.put(2, "two");
            decimalNumberToStringMapping.put(3, "three");
            decimalNumberToStringMapping.put(4, "four");
            decimalNumberToStringMapping.put(5, "five");
            decimalNumberToStringMapping.put(6, "six");
            decimalNumberToStringMapping.put(7, "seven");
            decimalNumberToStringMapping.put(8, "eight");
            decimalNumberToStringMapping.put(9, "nine");
            decimalNumberToStringMapping.put(10, "ten");
            decimalNumberToStringMapping.put(11, "eleven");
            decimalNumberToStringMapping.put(12, "twelve");
            decimalNumberToStringMapping.put(13, "thirteen");
            decimalNumberToStringMapping.put(14, "fourteen");
            decimalNumberToStringMapping.put(15, "fifteen");
            decimalNumberToStringMapping.put(16, "sixteen");
            decimalNumberToStringMapping.put(17, "seventeen");
            decimalNumberToStringMapping.put(18, "eighteen");
            decimalNumberToStringMapping.put(19, "nineteen");
            decimalNumberToStringMapping.put(20, "twenty");
            decimalNumberToStringMapping.put(30, "thirty");
            decimalNumberToStringMapping.put(40, "forty");
            decimalNumberToStringMapping.put(50, "fifty");
            decimalNumberToStringMapping.put(60, "sixty");
            decimalNumberToStringMapping.put(70, "seventy");
            decimalNumberToStringMapping.put(80, "eighty");
            decimalNumberToStringMapping.put(90, "ninety");
        }

        HashMap<BigInteger, String> powersToStringMapping = new HashMap<>();

        {
            powersToStringMapping.put(BigInteger.ONE, "");
            powersToStringMapping.put(BigInteger.TEN, "teen");
            powersToStringMapping.put(BigInteger.valueOf(100), "hundred");
            powersToStringMapping.put(BigInteger.valueOf(1_000), "thousand");
//            powersToStringMapping.put(BigInteger.valueOf(100_000), "hundred-thousand");
            powersToStringMapping.put(BigInteger.valueOf(1_000_000), "million");
//            powersToStringMapping.put(BigInteger.valueOf(100_000_000), "hundred-million");
            powersToStringMapping.put(BigInteger.valueOf(1_000_000_000), "billion");
//            powersToStringMapping.put(new BigInteger("100000000000"), "hundred-billion");
            powersToStringMapping.put(new BigInteger("1000000000000"), "trillion");

        }

        private String getStringRepresentation(BigInteger number) {
            int steps = number.toString().length() - 1;
            return getStringRepresentation(new ArrayList<>(), number, steps, steps);
        }

        private String getStringRepresentation(ArrayList<String> result, BigInteger number, int steps, final int totalSteps) {
            System.out.println("result = " + result + ", number = " + number + ", steps = " + steps);
            BigInteger power = BigInteger.TEN.pow(steps /*- (steps % 3)*/);
            System.out.println("power = " + power);
            BigInteger reduced = number./*mod*/divide(power);
            System.out.println("reduced = " + reduced);

            if (reduced.compareTo(BigInteger.ZERO) >= 0 && steps > 0) {

                Function<Object, String> errorString = (obj) -> String.format("\"[ERROR] %s\"", obj);

                String decimalString = decimalNumberToStringMapping.getOrDefault(reduced.intValue(), errorString.apply(reduced.intValue()));
                String powerString = powersToStringMapping.getOrDefault(power, errorString.apply(power));

//                if (reduced.intValue() != 0) {
                    result.add(decimalString);
                    result.add(powerString);
//                }

                return getStringRepresentation(result, number, steps - 1, totalSteps);
            } else {
                StringBuilder fin = new StringBuilder();
                result.forEach(s -> fin.append(s).append(' '));
                return fin.substring(0, fin.length() - 1);
            }
        }

        public BigInteger getNumber() {
            return number;
        }

        public String getResult() {
            return result;
        }

        @Override
        public String toString() {
            return "ConvertedNumber{" +
                    "number=" + number +
                    ", result='" + result + '\'' +
                    '}';
        }
    }

    @GetMapping("/number-text/{number}")
    public ConvertedNumber getConversion(@PathVariable String number) {
        BigInteger bigInteger = new BigInteger(number);
        return new ConvertedNumber(bigInteger);
    }
}
