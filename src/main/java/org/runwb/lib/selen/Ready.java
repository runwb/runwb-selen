package org.runwb.lib.selen;

import java.util.function.BooleanSupplier;

public class Ready {
	public double timeout = 30;
	public double retry = 0.25;
	public BooleanSupplier check = null;

	public boolean waitFor() {
		long start = System.currentTimeMillis();
		long timeoutMs = (long) timeout * 1000;
		long retryMs = (long) retry * 1000;
		if (check.getAsBoolean())
			return true;
		while (System.currentTimeMillis() - start < timeoutMs) {
			try {
				Thread.sleep(retryMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (check.getAsBoolean())
				return true;
		}
		return false;
	}
}
