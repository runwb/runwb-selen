package org.runwb.lib.selen;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public abstract class SelenObj {
	public Selen.Page pg;
	public WebElement elem;
	public String name;
	public final By by;
	public SelenObj(By by) {
		this.by = by;
	}
	public void type(String txt) {
		if (pg.highlightObj)
			highlightObj();
		System.out.println("Obj:" + name + " - about to type " + txt);
		elem.clear();
		elem.sendKeys(txt);
	}
	public void click() {
		if (pg.highlightObj)
			highlightObj();
		elem.click();
		System.out.println("Obj:" + name + " - about to click it");
	}
	public void highlightObj() {
		System.out.println("highlighting obj:" + name);
		JavascriptExecutor js = (JavascriptExecutor) pg.driver;
		js.executeScript(
				"arguments[0].setAttribute('style', arguments[1]);",
				elem, "color: red; border: 3px solid red;");
		JOptionPane.showMessageDialog(null, name);
		js.executeScript(
				"arguments[0].setAttribute('style', arguments[1]);",
				elem, "");				
	}

}
