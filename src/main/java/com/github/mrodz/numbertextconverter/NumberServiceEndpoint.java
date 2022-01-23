package com.github.mrodz.numbertextconverter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
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

        public static final HashMap<Integer, String> decimalNumberToStringMapping = new HashMap<>();

        public static final TreeMap<BigInteger, String> powersToStringMapping = new TreeMap<>();

        static {
            try {
                Yaml yaml = new Yaml();
                File yamlMapping = new File("number-mapping.yaml");
                InputStream inputStream = new FileInputStream(yamlMapping);
                Map<String, Object> obj = yaml.load(inputStream);

                powersToStringMapping.put(BigInteger.ONE, "");
                powersToStringMapping.put(BigInteger.TEN, "");

                @SuppressWarnings("unchecked")
                List<Object> powerMappings = (List<Object>) obj.get("number-text-power-mappings");

                for (Object o : powerMappings) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> keyValuePair = (Map<String, Object>) o;

                    Integer pow = (Integer) keyValuePair.get("pow");
                    String str = (String) keyValuePair.get("str");

                    powersToStringMapping.put(BigInteger.TEN.pow(pow), str);
                }

                @SuppressWarnings("unchecked")
                List<Object> specialMappings = (List<Object>) obj.get("number-text-special-mappings");

                for (Object o : specialMappings) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> keyValuePair = (Map<String, Object>) o;

                    Integer key = (Integer) keyValuePair.get("key");
                    String str = (String) keyValuePair.get("str");

                    decimalNumberToStringMapping.put(key, str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

            ArrayList<String> result = new ArrayList<>();

            Map.Entry[] arrayOfPowers = powersToStringMapping.entrySet().toArray(new Map.Entry[0]);

            for (int i = segments.size() + 1, c = 0; ; i--, c++) {
                if (!(c < segments.size() && i >= 0)) break; // make loop declaration smaller

                String forThisGrouping = getStringRepresentationOfSmallNumber(new BigInteger(segments.get(c).toString()));

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

        @SuppressWarnings("unused")
        public BigInteger getNumber() {
            return number;
        }

        @SuppressWarnings("unused")
        public String getResult() {
            return result == null ? "error" : result;
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
    public ResponseEntity<?> getConversion(@PathVariable String number) {
        BigInteger bigInteger;
        try {
            bigInteger = new BigInteger(number);
            ConvertedNumber result = new ConvertedNumber(bigInteger);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(new NumberFormatException(e.getMessage()) {
                @Override
                public StackTraceElement[] getStackTrace() {
                    return new StackTraceElement[] { e.getStackTrace()[0] };
                }
            }, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Exception(e.getMessage()) {
                @Override
                public StackTraceElement[] getStackTrace() {
                    return new StackTraceElement[] { e.getStackTrace()[0] };
                }
            }, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
