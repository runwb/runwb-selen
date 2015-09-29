package org.runwb.lib.selen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Selen extends SelenSelen {
	public final SelenValidate validate = new SelenValidate(this);
	public static class Page extends SelenPage {
		public static class Obj extends SelenPageObj {
			public Obj(Page page, WebElement elem, By by) { super(page, elem, by); }
			public Obj(Page page, By by) { super(page, by); }
			@java.lang.annotation.Target(value = { ElementType.FIELD })
			@Retention(value = RetentionPolicy.RUNTIME)
			@Deprecated
			public static @interface Late {
				boolean is() default true;
			}
		}
		public static class Target<P extends Page> extends Obj {
			final Class<P> target;
			public Target(Page page, Class<P> target, By by) {
				super(page, by);
				this.target = target;
			}
			public P go() {
				click();
				if (target != null)
					try {
						P p = target.newInstance();
						p.bind(page.driver);
						return p;
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				else
					return null;
			}
		}
		public static class Select extends SelenPageSelect {
			public Select(Page page, By by, Function<WebElement, List<WebElement>> choices) {
				super(page, by, choices);
			}
		}
		public static class NullObj extends Obj {
			public final NoSuchElementException noElemXn;
			public NullObj(Page page, NoSuchElementException exception) {
				super(page, (WebElement) null, null);
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
	public static interface Yes {
		boolean yes();
	}
	public static class Sync extends SelenSync {
		public Sync(Selen selen) {
			super(selen);
		}
		public static class Over extends SelenSyncOver {
			public Over(Selen selen, double intervalS, double beforeS, double afterS, boolean pub, Yes yes) {
				super(selen, intervalS, beforeS, afterS, pub, yes);
			}
		}
	}
}
