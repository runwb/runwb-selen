package org.runwb.lib.selen;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.runwb.lib.selen.Selen.Page;

public abstract class SelenObj implements WebElement, Locatable {
	public abstract Selen.Page page();
	WebElement elem;
	NoSuchElementException noElemXn;
	public String name;
	public final By by;
	public final MultiChoose multiChoose;
	public final SearchContext container;
	Boolean late = null;

	public NoSuchElementException noElemXn() {
		elem();
		return noElemXn;
	}
//	final boolean bound;
	public WebElement elem() {
		if (elem == null)
			return bind();
		return elem;
	}
//	public boolean bound() { return bound; }

	WebElement action() {
		WebElement e = elem();
		WebDriver wd = page().driver;
		if (wd instanceof Selen) {
			Selen selen = (Selen) wd;
			if (selen.play.paused()) {
				String nm;
				if (this.name != null)
					nm = this.name;
				else if (by != null)
					nm = by.toString();
				else
					nm = e.toString();
				System.out.println("\"" + nm + "\" action paused...");
				System.out.println(Thread.currentThread().getStackTrace()[3]);
				selen.play.proceed();
				System.out.println("\"" + nm + "\" action continue");
			}
			else
				selen.play.proceed();
		}
		return e;
	}

	WebElement bind() {
/*		if (bound)
			return;
		bound = true;
		if (elem != null) {
			bound = true;
			return;
		}*/
		long start = System.currentTimeMillis();
		if (Boolean.TRUE.equals(late))
			System.out.println("Obj:" + name + " - finding... ");
		WebElement e;
		SearchContext container = this.container;
		if (container == null)
			container = page().driver;
		if (multiChoose == null)
			try {
				e = container.findElement(by);
			} catch (NoSuchElementException xn) {
				e = page().new NullObj(xn);
			}
		else {
			List<WebElement> candidates = container.findElements(by);
			if (candidates.size() > 0)
				e = multiChoose.pick(candidates);
			else
				e = page().new NullObj(null);
		}
		if (e instanceof Page.NullObj) {
			noElemXn = ((Page.NullObj) e).noElemXn;
			e = null;
		}
//			throw ((Page.NullObj) elem).exception;
		else if (e instanceof Page.Obj)
			e = ((Page.Obj) e).elem;
//		elem = e;
		if (Boolean.TRUE.equals(late))
			if (e != null)
				System.out.println("Obj:" + name + " - found after " + ((float)(System.currentTimeMillis() - start) / 1000) + "s");
			else
				System.out.println("Obj:" + name + " - not found after " + ((float)(System.currentTimeMillis() - start) / 1000) + "s");
		return e;
	}
	public interface MultiChoose {
		WebElement pick(List<WebElement> elems);
	}

	public SelenObj(WebElement elem, By by) {
		this(null, by, null);
		this.elem = elem;
	}

	public SelenObj(SearchContext container, By by, MultiChoose multichoose) {
		this.container = container;
		this.by = by;
		this.multiChoose = multichoose;
	}

	public void type(String txt) {
		WebElement e = action();
		if (page().highlightObj)
			highlightObj();
		System.out.println("Obj:" + name + " - about to type " + txt);
		e.clear();
		e.sendKeys(txt);
	}
	public void click() {
		WebElement e = action();
		if (page().highlightObj)
			highlightObj();
		e.click();
		System.out.println("Obj:" + name + " - about to click it");
	}
	public void highlightObj() {
		System.out.println("highlighting obj:" + name);
		JavascriptExecutor js = (JavascriptExecutor) page().driver;
		js.executeScript(
				"arguments[0].setAttribute('style', arguments[1]);",
				elem(), "color: red; border: 3px solid red;");
		JOptionPane.showMessageDialog(null, name);
		js.executeScript(
				"arguments[0].setAttribute('style', arguments[1]);",
				elem(), "");				
	}
	public static boolean isNull(WebElement obj) {
		if (obj == null)
			return true;
		return obj instanceof Page.NullObj;
	}

	public boolean isNull() {
		return isNull(this) || elem() == null;
	}
	public boolean isObj() {
		return !isNull();
	}
	public NoSuchElementException getNotFoundException() {
		return isNull() ? ((Page.NullObj) this).noElemXn : null;
	}
	public void mouseTo() {
		new Actions(page().driver).moveToElement(this).perform();
	}
	
	@Override public Page.Obj findElement(By arg0) {
		try {
			return page().new Obj(elem().findElement(arg0), arg0);
		} catch (NoSuchElementException e) {
			return page().new NullObj(e);
		}
	}
	public Page.Obj findElement(Integer timeout, By arg0) {
		WebDriver driver = page().driver;
		Selen selen = null;
		if (driver instanceof Selen)
			selen = (Selen) driver;
		if (timeout != null && selen != null)
			selen.timeout.push(timeout);
		Page.Obj obj = findElement(arg0);
		if (timeout != null && selen != null)
			selen.timeout.pop();
		return obj;
	}

	@Override public List<WebElement> findElements(By arg0) {
		List<WebElement> list = elem().findElements(arg0);
		if (list == null)
			return null;
		List<WebElement> newList = new ArrayList<>();
		for (WebElement e : list)
			newList.add(page().new Obj(e, arg0));
		return newList;
	}
	@Override public String getAttribute(String arg0) { return elem().getAttribute(arg0); }
	@Override public String getCssValue(String arg0) { return elem().getCssValue(arg0); }
	@Override public Point getLocation() { return elem().getLocation(); }
	@Override public Dimension getSize() { return elem().getSize(); }
	@Override public String getTagName() { return elem().getTagName(); }
	@Override public String getText() { return elem().getText(); }
	@Override public boolean isDisplayed() { return elem().isDisplayed(); }
	@Override public boolean isEnabled() { return elem().isEnabled(); }
	@Override public boolean isSelected() { return elem().isSelected(); }
	@Override public void clear() { action().clear(); }
	@Override public void sendKeys(CharSequence... arg0) { action().sendKeys(arg0); }
	@Override public void submit() { action().submit(); }

	@Override public Coordinates getCoordinates() { return ((Locatable) elem()).getCoordinates(); }
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		if (name != null)
			sb.append("name: " + name + "; ");
		if (by != null)
			sb.append("by: " + by + "; ");
		return sb.toString();
	}
}
