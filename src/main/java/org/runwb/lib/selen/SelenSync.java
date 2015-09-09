package org.runwb.lib.selen;

import org.runwb.lib.selen.Selen.Sync.Over;
import org.runwb.lib.selen.Selen.Sync.Yes;

public class SelenSync {
	final Selen selen;
	SelenSync(Selen selen) {
		this.selen = selen;
	}
	public Over over(double afterS, Yes yes) {
		return new Over(selen, 0.2, 2, afterS, true, yes);
	}
	public Over over(double beforeS, double afterS, boolean pub, Yes yes) {
		return new Over(selen, 0.2, beforeS, afterS, pub, yes);
	}
	public <P> Over over(double intervalS, double beforeS, double afterS, boolean pub, Yes yes) {
		return new Over(selen, intervalS, beforeS, afterS, pub, yes);
	}
}
