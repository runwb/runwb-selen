package org.runwb.lib.selen;

import org.runwb.lib.selen.Selen.Sync.Over;
import org.runwb.lib.selen.Selen.Sync.Yes;

public class SelenSync {
	final Selen selen;
	SelenSync(Selen selen) {
		this.selen = selen;
	}
	public <P> Over<P> over(long before, long after, boolean pub, Yes<P> yes) {
		return over(200, before, after, pub, yes);
	}
	public <P> Over<P> over(long interval, long before, long after, boolean pub, Yes<P> yes) {
		return new Over<>(selen, interval, before, after, pub, yes);
	}
}
