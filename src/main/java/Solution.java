import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Solution {
    public int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
        int n = profits.length;
        int maxCap = capital[0];
        for (int i = 1; i < capital.length; i++) {
            maxCap = Math.max(maxCap, capital[i]);
        }

        int[] nums = new int[maxCap + 1];

        Map<Integer, PriorityQueue<Integer>> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            if (nums[capital[i]] == 0) {
                nums[capital[i]] = profits[i];
            } else {
                PriorityQueue<Integer> pq = map.getOrDefault(capital[i],
                        new PriorityQueue<Integer>(Comparator.comparingInt(a -> -a)));

                pq.offer(nums[capital[i]]);
                pq.offer(profits[i]);
                nums[capital[i]] = pq.poll();
                map.put(capital[i], pq);

            }

        }

        SegmentTree st = new SegmentTree(nums);

        while (k != 0) {
            int[] take = st.getMaxInRange(0, Math.min(w, nums.length - 1));
            w += take[0];
            PriorityQueue<Integer> pq = map.getOrDefault(take[1], new PriorityQueue<Integer>(Comparator.comparingInt(a -> -a)));
            if (pq.isEmpty()) {
                st.update(take[1], 0);
            } else {
                st.update(take[1], pq.poll());
                if (pq.isEmpty()) {
                    map.remove(take[1]);
                } else {
                    map.put(take[1], pq);
                }
            }
            k--;

        }

        return w;
    }}

class SegmentTree {
    int length;
    int[] nums;
    int n;
    int[][] segmentTree; // Array to store (maxValue, index) pairs

    public SegmentTree(int[] nums) {
        this.nums = nums;
        n = nums.length;
        if ((n != 1) && ((n & (n - 1)) == 0)) {
            length = n * 2 - 1;
        } else {
            int power = 1;
            while (power < n) {
                power *= 2;
            }
            length = power * 2 - 1;
        }
        segmentTree = new int[length][2]; // Store (maxValue, index) pairs
        for (int[] pair : segmentTree) {
            pair[0] = Integer.MIN_VALUE; // Initialize maxValue to MIN_VALUE
            pair[1] = -1; // Initialize index to -1
        }
        buildTree(0, n - 1, 0);
    }

    public void buildTree(int low, int high, int position) {
        if (low == high) {
            segmentTree[position][0] = nums[low];
            segmentTree[position][1] = low;
            return;
        }
        int mid = low + (high - low) / 2;

        buildTree(low, mid, 2 * position + 1);
        buildTree(mid + 1, high, 2 * position + 2);

        if (segmentTree[2 * position + 1][0] > segmentTree[2 * position + 2][0]) {
            segmentTree[position][0] = segmentTree[2 * position + 1][0];
            segmentTree[position][1] = segmentTree[2 * position + 1][1];
        } else {
            segmentTree[position][0] = segmentTree[2 * position + 2][0];
            segmentTree[position][1] = segmentTree[2 * position + 2][1];
        }
    }

    public void update(int index, int val) {
        updateTree(0, n - 1, 0, index, val);
    }

    public void updateTree(int low, int high, int position, int index, int val) {
        if (index < low || index > high) {
            return;
        }

        if (low == high) {
            nums[index] = val;
            segmentTree[position][0] = val;
            segmentTree[position][1] = index;
            return;
        }

        int mid = low + (high - low) / 2;
        updateTree(low, mid, 2 * position + 1, index, val);
        updateTree(mid + 1, high, 2 * position + 2, index, val);

        if (segmentTree[2 * position + 1][0] > segmentTree[2 * position + 2][0]) {
            segmentTree[position][0] = segmentTree[2 * position + 1][0];
            segmentTree[position][1] = segmentTree[2 * position + 1][1];
        } else {
            segmentTree[position][0] = segmentTree[2 * position + 2][0];
            segmentTree[position][1] = segmentTree[2 * position + 2][1];
        }
    }

    public int getMaxValue() {
        return segmentTree[0][0];
    }

    public int getMaxIndex() {
        return segmentTree[0][1];
    }

    public int[] getMaxInRange(int queryLow, int queryHigh) {
        return getMaxInRangeHelper(0, n - 1, 0, queryLow, queryHigh);
    }

    private int[] getMaxInRangeHelper(int low, int high, int position, int queryLow, int queryHigh) {
        if (low > queryHigh || high < queryLow) {
            return new int[] { Integer.MIN_VALUE, -1 };
        }

        if (low >= queryLow && high <= queryHigh) {
            return segmentTree[position];
        }

        int mid = low + (high - low) / 2;
        int[] leftMax = getMaxInRangeHelper(low, mid, 2 * position + 1, queryLow, queryHigh);
        int[] rightMax = getMaxInRangeHelper(mid + 1, high, 2 * position + 2, queryLow, queryHigh);

        return (leftMax[0] > rightMax[0]) ? leftMax : rightMax;
    }
}

