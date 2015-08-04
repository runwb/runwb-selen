package org.runwb.lib.selen;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Selen extends SelenSelen {
	public static abstract class Page extends SelenPage {
		public class Obj extends SelenObj {
			public Obj(By by) { super(by); }
			public Obj(WebElement elem) { super(elem); }
			@Override public Page page() {
				return Page.this;
			}
		}
		public class NullObj extends Obj {
			public final NoSuchElementException exception;
			public NullObj(NoSuchElementException exception) {
				super((WebElement) null);
				this.exception = exception;
			}
		}
	}

	public static interface Script extends SelenScript {}

	public Selen () {
		this(new FirefoxDriver());
	}
	public Selen (Class<? extends WebDriver> driverCls) {
		this((WebDriver) unchecked(() -> driverCls.newInstance()));
	}
	public Selen (WebDriver driver) {
		super(driver);
	}
}
