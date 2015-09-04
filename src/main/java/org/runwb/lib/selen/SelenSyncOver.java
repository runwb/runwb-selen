package org.runwb.lib.selen;

import org.runwb.lib.selen.Selen.Sync.Yes;

public abstract class SelenSyncOver {
	final Selen selen;
	final Yes yes;
	final long interval;
	final long before;
	final long after;
	final boolean pub;

	SelenSyncOver(Selen selen, long interval, long before, long after, boolean pub, Yes yes) {
		this.selen = selen;
		this.interval = interval;
		this.before = before;
		this.after = after;
		this.pub = pub;
		this.yes = yes;
	}
	public boolean is() {
		boolean got = false;
		long start = System.currentTimeMillis();
		boolean first = true;
		long timeout = before;
		while (System.currentTimeMillis() - start <= timeout) {
			if (first)
				first = false;
			else
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			boolean met = yes.yes();
			if (met) {
				if (!got && pub)
					System.out.println("over condition encountered!!!");
				got = true;
				timeout = after;
			}
			else // not met
				if (got) {
					System.out.println("over condition gone!!!");
					return true;
				}
		}
		if (got)
			System.out.println("over condition remains!!! - timing out after " + Math.round(after / 1000) + "s");
		else
			System.out.println("over condition probably missed!!! - timing out after " + Math.round(before / 1000) + "s");
		return false;
	}
}
