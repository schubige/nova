package org.corebounce.util;

import java.util.ArrayList;
import java.util.List;

public class ThreadUtilities {
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {}
	}

	public static List<Thread> getAllThreads() {
		List<Thread> result = new ArrayList<Thread>();
		ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
		while (root.getParent() != null)
			root = root.getParent();

		// Visit each thread group
		getAllThreadsRecr(root, 0, result);
		
		return result;
	}

	// This method recursively visits all thread groups under `group'.
	private static void getAllThreadsRecr(ThreadGroup group, int level, List<Thread> result) {
		int numThreads   = group.activeCount();
		Thread[] threads = new Thread[numThreads*2];
		numThreads       = group.enumerate(threads, false);

		for (int i = 0; i < numThreads; i++)
			result.add(threads[i]);

		// Get thread subgroups of `group'
		int numGroups        = group.activeGroupCount();
		ThreadGroup[] groups = new ThreadGroup[numGroups*2];
		numGroups            = group.enumerate(groups, false);

		// Recursively visit each subgroup
		for (int i = 0; i < numGroups; i++)
			getAllThreadsRecr(groups[i], level + 1, result);
	}
}
