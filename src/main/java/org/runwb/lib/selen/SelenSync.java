package org.runwb.lib.selen;

import org.runwb.lib.selen.Selen.Sync.Over;
import org.runwb.lib.selen.Selen.Sync.Yes;

public class SelenSync {
	final Selen selen;
	SelenSync(Selen selen) {
		this.selen = selen;
	}
	public boolean over(double afterS, Yes yes) {
		return overSrc(afterS, yes).isOver();
	}
	public boolean over(double beforeS, double afterS, Yes yes) {
		return overSrc(beforeS, afterS, yes).isOver();
	}
	public Over overSrc(double afterS, Yes yes) {
		return overSrc(0.2, 2, afterS, true, yes);
	}
	public Over overSrc(double beforeS, double afterS, Yes yes) {
		return overSrc(0.2, beforeS, afterS, true, yes);
	}
	public Over overSrc(double intervalS, double beforeS, double afterS, boolean pub, Yes yes) {
		return new Over(selen, intervalS, beforeS, afterS, pub, yes);
	}
	public boolean reached(double timeoutS, Yes yes) {
		return srcReached(timeoutS, yes).isReached();
	}
	public Selen.Sync.Reached srcReached(double timeoutS, Yes yes) {
		return srcReached(0.2, timeoutS, true, yes);
	}
	public Selen.Sync.Reached srcReached(double intervalS, double timeoutS, boolean pub, Yes yes) {
		return new Selen.Sync.Reached(selen, intervalS, timeoutS, pub, yes);
	}
		
		/*
		selen.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS);
		
		try {
			long interval = Math.round(intervalS * 1000);
			long timeout = Math.round(timeoutS * 1000);
	
			
			long start = System.currentTimeMillis();
			boolean first = true;
			if (pub) {
				System.out.println("check exist...");
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
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				boolean exists;
				try {
					exists = yes.yes();
				} catch (Exception xn) {
					exists = false;
				}
				times++;
				if (exists) {
					res = true;
					break;
				}
			}
			if (res)
				System.out.println("validation suceeded!!! - after " + times + " trys (" + Math.round(interval / 1000) + "s interval)");
			else
				System.out.println("validation failed!!! - timing out after " + Math.round(timeout / 1000) + "s");
			return res;
		}
		finally {
			selen.manage().timeouts().implicitlyWait(selen.timeout(), TimeUnit.SECONDS);
		}
	}*/
}
