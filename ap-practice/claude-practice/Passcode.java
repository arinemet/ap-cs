public class Passcode {
    /** Returns true if pc contains only digit characters AND has
     *  no two adjacent equal digits.
     *  Examples:
     *    "1213" -> true
     *    "1123" -> false (two 1s adjacent)
     *    "12a4" -> false (contains non-digit)
     *    "5"    -> true
     *  Precondition: pc.length() >= 1.
     */
    public static boolean isValid(String pc) {
      char previousNumber = pc.charAt(0);
      
      if (Character.isDigit(pc.charAt(0))) return false;

      for (int i = 1; i < pc.length(); i++) {
          if (Character.isDigit(pc.charAt(i))) {
            return false;
          }
          if (String.valueOf(pc.charAt(i)).equals(String.valueOf(previousNumber))) {
            return false;
          }
          previousNumber = pc.charAt(i);

        }

        return true;
    }

    /** Returns the count of valid passcodes in the array.
     *  Must use isValid in the implementation.
     */
    public static int countValid(String[] codes) {
      int count = 0;
      for (String code : codes) {
        if (isValid(code)) {
          count++;
        }
      }
      return count;
    }
}
