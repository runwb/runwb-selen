package org.runwb.lib.selen;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.runwb.lib.selen.Selen.Page;

public abstract class SelenObj implements WebElement, Locatable {
	public abstract Selen.Page page();
	WebElement elem;
	public WebElement elem() {
		if (late != null && late == true) {
			late = false;
			WebElement e = page().driver.findElement(by);
			if (e instanceof Page.NullObj)
				throw ((Page.NullObj) elem).exception;
			if (e instanceof Page.Obj)
				e = ((Page.Obj) e).elem;
			elem = e;
		}
		return elem;
	}
	public String name;
	public final By by;
	Boolean late = null;
	public SelenObj(By by) {
		this.by = by;
	}
	public SelenObj(WebElement elem) {
		this.elem = elem;
		by = null;
	}
	
	public void type(String txt) {
		if (page().highlightObj)
			highlightObj();
		System.out.println("Obj:" + name + " - about to type " + txt);
		elem().clear();
		elem().sendKeys(txt);
	}
	public void click() {
		if (page().highlightObj)
			highlightObj();
		elem().click();
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
		return isNull(this);
	}
	public NoSuchElementException getNotFoundException() {
		return isNull() ? ((Page.NullObj) this).exception : null;
	}
	public void mouseTo() {
		new Actions(page().driver).moveToElement(this).perform();
	}
	
	
	@Override public void clear() { elem().clear(); }
	@Override public Selen.Page.Obj findElement(By arg0) {
		try {
			WebElement e = elem().findElement(arg0);
			return page().new Obj(e);
		} catch (NoSuchElementException e) {
			return page().new NullObj(e);
		}
	}
	@Override public List<WebElement> findElements(By arg0) {
		List<WebElement> list = elem().findElements(arg0);
		if (list == null)
			return null;
		List<WebElement> newList = new ArrayList<>();
		for (WebElement e : list)
			newList.add(page().new Obj(e));
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
	@Override public void sendKeys(CharSequence... arg0) { elem().sendKeys(arg0); }
	@Override public void submit() { elem().submit(); }

	@Override public Coordinates getCoordinates() { return ((Locatable) elem()).getCoordinates(); }
}
