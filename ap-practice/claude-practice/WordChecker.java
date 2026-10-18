public class WordChecker {
  /**
   * Returns true if word contains at least one vowel (a, e, i, o, u — lowercase
   * only) AND at least one non-vowel character. Examples: hasMix("hello") → true
   * (has 'e','o' AND 'h','l','l') hasMix("aeiou") → false (all vowels)
   * hasMix("xyz") → false (no vowels) hasMix("a1") → true ('a' is vowel, '1' is
   * non-vowel) hasMix("") → false (empty) Precondition: word is not null.
   */
  public static boolean hasMix(String word) {
    // for the return of the function, if it has a mix or not
    boolean hasVowel = false;
    boolean hasConsonant = false;
    // iterate through the word
    for (int i = 0; i < word.length(); i++) {
      // if it is a vowel
      if ("aeiou".indexOf(word.charAt(i)) != -1) {
        hasVowel = true;
        // if it isn't a vowel
      } else {
        hasConsonant = true;
      }
    }
    // only return true if there is a vowel AND it has a consonant
    return hasVowel && hasConsonant;

  }
}
