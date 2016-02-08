import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


public class PageReplacement {
	public static ArrayList<Integer> pgRef = new ArrayList<>();
	public static int totalPgs = 0;

	private static void readFile(String fileName) {
		String line = null;
		// Read file and store total number of pages in totalPgs and
		// Pagereferences in arraylist pgRef
		try {
			// System.out.println(fileName);
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			totalPgs = Integer.parseInt(br.readLine());
			System.out.println("Total Pgs " + totalPgs);
			while ((line = br.readLine()) != null) {
				pgRef.add(Integer.parseInt(line));
			}
//			System.out.println(totalPgs);
//			System.out.println(pgRef);
			br.close();

		}

		catch (FileNotFoundException e) {
			System.out.println("Unable to open file " + fileName);
			System.exit(1);
		}

		catch (IOException e) {
			System.exit(1);
		}
	}

	private static void pageFltFreq() {
		// When F is very small total number of page faults are more
		// but when F is optimum (around 40) numbr of page faults are minimum.
		// As we increase F (beyond 40) number of page faults would remain
		// constant.
		
//		int F = 40;
//		When F decreases, total number of page fault increase in PFF. And when F is very large, it doesn't impact total number of page faults.
		double F = Math.ceil(((double)(1)/100)*totalPgs);
		System.out.println();
		System.out.println("PFF");
		System.out.println("F: " + F);
		
		// HashMap to store resident set for page fault frequency algorithm
		HashMap<Integer, Integer> pResSet = new HashMap<Integer, Integer>();

		// Initialize count of last page fault to 0.
		int cntLstFlt = 0, cntFrmsLsTen = 0, maxFrms = 0, cntMaxFrms = 0;
		int cntTtlPgFlt = 0;
		// Access all page references in sequence
		for (int pr : pgRef) {
			// If referred page already present in resident set then increase
			// last page fault time and update use bit of that page to 1 in
			// resident set.
			// System.out.println("Referred page " + pr);
			// System.out.println("Resident set before " + pResSet);
			// System.out.println("Count from last page fault " + cntLstFlt);
			if (pResSet.containsKey(pr)) {
				cntLstFlt++;
				pResSet.put(pr, 1);
			} else {
				cntTtlPgFlt++;
				cntLstFlt++;
				// If page fault occurs and last page fault time is less than F
				// then add page to resident set.
				if (cntLstFlt < F) {
					pResSet.put(pr, 1);
				} else {
					// If page fault occurs and last page fault time is greater
					// than or equal to F then discard all pages with use bit 0
					// and set use bit to 0 for rest of the pages.
//					System.out.print("Resident set size before " + pResSet.size());
					HashMap<Integer, Integer> tempRem = new HashMap<Integer, Integer>();
					for (Entry<Integer, Integer> pair : pResSet.entrySet()) {
						if (pair.getValue() == 0) {
							// Adding elements to be removed temporarily in
							// tempRem hashmap
							tempRem.put(pair.getKey(), pair.getValue());
						} else
							pResSet.put(pair.getKey(), 0);

					}
					// Remove all elements from resident set whose use bit was 0
					// before page fault.
					for (int key : tempRem.keySet())
						pResSet.remove(key);
//					System.out.println("Resident set size after " + pResSet.size());
					tempRem.clear();
					pResSet.put(pr, 1);
				}
				// Update page fault time
				cntLstFlt = 0;

			}
			int size = pResSet.size();
			if (size < 10)
				cntFrmsLsTen++;
			if (size > maxFrms) {
				maxFrms = size;
				cntMaxFrms = 1;

			} else if (size == maxFrms) {
				cntMaxFrms++;
			}
			// System.out.println("Resident set after adding " + pResSet);
		}

		System.out.println("Total number of page faults using PFF algorithm " + cntTtlPgFlt);
		System.out.println(
				"Total number of times numbers of frames in memory less than 10 using PFF algorithm " + cntFrmsLsTen);
		System.out.println("Maximum number of frames used at a time in memory " + maxFrms);
		System.out.println("Total number of times max frames were present in memory " + cntMaxFrms);

	}

	private static void vsws() {
//		int M = 1200, L = 4000, Q = 1200;
//		int M = 1200, L = 4400, Q = 1500;
//		int M = 1200, L = 4400, Q = 1600;
//		In ideal situation, reset of bits should be triggered when number of page faults during interval equal to Q. 
//		If we keep Q very large, then reset of bits would be triggered by L.
		double M = Math.ceil(((double)(45)/100)*totalPgs);
		double Q = Math.ceil(((double)(40)/100)*totalPgs);
		double L = Math.ceil(totalPgs + ((double)(10)/100)*totalPgs);
		System.out.println();
		System.out.println("VSWS");
		System.out.println("M: " + M + " L: " + L + " Q: " + Q);
		

		// HashMap to store resident set for vsws algorithm
		HashMap<Integer, Integer> pResSet = new HashMap<Integer, Integer>();

		// Initialize count.
		int cntFrmsLsTen = 0, maxFrms = 0, cntMaxFrms = 0, intrvlPgFlt = 0, elapsedTime = 0;
		int cntTtlPgFlt = 0;
		// Access all page references in sequence
		for (int pr : pgRef) {
			// If referred page already present in resident set then increase
			// last page fault time and update use bit of that page to 1 in
			// resident set.
//			 System.out.println("Referred page " + pr);
//			 System.out.println("Resident set before " + pResSet);
//			 System.out.println("Interval Page fault count " + intrvlPgFlt);
			if (pResSet.containsKey(pr)) {
				pResSet.put(pr, 1);
//				elapsedTime++;
			} else {
				intrvlPgFlt++;
				cntTtlPgFlt++;
				// If page fault occurs before crossing Q and before reaching
				// maximum duration (Calculated after main if loop))
				// then add page to resident set.
				if (intrvlPgFlt < Q) {
					pResSet.put(pr, 1);
				} else if (intrvlPgFlt >= Q && elapsedTime < M) {
//					System.out.println("Occured 2 a Q fault occurs but M not reached yet");
//					System.out.println("IntervalFaults " + intrvlPgFlt + " Elapsed Time " + elapsedTime + " Size of resident set " + pResSet.size());
					pResSet.put(pr, 1);
				} else if (intrvlPgFlt >= Q) {
//					System.out.println("Triggered by Q ");
//					System.out.print("IntervalFaults " + intrvlPgFlt + " Elapsed Time " + elapsedTime + " Size of resident set " + pResSet.size());
					// If page fault is greater than or equal to Q within
					// interval and M duration already passed then
					// discard all pages with use bit 0 and set use bit to 0 for
					// rest of the pages.
					HashMap<Integer, Integer> tempRem = new HashMap<Integer, Integer>();
					for (Entry<Integer, Integer> pair : pResSet.entrySet()) {
						if (pair.getValue() == 0) {
							// Adding elements to be removed temporarily in
							// tempRem hashmap
							tempRem.put(pair.getKey(), pair.getValue());
						} else
							pResSet.put(pair.getKey(), 0);

					}
					// Remove all elements from resident set whose use bit was 0
					// before page fault.
					for (int key : tempRem.keySet())
						pResSet.remove(key);
//					System.out.println(" Resident set size after removal " + pResSet.size());
					tempRem.clear();
					pResSet.put(pr, 1);
					elapsedTime = 0;
					intrvlPgFlt = 1;

				}
				// Update page fault time and add referred page in resident set

			}
			elapsedTime++;
			if (elapsedTime >= L) {
//				System.out.println("Triggered by L");
//				System.out.print("IntervalFaults " + intrvlPgFlt + " Elapsed Time " + elapsedTime + " Size of resident set " + pResSet.size());
				HashMap<Integer, Integer> tempRem = new HashMap<Integer, Integer>();
				for (Entry<Integer, Integer> pair : pResSet.entrySet()) {
					if (pair.getValue() == 0) {
						// Adding elements to be removed temporarily in
						// tempRem hashmap
						tempRem.put(pair.getKey(), pair.getValue());
					} else
						pResSet.put(pair.getKey(), 0);

				}
				// Remove all elements from resident set whose use bit was 0
				// before page fault.
				for (int key : tempRem.keySet())
					pResSet.remove(key);
//				System.out.println(" Resident set size after removal " + pResSet.size());
				tempRem.clear();
				elapsedTime = 0;
				intrvlPgFlt = 0;
			}

			int size = pResSet.size();
			if (size < 10)
				cntFrmsLsTen++;
			if (size > maxFrms) {
				maxFrms = size;
				cntMaxFrms = 1;

			} else if (size == maxFrms) {
				cntMaxFrms++;
			}
//			System.out.println("Size of Resident set after adding " + pResSet.size());
//			 System.out.println("Resident set after adding " + pResSet);
		}

		System.out.println("Elapsed time at the end of program " + elapsedTime + " IntervalFaults " + intrvlPgFlt);
		System.out.println("Total number of page faults using VSWS algorithm " + cntTtlPgFlt);
		System.out.println("Total number of times numbers of frames in memory less than 10 using VSWS algorithm " + cntFrmsLsTen);
		System.out.println("Maximum number of frames used at a time in memory for VSWS " + maxFrms);
		System.out.println("Total number of times max frames were present in memory for VSWS " + cntMaxFrms);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args[0] != null) {
			String s = args[0];
			// System.out.println(s);
			readFile(s);
			pageFltFreq();
			vsws();

		} else
			System.out.println("Please enter input file path");
	}

}
