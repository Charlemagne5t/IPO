import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Solution {
    public int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
        int n = profits.length;
        int[][] pc = new int[n][2];

        for (int i = 0; i < n; i++) {
            pc[i][0] = profits[i];
            pc[i][1] = capital[i];
        }

        Arrays.sort(pc, Comparator.comparingInt((int[] a) -> a[1]));
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.reverseOrder());
        int i = 0;
        while (i <= n) {
            while (i != n && pc[i][1] <= w) {
                pq.offer(pc[i][0]);
                i++;
            }
            if(!pq.isEmpty()) {
                w += pq.poll();
                k--;
                if(k == 0) {
                    break;
                }
            }else break;
        }


        return w;
    }
}