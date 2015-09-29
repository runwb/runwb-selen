package org.runwb.lib.selen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import org.runwb.lib.selen.Selen.Page.Obj;
import org.runwb.lib.selen.Selen.Page.NullObj;

public abstract class SelenPageObj implements WebElement, Locatable {
	final WebElement elem;
	NoSuchElementException noElemXn;
	public String name;
	public final Page page;
	public final By by;
	public MultiChoose multiChoose;
	public SearchContext container;
	public Boolean late = null;
	@SuppressWarnings("unchecked")
	public <O extends Obj> O multiChoose(MultiChoose multiChoose) { this.multiChoose = multiChoose; return (O)this; }
	@SuppressWarnings("unchecked")
	public <O extends Obj> O container(SearchContext container) { this.container = container; return (O)this; }
	@SuppressWarnings("unchecked")
	public <O extends Obj> O late() { this.late = true; return (O)this; }

	public NoSuchElementException noElemXn() {
		elem();
		return noElemXn;
	}
	public WebElement elem() {
		if (elem == null)
			return bind();
		return elem;
	}
	WebElement action() {
		WebElement e = elem();
		WebDriver wd = page.driver;
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
		long start = System.currentTimeMillis();
		if (Boolean.TRUE.equals(late))
			System.out.println("Obj:" + name + " - finding... ");
		WebElement e;
		SearchContext container = this.container;
		if (container == null)
			container = page.driver;
		if (multiChoose == null)
			try {
				e = container.findElement(by);
			} catch (NoSuchElementException xn) {
				e = new NullObj(page, xn);
			}
		else {
			List<WebElement> candidates = container.findElements(by);
			if (candidates.size() > 0)
				e = multiChoose.pick(candidates);
			else
				e = new NullObj(page, null);
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

	public SelenPageObj(Page page, WebElement elem, By by) {
		this.page = page;
		this.by = by;
		this.elem = elem;
	}

/*	public SelenPageObj(Page page, SearchContext container, By by, MultiChoose multichoose) {
		this.page = page;
		this.container = container;
		this.by = by;
		this.multiChoose = multichoose;
	}*/
	public SelenPageObj(Page page, By by) {
		this.page = page;
		this.by = by;
		this.elem = null;
	}

	public void type(String txt) {
		WebElement e = action();
		if (page.highlightObj)
			highlightObj();
		System.out.println("Obj:" + name + " - about to type " + txt);
		e.clear();
		e.sendKeys(txt);
	}
	public void click() {
		WebElement e = action();
		if (page.highlightObj)
			highlightObj();
		e.click();
		System.out.println("Obj:" + name + " - about to click it");
	}
	public void highlightObj() {
		System.out.println("highlighting obj:" + name);
		JavascriptExecutor js = (JavascriptExecutor) page.driver;
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
		new Actions(page.driver).moveToElement(this).perform();
	}
	
	@Override public Page.Obj findElement(By arg0) {
		try {
			return new Obj(page, elem().findElement(arg0), arg0);
		} catch (NoSuchElementException e) {
			return new NullObj(page, e);
		}
	}
	public Page.Obj findElement(Integer timeout, By arg0) {
		WebDriver driver = page.driver;
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
			newList.add(new Obj(page, e, arg0));
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
/*	public <P extends Page> P go() {
		throw new UnsupportedOperationException();
	}*/
	public boolean exists(double timeoutS) {
		return exists(0.2, timeoutS, true);
	}
	public boolean exists(double intervalS, double timeoutS, boolean pub) {
		WebDriver driver = page.driver;
		Selen selen = null;
		if (driver instanceof Selen)
			selen = (Selen) driver;
		if (selen != null)
			selen.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS);
		
		try {
			long interval = Math.round(intervalS * 1000);
			long timeout = Math.round(timeoutS * 1000);
	
			
			long start = System.currentTimeMillis();
			boolean first = true;
			if (pub) {
				System.out.println("check obj exist...");
				StackTraceElement[] stes = Thread.currentThread().getStackTrace();
				String pkgNm = getClass().getPackage().getName();
				for (int i=1; i<stes.length; i++) {
					StackTraceElement ste = stes[i];
					if (!ste.getClassName().startsWith(pkgNm)) {
						System.out.println(ste);
						break;
					}
				}
			}
			int times = 0;
			boolean res = false;
			while (System.currentTimeMillis() - start <= timeout) {
				if (first)
					first = false;
				else
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				boolean exists;
				try {
					exists = elem() != null;
				} catch (Exception xn) {
					exists = false;
				}
				times++;
				if (exists) {
					res = true;
					break;
				}
			}
			if (res)
				System.out.println("obj exists suceeded!!! - after " + times + " trys (" + Math.round(interval / 1000) + "s interval)");
			else
				System.out.println("obj exists failed!!! - timing out after " + Math.round(timeout / 1000) + "s");
			return res;
		}
		finally {
			if (selen != null)
				selen.manage().timeouts().implicitlyWait(selen.timeout(), TimeUnit.SECONDS);
		}
	}
}
