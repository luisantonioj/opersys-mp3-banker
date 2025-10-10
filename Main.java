import java.util.*;

public class Main {
    static int n; // number of processes
    static int totalResources; // total instances of resource

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            // Input number of processes
            n = inputPositiveInt(sc, "Enter number of processes: ");

            int[] maxNeed = new int[n];
            int[] allocated = new int[n];
            int[] need = new int[n];

            // Input max and allocated for each process
            for (int i = 0; i < n; i++) {
                while (true) {
                    maxNeed[i] = inputPositiveInt(sc, "  P" + (i + 1) + " - Maximum Needed: ");
                    allocated[i] = inputPositiveInt(sc, "  P" + (i + 1) + " - Currently Holding: ");

                    if (allocated[i] > maxNeed[i]) {
                        System.out.println("    ❌ Error: Currently Holding cannot exceed Maximum Needed. Try again.");
                    } else {
                        need[i] = maxNeed[i] - allocated[i];
                        break;
                    }
                }
            }

            // Input total resources with validation
            while (true) {
                totalResources = inputPositiveInt(sc, "Enter total number of resources in the system: ");
                int sumAlloc = 0;
                for (int x : allocated) sumAlloc += x;

                if (totalResources < sumAlloc) {
                    System.out.println("    ❌ Error: Total resources must be >= sum of allocations (" + sumAlloc + ")");
                } else {
                    int available = totalResources - sumAlloc;
                    System.out.println("\nInitial Available: " + available);

                    // Generate all permutations of process sequences
                    List<int[]> sequences = new ArrayList<>();
                    permute(generateProcesses(), 0, sequences);

                    // Check each sequence
                    for (int[] seq : sequences) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < seq.length; i++) {
                            sb.append("P").append(seq[i]);
                            if (i < seq.length - 1) sb.append(" → ");
                        }

                        if (isSafe(seq, need, allocated, available)) {
                            System.out.println("✅ Safe Sequence:   " + sb.toString());
                        } else {
                            System.out.println("❌ Unsafe Sequence: " + sb.toString());
                        }
                    }
                    break;
                }
            }

            // Ask to repeat
            System.out.print("\nDo you want to try again? (Y/N): ");
            String choice = sc.next().trim().toUpperCase();
            if (!choice.equals("Y")) {
                System.out.println("Exiting program. Goodbye!");
                break;
            }
        }
        sc.close();
    }

    // Input helper with validation
    private static int inputPositiveInt(Scanner sc, String msg) {
        int val;
        while (true) {
            System.out.print(msg);
            if (sc.hasNextInt()) {
                val = sc.nextInt();
                if (val >= 0) return val;
                else System.out.println("    ❌ Error: Value must be non-negative.");
            } else {
                System.out.println("    ❌ Error: Enter a valid integer.");
                sc.next(); // clear invalid input
            }
        }
    }

    // Generate list of processes [1..n]
    private static int[] generateProcesses() {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i + 1;
        return arr;
    }

    // Permutations using backtracking
    private static void permute(int[] arr, int l, List<int[]> results) {
        if (l == arr.length - 1) {
            results.add(arr.clone());
            return;
        }
        for (int i = l; i < arr.length; i++) {
            swap(arr, l, i);
            permute(arr, l + 1, results);
            swap(arr, l, i);
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    // Check if a given sequence is safe
    private static boolean isSafe(int[] seq, int[] need, int[] alloc, int available) {
        int work = available;
        boolean[] finished = new boolean[n];

        for (int idx : seq) {
            int p = idx - 1; // process index
            if (!finished[p] && need[p] <= work) {
                work += alloc[p];
                finished[p] = true;
            } else {
                return false; // sequence fails
            }
        }
        return true;
    }
}
