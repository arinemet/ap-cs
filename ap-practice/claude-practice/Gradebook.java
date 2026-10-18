import java.util.ArrayList;

public class Gradebook {
    private ArrayList<Integer> scores; // each score 0..100

    /**
     * Returns the average of all scores. Precondition: scores.size() > 0.
     */
    public double average() {
        double total = 0;
        for (int i = 0; i < scores.size(); i++) {
            total += scores.get(i);
        }

        return total / scores.size();
    }

    /**
     * Removes all scores strictly below the given cutoff. Returns the number of
     * scores removed. Examples: scores = [72, 55, 88, 40, 90], cutoff = 60 ->
     * after: [72, 88, 90]; returns 2
     *
     * scores = [50, 50, 50], cutoff = 60 -> after: []; returns 3
     *
     * scores = [80, 90, 100], cutoff = 60 -> after: [80, 90, 100]; returns 0
     */
    public int dropBelow(int cutoff) {
        int removed = 0;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) < cutoff) {
                scores.remove(i);
                removed++;
                i--;
            }
        }

        return removed;
    }
}