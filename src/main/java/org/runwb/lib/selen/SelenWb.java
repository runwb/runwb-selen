package org.runwb.lib.selen;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SelenWb {
	public static <P extends Selen.Page> P newPage(Class<P> pcls, WebDriver driver, String url) {
		try {
			return pcls.newInstance().bind(driver, url);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static Selen.Obj obj(By by) {
		return new Selen.Obj(by);
	}
}
