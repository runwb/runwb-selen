package org.runwb.lib.selen;

import org.openqa.selenium.WebDriver;

public interface SelenScript {
	static <P extends Selen.Page> P newPage(Class<P> pgCls, WebDriver driver, String url) {
		return Selen.newPage(pgCls, driver, url);
	}
}
