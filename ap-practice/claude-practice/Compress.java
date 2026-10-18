public class Compress {
  /** Returns a string where consecutive duplicate characters
   *  are replaced by a single occurrence of that character.
   *  Examples:
   *    dedupe("aabbbcdd")  → "abcd"
   *    dedupe("aaaa")      → "a"
   *    dedupe("abcd")      → "abcd"  (no duplicates to remove)
   *    dedupe("a")         → "a"
   *    dedupe("")          → ""
   *  Precondition: s is not null.
   */
  public static String dedupe(String s) {
    // add first letter as the starter to result
    if (s.length() == 0) {
      return "";
    }
    String result = s.substring(0, 1);
    
    // iterate through the string
    for (int i = 1; i < s.length(); i++) {
      // if the previous letter in result does NOT equal the current letter we are iterating at, add that letter to char, else just do nothing
      if (result.charAt(result.length() - 1) != s.charAt(i)) {
        result = result + s.charAt(i);
      }
    }
    // return result at the end
    return result;
  }
}
