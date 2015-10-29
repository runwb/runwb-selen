package org.runwb.lib.selen;

import org.runwb.lib.selen.Selen.Sync.Yes;

public abstract class SelenSyncReached {
	final Selen selen;
	final Yes yes;
	final double intervalS;
	final long timeout;
	final boolean pub;

	SelenSyncReached(Selen selen, double intervalS, double timeoutS, boolean pub, Yes yes) {
		this.selen = selen;
		this.intervalS = intervalS;
		this.timeout = Math.round(timeoutS * 1000);
		this.pub = pub;
		this.yes = yes;
	}
	public boolean isReached() {
		try {
			selen.timeout.override(0.2);
			long start = System.currentTimeMillis();
			boolean first = true;
			if (pub) {
				System.out.println("check reached...");
				StackTraceElement[] stes = Thread.currentThread().getStackTrace();
				String pkgNm = getClass().getPackage().getName();
				for (StackTraceElement ste : stes)
					if (!ste.getClassName().startsWith(pkgNm)) {
						System.out.println(ste);
						break;
					}
			}
			int times = 0;
			boolean res = false;
			while (System.currentTimeMillis() - start <= timeout) {
				if (first)
					first = false;
				else
					Selen.sleep(intervalS);
				boolean reached;
				try {
					reached = yes.yes();
				} catch (Exception xn) {
					reached = false;
				}
				times++;
				if (reached) {
					res = true;
					break;
				}
			}
			if (res)
				System.out.println("\"reach\" suceeded!!! - after " + times + " trys (" + intervalS + "s interval)");
			else
				System.out.println("\"reach\" failed!!! - timing out after " + Math.round(timeout / 1000L) + "s");
			return res;
		}
		finally {
			selen.timeout.reset();
		}
	}
}
