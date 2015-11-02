package org.runwb.lib.selen;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Selen extends SelenSelen {
	public final SelenValidate validate = new SelenValidate(this);
	public static class Page extends SelenPage {
		public static class Obj extends SelenPageObj {
			Obj(Page page, WebElement elem, By by) { super(page, elem, by); }
			Obj(Page page, By by) { super(page, by); }
			Obj(Obj obj) { super(obj); }
			public interface PickFromList {
				WebElement pick(List<WebElement> elems);
			}
		}
		public static class Target<P extends Page> extends Obj {
			final Class<P> target;
			/*
			public Target(P page, Class<P> target, By by) {
				super(page, by);
				this.target = target;
			}*/
			Target(Page.Obj obj, Class<P> target) {
				super(obj);
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
		public static class UnexpectedAlert extends Xn {
			public final Alert alert;
			UnexpectedAlert(Alert alert) {
				super("Modal dialog present: " + fallback("", ()-> alert.getText()));
				this.alert = alert;
			}
			UnexpectedAlert(UnhandledAlertException xn) {
				super(xn);
				this.alert = null;
			}
		}
		public static class PageUnavailable extends Xn {
			public PageUnavailable(String msg, Exception xn) {
				super(msg, xn);
			}
		}

		public static RuntimeException unchecked(Exception xn) {
			if (xn instanceof RuntimeException)
				return (RuntimeException)xn;
			else
				return new RuntimeException(xn);
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
			public Over(Selen selen, Intervals intervals, double beforeS, double afterS, boolean pub, Yes yes) {
				super(selen, intervals, beforeS, afterS, pub, yes);
			}
		}
		public static class Reached extends SelenSyncReached {
			public Reached(Selen selen, Intervals intervals, double afterS, boolean pub, Yes yes) {
				super(selen, intervals, afterS, pub, yes);
			}
		}
		public static class Intervals extends SelenSyncIntervals {

			public Intervals() {
				this(
					Set.a(0, 1),
					Set.a(125, 8),
					Set.a(250, 8),
					Set.a(500, 8),
					Set.a(1000, 0)
				);
			}
			public Intervals(long interval) {
				this(
					Set.a(0, 1),
					Set.a(interval, 0)
				);
			}
			public Intervals(Set... sets) {
				super(sets);
			}
		}
	}
	static void sleep(double timeS) {
		sleep(Math.round(timeS * 1000));
	}
	static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	public static <O> O fallback(O fallback, Supplier<O> supplier) {
		try {
			return supplier.get();
		} catch (Exception xn) {
			return fallback;
		}
	}
}
