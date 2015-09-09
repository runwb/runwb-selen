package org.runwb.lib.selen;

import org.openqa.selenium.WebDriver;

public class SelenWb {
	@Deprecated
	public static <P extends Selen.Page> P newPage(Class<P> pcls, WebDriver driver) {
		try {
			P p = pcls.newInstance();
			p.bind(driver);
			return p;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
/*	public static WebElement nullOrElem(Supplier<WebElement> find) {
		try {
			return find.get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}*/
	public interface Unchecked {
		Object get() throws Exception;
	}
	public static Object unchecked(Unchecked code) {
		try {
			return code.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
