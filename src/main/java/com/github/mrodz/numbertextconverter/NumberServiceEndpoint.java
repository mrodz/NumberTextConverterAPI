package com.github.mrodz.numbertextconverter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

@RestController
public class NumberServiceEndpoint {
    public static class ConvertedNumber {
        private final BigInteger number;
        private final String result;

        public ConvertedNumber(BigInteger number) {
            this.number = number;
            this.result = getStringRepresentationOfNumber(number);
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

        TreeMap<BigInteger, String> powersToStringMapping = new TreeMap<>();

        {
            powersToStringMapping.put(BigInteger.ONE, "");
            powersToStringMapping.put(BigInteger.TEN, "");
            powersToStringMapping.put(BigInteger.TEN.pow(2), "hundred");
            powersToStringMapping.put(BigInteger.TEN.pow(3), "thousand");
            powersToStringMapping.put(BigInteger.TEN.pow(6), "million");
            powersToStringMapping.put(BigInteger.TEN.pow(9), "billion");
            powersToStringMapping.put(BigInteger.TEN.pow(12), "trillion");
            powersToStringMapping.put(BigInteger.TEN.pow(15), "quadrillion");
            powersToStringMapping.put(BigInteger.TEN.pow(18), "quintillion");
            powersToStringMapping.put(BigInteger.TEN.pow(21), "sextillion");
            powersToStringMapping.put(BigInteger.TEN.pow(24), "septillion");
            powersToStringMapping.put(BigInteger.TEN.pow(27), "octillion");
            powersToStringMapping.put(BigInteger.TEN.pow(30), "nonillion");
            powersToStringMapping.put(BigInteger.TEN.pow(33), "decillion");
            powersToStringMapping.put(BigInteger.TEN.pow(36), "undecillion");
            powersToStringMapping.put(BigInteger.TEN.pow(39), "duodecillion");
            powersToStringMapping.put(BigInteger.TEN.pow(42), "tredecillion");
            powersToStringMapping.put(BigInteger.TEN.pow(45), "quattuordecillion");
            powersToStringMapping.put(BigInteger.TEN.pow(48), "quindecillion");
            powersToStringMapping.put(BigInteger.TEN.pow(51), "sexdecillion");
            powersToStringMapping.put(BigInteger.TEN.pow(54), "septendecillion");
            powersToStringMapping.put(BigInteger.TEN.pow(57), "octodecillion");
            powersToStringMapping.put(BigInteger.TEN.pow(60), "novemdecillion");
            powersToStringMapping.put(BigInteger.TEN.pow(63), "vigintillion");
            powersToStringMapping.put(BigInteger.TEN.pow(100), "googol");
            powersToStringMapping.put(BigInteger.TEN.pow(303), "centillion");
        }

        private String getStringRepresentationOfSmallNumber(BigInteger number) {
            int steps = number.toString().length() - 1;
            return this.getStringRepresentationOfSmallNumber(new ArrayList<>(), number, steps);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private String getStringRepresentationOfNumber(BigInteger number) {
            if (number.compareTo(BigInteger.ZERO) == 0) return "zero";

            String negativeAddon = number.compareTo(BigInteger.ZERO) < 0 ? "negative " : "";

            number = number.abs();

            if (number.toString().length() < 3) return negativeAddon.concat(getStringRepresentationOfSmallNumber(number));

            ArrayList<StringBuilder> segments = new ArrayList<>();
            StringBuilder actual = new StringBuilder(number.toString());
            StringBuilder buffer = new StringBuilder();

            actual.reverse();
            for (int i = 1; i <= actual.length(); i++) {
                buffer.append(actual.charAt(i - 1));
                if (i % 3 == 0 || i == actual.length()) {
                    segments.add(new StringBuilder(buffer));
                    buffer.delete(0, buffer.length());
                }
            }

            segments.forEach(StringBuilder::reverse);

            segments = new ArrayList(Arrays.asList(reverseArray(segments.toArray())));

            System.out.println("segments = " + segments);

            ArrayList<String> result = new ArrayList<>();

            Map.Entry[] arrayOfPowers = powersToStringMapping.entrySet().toArray(new Map.Entry[0]);

            for (int i = segments.size() + 1, c = 0; ; i--, c++) {
                if (!(c < segments.size() && i >= 0)) break; // make loop declaration smaller

                String forThisGrouping = getStringRepresentationOfSmallNumber(new BigInteger(segments.get(c).toString()));

                System.out.println("forThisGrouping = " + forThisGrouping);
                System.out.println("arrayOfPowers = " + arrayOfPowers[i]);

                if (!forThisGrouping.isEmpty()) {
                    result.add(String.format("%s%s", forThisGrouping, arrayOfPowers[i].getValue()
                            .equals(powersToStringMapping.get(BigInteger.valueOf(100)))
                            ? "" : " " + arrayOfPowers[i].getValue()));
                }
            }

            return negativeAddon.concat(result.stream().reduce("", (first, second) -> first.concat(" ").concat(second)).trim());
        }

        private static <T> T[] reverseArray(T[] arr) {
            @SuppressWarnings("unchecked")
            T[] newArr = (T[]) Array.newInstance(arr.getClass().getComponentType(), arr.length);
            for (int i = 0, length = arr.length; i < length; i++) {
                newArr[i] = arr[length - 1 - i];
            }
            return newArr;
        }

        private String getStringRepresentationOfSmallNumber(ArrayList<String> result, BigInteger number, int steps) {
            if (steps < 0) {
                StringBuilder fin = new StringBuilder();
                result.forEach(s -> fin.append(s).append(' '));
                return handleEdgeCases(fin.substring(0, fin.length() - 1));
            }

            BigInteger power = BigInteger.TEN.pow(steps/*- (steps % 3)*/);
            int reduced = getNumberAtDecimalPlace(number, power);

            Function<Object, String> errorString = (obj) -> String.format("\"[ERROR] %s\"", obj);

            String decimalString = decimalNumberToStringMapping.getOrDefault(reduced, errorString.apply(reduced));
            String powerString = powersToStringMapping.getOrDefault(power, errorString.apply(power));

            int key = power.multiply(BigInteger.valueOf(reduced)).intValue();
            if (decimalNumberToStringMapping.containsKey(key)) {
                result.add(decimalNumberToStringMapping.get(key));
            }
            if (powerString.length() > 0 && reduced != 0) {
                result.add(decimalString);
                result.add(powerString);
            }

            return this.getStringRepresentationOfSmallNumber(result, number, steps - 1);
        }

        public String handleEdgeCases(String number) {
            number = number.replaceAll("(\\s*zero)++", "").replaceAll("^\\s++|\\s++$", "");

            number = number.replaceAll("(ten one)", "eleven");
            number = number.replaceAll("(ten two)", "twelve");
            number = number.replaceAll("(ten three)", "thirteen");
            number = number.replaceAll("(ten four)", "fourteen");
            number = number.replaceAll("(ten five)", "fifteen");
            number = number.replaceAll("(ten six)", "sixteen");
            number = number.replaceAll("(ten seven)", "seventeen");
            number = number.replaceAll("(ten eight)", "eighteen");
            number = number.replaceAll("(ten nine)", "nineteen");

            return number;
        }

        /**
         * Get an integer value at a number's specific power.
         * <pre>
         *     (1350, 100) -> 3
         *     (543210, 10000) -> 4
         * </pre>
         * Follows this equation: <tt>((x-((x-(x%(t*10))))-(x%t))/t)</tt> where
         * <tt>x</tt> is any number and <tt>t</tt> is the target decimal place.
         *
         * @param number a number
         * @param target a multiple of 10. (1, 10, 100, 1000, 10000, etc.)
         * @return the digit at a specified number's place value.
         * @see #getStringRepresentationOfSmallNumber(BigInteger)
         */
        public static int getNumberAtDecimalPlace(final BigInteger number, BigInteger target) {
            if (String.valueOf(target).length() > String.valueOf(number).length()) {
                throw new IllegalArgumentException("Place value is too large");
            }
            return ((number.subtract((number.subtract(number.mod(target.multiply(BigInteger.TEN)))))
                    .subtract(number.mod(target))).divide(target)).intValue();
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
