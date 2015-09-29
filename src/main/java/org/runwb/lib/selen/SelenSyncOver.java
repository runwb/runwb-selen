package org.runwb.lib.selen;

import java.util.concurrent.TimeUnit;

import org.runwb.lib.selen.Selen.Yes;

public abstract class SelenSyncOver {
	final Selen selen;
	final Yes yes;
	final long interval;
	final long before;
	final long after;
	final boolean pub;

	SelenSyncOver(Selen selen, double intervalS, double beforeS, double afterS, boolean pub, Yes yes) {
		this.selen = selen;
		this.interval = Math.round(intervalS * 1000);
		this.before = Math.round(beforeS * 1000);
		this.after = Math.round(afterS * 1000);
		this.pub = pub;
		this.yes = yes;
	}
	public boolean is() {
		if (selen != null)
			selen.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS);
		try {
			boolean got = false;
			long start = System.currentTimeMillis();
			boolean first = true;
			long timeout = before;
			if (pub) {
				System.out.println("waiting to get pass \"over\" condition...");
				StackTraceElement[] ste = Thread.currentThread().getStackTrace();
				System.out.println(ste[2]);
			}
			while (System.currentTimeMillis() - start <= timeout) {
				if (first)
					first = false;
				else
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
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
			if (selen != null)
			selen.manage().timeouts().implicitlyWait(selen.timeout(), TimeUnit.SECONDS);
		}
	}
}
