package org.runwb.lib.selen;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Selen extends SelenSelen {
	public static abstract class Page extends SelenPage {}

	public static interface Script extends SelenScript {}

	public static class Obj extends SelenObj {
		public Obj(By by) {
			super(by);
		}
	}
	public Selen () {
		this(new FirefoxDriver());
	}
	public Selen (WebDriver driver) {
		super(driver);
	}
}
