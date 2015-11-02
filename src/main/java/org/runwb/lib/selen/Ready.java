package org.runwb.lib.selen;

import java.util.function.BooleanSupplier;

import org.openqa.selenium.UnhandledAlertException;
import org.runwb.lib.selen.Selen.Sync.Intervals;

public class Ready {
	public double timeoutS = 30;
	public Intervals intervals = new Intervals();
	public BooleanSupplier check = null;

	public boolean waitFor() {
		return waitFor(null);
	}
	public boolean waitFor(Double timeoutS) {
		if (timeoutS == null)
			timeoutS = this.timeoutS;
		if (check == null)
			return true;
		long start = System.currentTimeMillis();
		long timeout = Math.round(timeoutS * 1000);
		if (check.getAsBoolean())
			return true;
		intervals.reset();
		while (System.currentTimeMillis() - start < timeout) {
			Selen.sleep(intervals.next());
			try {
				if (check.getAsBoolean())
					return true;
			} catch (UnhandledAlertException xnAlert) {
				throw new Selen.Xn.UnexpectedAlert(xnAlert);
			} catch (Exception xn) {
				return false;
			}
		}
		return false;
	}
}
