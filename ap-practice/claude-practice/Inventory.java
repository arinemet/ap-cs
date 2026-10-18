public class Inventory {
    /** Returns the sum of all positive integers in arr.
     *  Negative numbers and zeros are ignored.
     *  Examples:
     *    sumPositives({3, -1, 5, 0, 2})  → 10
     *    sumPositives({-1, -2, -3})      → 0
     *    sumPositives({})                → 0
     */
    public static int sumPositives(int[] arr) {
      int ret = 0;

      for (int n : arr) {
        if (n > 0) {
          ret += n;
        }
      }
      return ret;
    }
}
