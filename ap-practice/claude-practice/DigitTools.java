public class DigitTools {
  /**
   * Returns the number of digit characters in s. Examples: countDigits("a1b22c")
   * → 3 countDigits("abc") → 0 countDigits("") → 0 Precondition: s is not null.
   */
  public static int countDigits(String s) {
    // tracks how many digits we encounter in the string
    int count = 0;

    // iterate through s
    for (int i = 0; i < s.length(); i++) {
      // check if the current char is a digit or not
      if (Character.isDigit(s.charAt(i))) {
        count++;
      }
    }

    // return how many digits we encountered
    return count;
  }

  /**
   * Returns true if more than half the characters in s are digits. For an empty
   * string, returns false. MUST call countDigits. Examples: isMostlyDigits("a12")
   * → false (1 digit out of 3 = not more than half? wait, 2 digits out of 3)
   * isMostlyDigits("a12") → true (2/3 > 1/2) isMostlyDigits("ab12") → false (2/4
   * = 1/2, not strictly more) isMostlyDigits("abc1") → false (1/4)
   * isMostlyDigits("12345") → true (5/5) isMostlyDigits("") → false Precondition:
   * s is not null.
   */
  public static boolean isMostlyDigits(String s) {
    // number of digits there are in the string
    double digits = (double) countDigits(s);

    // number of characters there are in the string
    double characters = s.length() - digits;

    // ratio of the two
    double ratio = digits / characters;

    return ratio > 0.5;
  }
}