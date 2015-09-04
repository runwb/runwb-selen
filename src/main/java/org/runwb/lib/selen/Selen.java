package org.runwb.lib.selen;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Selen extends SelenSelen {
	public static abstract class Page extends SelenPage {
		public class Obj extends SelenObj {
			public Obj(Obj container, By by, Obj.MultiChoose multiChoose) { super(container, by, multiChoose); }
			public Obj(WebElement elem, By by) { super(elem, by); }
			@Override public Page page() {
				return Page.this;
			}
		}
		public class NullObj extends Obj {
			public final NoSuchElementException noElemXn;
			public NullObj(NoSuchElementException exception) {
				super((WebElement) null, null);
				this.noElemXn = exception;
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
	@SuppressWarnings("serial")
	public static class Xn extends RuntimeException {

		public Xn() { super(); }

		public Xn(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public Xn(String message, Throwable cause) {
			super(message, cause);
		}

		public Xn(String message) {
			super(message);
		}

		public Xn(Throwable cause) {
			super(cause);
		}
	}
	public static class Sync extends SelenSync {
		public Sync(Selen selen) {
			super(selen);
		}
		public static interface Yes {
			boolean yes();
		}
		public static class Over extends SelenSyncOver {
			Over(Selen selen, long interval, long before, long after, boolean pub, Yes yes) {
				super(selen, interval, before, after, pub, yes);
			}
		}
	}
}
