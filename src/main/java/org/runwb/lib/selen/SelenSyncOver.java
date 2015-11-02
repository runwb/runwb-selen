package org.runwb.lib.selen;

import org.openqa.selenium.UnhandledAlertException;
import org.runwb.lib.selen.Selen.Sync.Yes;

public abstract class SelenSyncOver {
	final Selen selen;
	final Yes yes;
	final Selen.Sync.Intervals intervals;
	final long before;
	final long after;
	final boolean pub;

	SelenSyncOver(Selen selen, Selen.Sync.Intervals intervals, double beforeS, double afterS, boolean pub, Yes yes) {
		this.selen = selen;
		if (intervals == null)
			intervals = new Selen.Sync.Intervals();
		this.intervals = intervals;
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
			long timeout = before;
			if (pub) {
				System.out.println("waiting to get pass \"over\" condition...");
				StackTraceElement[] stes = Thread.currentThread().getStackTrace();
				String pkgNm = getClass().getPackage().getName();
//				for (StackTraceElement ste : stes)
				for (int i=1; i<stes.length; i++) {
					StackTraceElement ste = stes[i];
					if (!ste.getClassName().startsWith(pkgNm)) {
						System.out.println(ste);
						break;
					}
				}
			}
			intervals.reset();
			while (System.currentTimeMillis() - start <= timeout) {
				Selen.sleep(intervals.next());
				boolean met = false;
				try {
					met = yes.yes();
				} catch (UnhandledAlertException xnAlert) {
					try {
						throw new Selen.Xn.UnexpectedAlert(selen.driver.switchTo().alert());
					} catch (Exception xn) {
						throw new Selen.Xn.UnexpectedAlert(xnAlert);
					}
				} catch (Exception xn) {
					continue;
				}
				if (met) {
					if (!got) {
						got = true;
						if (pub)
							System.out.println("over condition encountered!!!");
						timeout = after;
						intervals.reset();
					}
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
