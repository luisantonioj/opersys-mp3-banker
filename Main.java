import java.util.*;

public class Main {
    static int n; // number of processes
    static int totalResources; // total instances of resource
    static String[] processIds; // Custom process names

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            // Input number of processes
            n = inputPositiveInt(sc, "Enter number of processes: ");

            processIds = new String[n];
            int[] maxNeed = new int[n];
            int[] allocated = new int[n];
            int[] need = new int[n];

            // Input custom process IDs with validation
            for (int i = 0; i < n; i++) {
                while (true) {
                    System.out.print("Enter process ID for Process" + (i + 1) + ": ");
                    processIds[i] = sc.next().trim();

                    if (processIds[i].isEmpty()) {
                        System.out.println("    Error: Process ID cannot be empty.");
                        continue;
                    }

                    // Check if already used
                    boolean duplicate = false;
                    for (int j = 0; j < i; j++) {
                        if (processIds[j].equals(processIds[i])) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (duplicate) {
                        System.out.println("    Error: Process ID '" + processIds[i] + "' is already used.");
                        continue;
                    }

                    break;
                }
            }

            // Input max and allocated for each process
            for (int i = 0; i < n; i++) {
                while (true) {
                    maxNeed[i] = inputPositiveInt(sc, "  " + processIds[i] + " - Maximum Needed: ");
                    allocated[i] = inputPositiveInt(sc, "  " + processIds[i] + " - Currently Holding: ");

                    if (allocated[i] > maxNeed[i]) {
                        System.out.println("    Error: Currently Holding cannot exceed Maximum Needed. Try again.");
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
                    System.out.println("    Error: Total resources must be >= sum of allocations (" + sumAlloc + ")");
                } else {
                    int available = totalResources - sumAlloc;
                    System.out.println("\nInitial Available: " + available);

                    // Print Resource Allocation Table
                    printAllocationTable(maxNeed, allocated, need, available);

                    // Generate all permutations of process indices [0..n-1]
                    List<int[]> sequences = new ArrayList<>();
                    permute(generateIndices(), 0, sequences);

                    boolean safeExists = false;

                    // Check each sequence
                    for (int[] seq : sequences) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < seq.length; i++) {
                            sb.append(processIds[seq[i]]);
                            if (i < seq.length - 1) sb.append(" → ");
                        }

                        if (isSafe(seq, need, allocated, available)) {
                            System.out.println("[/] Safe Sequence:   " + sb.toString());
                            safeExists = true;
                        } else {
                            System.out.println("[X] Unsafe Sequence: " + sb.toString());
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
            System.out.println(); // extra line for clarity
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
                else System.out.println("    Error: Value must be non-negative.");
            } else {
                System.out.println("    Error: Enter a valid integer.");
                sc.next(); // clear invalid input
            }
        }
    }

    // Generate indices [0, 1, ..., n-1]
    private static int[] generateIndices() {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i;
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

    // Check if a given sequence (of indices) is safe
    private static boolean isSafe(int[] seq, int[] need, int[] alloc, int available) {
        int work = available;
        boolean[] finished = new boolean[n];

        for (int idx : seq) {
            if (!finished[idx] && need[idx] <= work) {
                work += alloc[idx];
                finished[idx] = true;
            } else {
                return false;
            }
        }
        return true;
    }

    // Print formatted allocation table using custom process IDs
    private static void printAllocationTable(int[] max, int[] alloc, int[] need, int available) {
        System.out.println("\n" + "=".repeat(65));
        System.out.println("           RESOURCE ALLOCATION TABLE");
        System.out.println("=".repeat(65));
        System.out.printf("%-12s %-15s %-15s %-15s%n", "Process", "Max Need", "Allocated", "Need");
        System.out.println("-".repeat(65));
        for (int i = 0; i < n; i++) {
            System.out.printf("%-12s %-15d %-15d %-15d%n", processIds[i], max[i], alloc[i], need[i]);
        }
        System.out.println("-".repeat(65));
        System.out.printf("%-12s %-15s %-15s %d%n", "", "", "Available:", available);
        System.out.println("=".repeat(65) + "\n");
    }
}