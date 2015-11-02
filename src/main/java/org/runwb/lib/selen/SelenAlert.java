package org.runwb.lib.selen;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.runwb.lib.selen.Selen.Sync.Intervals;

public class SelenAlert {
	final Selen selen;
	SelenAlert(Selen selen) {
		this.selen = selen;
	}
	
	public Alert here() {
		try {
			return selen.driver.switchTo().alert();
		} catch (NoAlertPresentException e) {
			return null;
		}
	}
	public Alert here(double timeoutS) {
		long timeout = Math.round(timeoutS) * 1000;
		Intervals intervals = new Intervals();
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis() - start < timeout) {
			Selen.sleep(intervals.next());
			Alert a = here();
			if (a != null)
				return a;
		}
		return null;
	}
}
