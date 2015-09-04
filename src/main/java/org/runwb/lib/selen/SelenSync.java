package org.runwb.lib.selen;

import org.runwb.lib.selen.Selen.Sync.Over;
import org.runwb.lib.selen.Selen.Sync.Yes;

public class SelenSync {
	final Selen selen;
	SelenSync(Selen selen) {
		this.selen = selen;
	}
	public Over over(long before, long after, boolean pub, Yes yes) {
		return over(200, before, after, pub, yes);
	}
	public <P> Over over(long interval, long before, long after, boolean pub, Yes yes) {
		return new Over(selen, interval, before, after, pub, yes);
	}
}
