package org.runwb.lib.selen;

import org.runwb.lib.selen.Selen.Sync.Yes;

public class SelenValidate {
	final Selen selen;
	SelenValidate(Selen selen) {
		this.selen = selen;
	}
	
	public boolean is(double timeoutS, Yes yes) {
		return is(0.2, timeoutS, true, yes);
	}
	public boolean is(double intervalS, double timeoutS, boolean pub, Yes yes) {

		selen.timeout.override(0.2);
		try {
			long interval = Math.round(intervalS * 1000);
			long timeout = Math.round(timeoutS * 1000);
			long start = System.currentTimeMillis();
			boolean first = true;
			if (pub) {
				System.out.println("validating...");
				StackTraceElement[] stes = Thread.currentThread().getStackTrace();
				String pkgNm = getClass().getPackage().getName();
				for (int i=1; i<stes.length; i++) {
					StackTraceElement ste = stes[i];
					if (!ste.getClassName().startsWith(pkgNm)) {
						System.out.println(ste);
						break;
					}
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
				boolean good;
				try {
					good = yes.yes();
				} catch (Exception xn) {
					good = false;
				}
				times++;
				if (good) {
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
			selen.timeout.reset();
		}
	}

}
