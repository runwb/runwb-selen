package org.runwb.lib.selen;

public abstract class SelenSyncIntervals {
	static class Set {
		boolean exhausted() { return times >= limit; }
		final long val;
		final int limit;
		Set(long val, int limit) {
			this.val = val;
			this.limit = limit;
		}
		
		int times = 0;
		long next() {
			times++;
			return val;
		}
		public static Set a(long val, int limit) {
			return new Set(val, limit);
		}
	}
	final Set[] sets;
	final int n;
	int i=0;
	Set cur;
	protected SelenSyncIntervals(Set...sets) {
		this.sets = sets;
		n = sets.length;
		reset();
	}
	public long next() {
		if (i<n && cur.exhausted())
			cur = sets[i++];
		return cur.next();
	}
	public void reset() {
		i=0;
		cur = sets[i++];
	}
}
