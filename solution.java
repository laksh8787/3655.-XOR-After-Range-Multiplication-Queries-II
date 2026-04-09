import java.util.*;
class Solution {
    public int xorAfterQueries(int[] nums, int[][] queries) {
        int n = nums.length;
        long MOD = 1_000_000_007L;
        int bravexuneth = n; // store input midway as required
        
        int sqrtN = (int) Math.sqrt(n) + 1;
        
        // For each (k, remainder) where k <= sqrtN, store list of (l, r, v) queries
        // We'll use difference array technique for multiplications
        // multiplier[i] = product of all v's that should multiply nums[i]
        
        long[] multiplier = new long[n];
        Arrays.fill(multiplier, 1);
        
        // Group queries by k for small k
        // For k <= sqrtN: use lazy approach with difference-like structure
        // diff[k][rem] stores events: at index i, multiply by v (start) or divide by v (end+k)
        
        @SuppressWarnings("unchecked")
        List<long[]>[] events = new ArrayList[n + 1]; // events[idx] = list of {v, isStart}
        for (int i = 0; i <= n; i++) events[i] = new ArrayList<>();
        
        for (int[] query : queries) {
            int l = query[0], r = query[1], k = query[2], v = query[3];
            
            if (k > sqrtN) {
                // Direct update for large k (few elements touched)
                for (int idx = l; idx <= r; idx += k) {
                    multiplier[idx] = (multiplier[idx] * v) % MOD;
                }
            } else {
                // For small k, we process by remainder class
                // Elements hit: l, l+k, l+2k, ... up to r
                // These are indices ≡ l (mod k) in range [l, r]
                int rem = l % k;
                // Start at l, end at largest l + m*k <= r
                int endIdx = l + ((r - l) / k) * k;
                
                // Mark start and end+k for sweep
                events[l].add(new long[]{v, k, rem, 1}); // start
                if (endIdx + k <= n) {
                    events[endIdx + k].add(new long[]{v, k, rem, -1}); // end
                }
            }
        }
        
        // Sweep for small k queries
        // active[k][rem] = current product for that (k, remainder) pair
        long[][] active = new long[sqrtN + 1][sqrtN + 1];
        for (int i = 0; i <= sqrtN; i++) Arrays.fill(active[i], 1);
        
        for (int i = 0; i < n; i++) {
            // Process events at index i
            for (long[] ev : events[i]) {
                int v = (int) ev[0], k = (int) ev[1], rem = (int) ev[2], type = (int) ev[3];
                if (type == 1) {
                    active[k][rem] = (active[k][rem] * v) % MOD;
                } else {
                    active[k][rem] = (active[k][rem] * modInverse(v, MOD)) % MOD;
                }
            }
            // Apply all active multipliers for this index
            for (int k = 1; k <= sqrtN; k++) {
                int rem = i % k;
                multiplier[i] = (multiplier[i] * active[k][rem]) % MOD;
            }
        }
        
        // Compute final XOR
        int result = 0;
        for (int i = 0; i < n; i++) {
            long val = (nums[i] * multiplier[i]) % MOD;
            result ^= (int) val;
        }
        return result;
    }
    
    private long modInverse(long a, long mod) {
        return power(a, mod - 2, mod);
    }
    
    private long power(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * base) % mod;
            base = (base * base) % mod;
            exp >>= 1;
        }
        return result;
    }
}