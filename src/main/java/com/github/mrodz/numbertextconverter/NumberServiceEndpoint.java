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
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

/**
 * Class that acts as this program's API endpoint for its Number to Words service.
 */
@RestController
public class NumberServiceEndpoint {
    /**
     * Class to encapsulate a {@link BigInteger} and a {@link String},
     * to construct a proper JSON object for API endpoint GET requests.
     * @see #getConversion(String)
     */
    public static class ConvertedNumber {
        private final BigInteger number;
        private final String result;

        public ConvertedNumber(BigInteger number) {
            this.number = number;
            this.result = getStringRepresentationOfNumber(number);
        }

        public static final HashMap<Integer, String> decimalNumberToStringMapping = new HashMap<>();
        public static final TreeMap<BigInteger, String> powersToStringMapping = new TreeMap<>();

        /* Load YAML configurations */
        static {
            try {
                Yaml yaml = new Yaml();
                File yamlMapping = new File("number-mapping.yaml"); // fixme
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

        @SuppressWarnings("rawtypes")
        private static final Map.Entry[] arrayOfPowers = powersToStringMapping.entrySet().toArray(new Map.Entry[0]);

        /**
         * This method encapsulates this API's entire functionality. It does as its name
         * suggests; converts a {@link BigInteger} number to its value in words.
         * @param number a number whose value is to be turned into words.
         * @return a {@link String} of the words that make up a number.
         * @see #getStringRepresentationOfSmallNumber(ArrayList, BigInteger, int)
         * @see #powersToStringMapping
         * @see #decimalNumberToStringMapping
         */
        public static String getStringRepresentationOfNumber(BigInteger number) {
            if (number.compareTo(BigInteger.ZERO) == 0) return "zero"; // handle an edge case

            String negativeAddon = number.compareTo(BigInteger.ZERO) < 0 ? "negative " : ""; // to append later
            number = number.abs(); // see above

            if (number.toString().length() < 3) {
                // if there are less than three digits, no need to go through the steps that follow.
                return negativeAddon.concat(getStringRepresentationOfSmallNumber(number));
            }

            ArrayList<StringBuilder> segments = new ArrayList<>(); // used to store number 'segments' (groups of 3)
            StringBuilder actual = new StringBuilder(number.toString()); // StringBuilder of number
            StringBuilder buffer = new StringBuilder(); // mutable buffer

            // split the number into groups of three digits, picture the splice at where
            // commas (or periods, if not US) would go in a formatted number.
            actual.reverse();
            for (int i = 1; i <= actual.length(); i++) {
                buffer.append(actual.charAt(i - 1));
                if (i % 3 == 0 || i == actual.length()) {
                    segments.add(new StringBuilder(buffer));
                    buffer.delete(0, buffer.length());
                }
            }

            // undo the reversal
            segments.forEach(StringBuilder::reverse);
            Collections.reverse(segments);

            ArrayList<String> result = new ArrayList<>();

            for (int i = segments.size() + 1, c = 0; ; i--, c++) {
                if (!(c < segments.size() && i >= 0)) break; // make loop declaration smaller

                String forThisGrouping = getStringRepresentationOfSmallNumber(new BigInteger(segments.get(c).toString()));

                boolean isFinalSegment = arrayOfPowers[i].getValue()
                        .equals(powersToStringMapping.get(BigInteger.valueOf(100L)));

                // if this section is one whose value is < 1000, don't print the power of said number;
                // otherwise, do print its power.
                if (!forThisGrouping.isEmpty()) result.add(String.format("%s%s", forThisGrouping, isFinalSegment
                        ? "" : " " + arrayOfPowers[i].getValue()));
            }

            // add elements together
            String collected = result.stream()
                    .reduce("", (first, second) -> first.concat(" ").concat(second)).trim();

            return negativeAddon.concat(collected);
        }

        /**
         * Wrapper to be used in preference over this method's recursive counterpart.
         * @param number a {@link BigInteger} value.
         * @return the base {@link String} representation of a small number (< 1000).
         * @see #getStringRepresentationOfSmallNumber(ArrayList, BigInteger, int)
         */
        private static String getStringRepresentationOfSmallNumber(BigInteger number) {
            int steps = number.toString().length() - 1;
            return getStringRepresentationOfSmallNumber(new ArrayList<>(), number, steps);
        }

        /**
         * Get the {@link String} representation of a small number. A small number is
         * one whose value is less than 1000. Uses {@link #decimalNumberToStringMapping}
         * and {@link #powersToStringMapping} to map numerical integers to text.
         * <p>Usage:</p>
         * <pre>
         *     0 -> "zero"
         *     999 -> "nine hundred ninety nine"
         *     420 -> "four hundred twenty"
         *     17 -> "seventeen"
         * </pre>
         * @param result an {@link ArrayList}, a recursive parameter. Set to {@code new ArrayList<>()}
         * @param number the number to be converted.
         * @param steps set to the amount of digits in the number.
         * @return a {@link String} representation of a small number.
         */
        private static String getStringRepresentationOfSmallNumber(ArrayList<String> result, final BigInteger number, int steps) {
            // assertion
            if (number.compareTo(BigInteger.valueOf(999)) > 0 || number.compareTo(BigInteger.ZERO) < 0) {
                throw new IllegalArgumentException("number " + number + " is out of bounds (0-999)");
            }

            if (steps < 0) { // the end case (break)
                StringBuilder fin = new StringBuilder();
                result.forEach(s -> fin.append(s).append(' '));
                return handleEdgeCases(fin.substring(0, fin.length() - 1));
            }

            BigInteger power = BigInteger.TEN.pow(steps); // the power of this step (1, 10, or 100).
            int reduced = getNumberAtDecimalPlace(number, power); // the digit at this frame's 'step'

            // should never appear; useful for debugging
            Function<Object, String> errorString = (obj) -> String.format("\"[ERROR] %s\"", obj);

            // set corresponding String values
            String decimalString = decimalNumberToStringMapping.getOrDefault(reduced, errorString.apply(reduced));
            String powerString = powersToStringMapping.getOrDefault(power, errorString.apply(power));

            // handle special cases ("one" .. "ten" & "ten" .. "ninety")
            int key = power.multiply(BigInteger.valueOf(reduced)).intValue();
            if (decimalNumberToStringMapping.containsKey(key)) {
                result.add(decimalNumberToStringMapping.get(key));
            }

            // if the result is valid, append it to result
            if (powerString.length() > 0 && reduced != 0) {
                result.add(decimalString);
                result.add(powerString);
            }

            // recursive call, going to the next smallest power (100 -> 10, 10 -> 1)
            return getStringRepresentationOfSmallNumber(result, number, steps - 1);
        }

        /**
         * Remove: unnecessary "zero" instances in the number, excess spaces.
         * Also handles numbers 11-19.
         * @param number a formatted number.
         * @return a {@link String} will all unnecessary "zero" instances and
         * excess spaces removed, alongside properly formatting numbers 11-19.
         */
        private static String handleEdgeCases(String number) {
            return number
                    .replaceAll("(\\s*zero)++", "")
                    .replaceAll("^\\s++|\\s++$", "")
                    .replaceAll("(ten one)", "eleven")
                    .replaceAll("(ten two)", "twelve")
                    .replaceAll("(ten three)", "thirteen")
                    .replaceAll("(ten four)", "fourteen")
                    .replaceAll("(ten five)", "fifteen")
                    .replaceAll("(ten six)", "sixteen")
                    .replaceAll("(ten seven)", "seventeen")
                    .replaceAll("(ten eight)", "eighteen")
                    .replaceAll("(ten nine)", "nineteen");
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
         * @deprecated - there is bound to be a more efficient way to do this.
         */
        @Deprecated
        public static int getNumberAtDecimalPlace(final BigInteger number, BigInteger target) {
            if (String.valueOf(target).length() > String.valueOf(number).length()) {
                throw new IllegalArgumentException("Place value is too large");
            }
            return number.
            return ((number.subtract((number.subtract(number.mod(target.multiply(BigInteger.TEN)))))
                    .subtract(number.mod(target))).divide(target)).intValue();
        }

        /**
         * Required for {@link #getConversion(String)} as per
         * {@link RestController} standards.
         * @return this instance's {@link #number}
         */
        @SuppressWarnings("unused")
        public BigInteger getNumber() {
            return number;
        }

        /**
         * Required for {@link #getConversion(String)} as per
         * {@link RestController} standards.
         * @return this instance's {@link #result} if not null. Else, {@code "error"}
         */
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

    /**
     * The RESTful API's <tt>GET</tt> endpoint.
     * @param number a {@link String} path variable, the target for this operation.
     * @return a number's word value as a {@link String}.
     * @see ConvertedNumber#getStringRepresentationOfNumber(BigInteger)
     * @see RestController
     */
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
                    return new StackTraceElement[]{e.getStackTrace()[0]};
                }
            }, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Exception(e.getMessage()) {
                @Override
                public StackTraceElement[] getStackTrace() {
                    return new StackTraceElement[]{e.getStackTrace()[0]};
                }
            }, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
