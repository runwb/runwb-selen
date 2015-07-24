package org.runwb.lib.selen;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Selen {
	public static abstract class Page extends SelenPage {}

	public static class Obj extends SelenObj {
		public Obj(By by) {
			super(by);
		}
	}
	public static <P extends Selen.Page> P newPage(Class<P> pcls, WebDriver driver, String url) {
		try {
			return pcls.newInstance().init(driver, url);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static Selen.Obj obj(By by) {
		return new Selen.Obj(by);
	}
}
