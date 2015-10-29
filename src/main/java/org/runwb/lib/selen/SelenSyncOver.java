package org.runwb.lib.selen;

import org.runwb.lib.selen.Selen.Sync.Yes;

public abstract class SelenSyncOver {
	final Selen selen;
	final Yes yes;
	final double intervalS;
	final long before;
	final long after;
	final boolean pub;

	SelenSyncOver(Selen selen, double intervalS, double beforeS, double afterS, boolean pub, Yes yes) {
		this.selen = selen;
		this.intervalS = intervalS;
		this.before = Math.round(beforeS * 1000);
		this.after = Math.round(afterS * 1000);
		this.pub = pub;
		this.yes = yes;
	}
	public boolean isOver() {
		try {
			selen.timeout.override(0.2);
			boolean got = false;
			long start = System.currentTimeMillis();
			boolean first = true;
			long timeout = before;
			if (pub) {
				System.out.println("waiting to get pass \"over\" condition...");
				StackTraceElement[] stes = Thread.currentThread().getStackTrace();
				String pkgNm = getClass().getPackage().getName();
				for (StackTraceElement ste : stes)
					if (!ste.getClassName().startsWith(pkgNm)) {
						System.out.println(ste);
						break;
					}
			}
			while (System.currentTimeMillis() - start <= timeout) {
				if (first)
					first = false;
				else
					Selen.sleep(intervalS);
				boolean met = false;
				try {
					met = yes.yes();
				} catch (Exception xn) {
					met = false;
				}
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
		finally {
			selen.timeout.reset();
		}
	}
}
