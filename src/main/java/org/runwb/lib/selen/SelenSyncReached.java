package org.runwb.lib.selen;

import org.openqa.selenium.UnhandledAlertException;
import org.runwb.lib.selen.Selen.Sync.Intervals;
import org.runwb.lib.selen.Selen.Sync.Yes;

public abstract class SelenSyncReached {
	final Selen selen;
	final Yes yes;
	final Intervals intervals;
	final long timeout;
	final boolean pub;

	SelenSyncReached(Selen selen, Intervals intervals, double timeoutS, boolean pub, Yes yes) {
		this.selen = selen;
		if (intervals == null)
			intervals = new Intervals();
		this.intervals = intervals;
		this.timeout = Math.round(timeoutS * 1000);
		this.pub = pub;
		this.yes = yes;
	}
	public boolean isReached() {
		try {
			selen.timeout.override(0.2);
			long start = System.currentTimeMillis();
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
			intervals.reset();
			while (System.currentTimeMillis() - start <= timeout) {
				Selen.sleep(intervals.next());
				boolean reached;
				try {
					reached = yes.yes();
				} catch (UnhandledAlertException xnAlert) {
					try {
						throw new Selen.Xn.UnexpectedAlert(selen.driver.switchTo().alert());
					} catch (Exception xn) {
						throw new Selen.Xn.UnexpectedAlert(xnAlert);
					}
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
				System.out.println("\"reach\" suceeded!!! - after " + times + " trys (" + (System.currentTimeMillis() - start)/1000. + "s)");
			else
				System.out.println("\"reach\" failed!!! - timing out after " + Math.round(timeout / 1000L) + "s");
			return res;
		} finally {
			selen.timeout.reset();
		}
	}
}
