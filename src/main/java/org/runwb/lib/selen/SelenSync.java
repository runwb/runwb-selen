package org.runwb.lib.selen;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.UnhandledAlertException;
import org.runwb.lib.selen.Selen.Page;
import org.runwb.lib.selen.Selen.Xn;
import org.runwb.lib.selen.Selen.Sync.Intervals;
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
		return overSrc(null, 2, afterS, true, yes);
	}
	public Over overSrc(double beforeS, double afterS, Yes yes) {
		return overSrc(null, beforeS, afterS, true, yes);
	}
	public Over overSrc(Intervals intervals, double beforeS, double afterS, boolean pub, Yes yes) {
		return new Over(selen, intervals, beforeS, afterS, pub, yes);
	}
	public boolean reached(double timeoutS, Yes yes) {
		return reachedSrc(timeoutS, yes).isReached();
	}
	public Selen.Sync.Reached reachedSrc(double timeoutS, Yes yes) {
		return reachedSrc(null, timeoutS, true, yes);
	}
	public Selen.Sync.Reached reachedSrc(Intervals intervals, double timeoutS, boolean pub, Yes yes) {
		return new Selen.Sync.Reached(selen, intervals, timeoutS, pub, yes);
	}
	@SafeVarargs
	public final Page where(Class<? extends Page>...pageClss) {
		selen.timeout.override(0.2);
		try {
			List<Page> theres = new ArrayList<>();
			for (Class<? extends Page> pageCls : pageClss) {
				Constructor<? extends Page> cntr;
				try {
					cntr = pageCls.getDeclaredConstructor();
				} catch (Exception xn) {
					throw Xn.unchecked(xn);
				}
				cntr.setAccessible(true);
				try {
					Page page = cntr.newInstance();
					page.bind(selen, 0.2);
					theres.add(page);
				} catch (Selen.Xn.PageUnavailable unavail) {
				} catch (UnhandledAlertException xnAlert) {
					//TODO simplify
					try {
						throw new Selen.Xn.UnexpectedAlert(selen.switchTo().alert());
					} catch (Exception xn) {
						throw new Selen.Xn.UnexpectedAlert(xnAlert);
					}
				} catch (Exception xn) {
					throw Xn.unchecked(xn);
				}
			}
			if (theres.size() > 1) {
				StringBuilder sb = new StringBuilder();
				sb.append("multiple pages matching - " + theres.get(0).name);
				for (int i=1; i<theres.size(); i++)
					sb.append(", " + theres.get(i).name);
				throw new Xn(sb.toString());
			}
			else if (theres.size() == 0)
				return null;
			else
				return theres.get(0);
		} finally {
			selen.timeout.reset();
		}
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
