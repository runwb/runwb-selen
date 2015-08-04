package org.runwb.lib.selen;

import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.runwb.lib.selen.Selen.Page;

public abstract class SelenSelen extends SelenWb implements WebDriver, HasInputDevices {
	public WebDriver driver;
	Map<Class<? extends Page>, Page> pages = new LinkedHashMap<>();
	
	public class Timeout {
		Deque<Integer> stack = new LinkedList<>();
		int cur = 30;
		{
			stack.push(cur);
		}
		public void pop() {
			stack.pop();
			cur = stack.peek();
			driver.manage().timeouts().implicitlyWait(cur, TimeUnit.SECONDS);
			
		}
		public void push(int seconds) {
			cur = seconds;
			stack.push(seconds);
			driver.manage().timeouts().implicitlyWait(cur, TimeUnit.SECONDS);
		}
	}
	public Timeout timeout = new Timeout();
	public int timeout() { return timeout.stack.peek(); }

	SelenSelen(WebDriver driver) {
		driver(driver);
	}
	
	public void driver(WebDriver driver) {
		this.driver = driver;
		this.driver.manage().timeouts().implicitlyWait(timeout.stack.peek(), TimeUnit.SECONDS);
	}
	
	public WebElement findElement(Integer timeout, By by) {
		if (timeout != null)
			this.timeout.push(timeout);
		WebElement res = null;
		try {
			res = driver.findElement(by);
		} catch (Exception e) {
			res = null;
		}
		if (timeout != null)
			this.timeout.pop();
		return res;
	}
	public <P extends Page> P bindPage(Class<P> pageCls) {
		return bindPage(pageCls, null);
	}
	public <P extends Page> P bindPage(Class<P> pageCls, String url) {
		P p = Selen.newPage(pageCls, this, url);
		pages.put(pageCls, p);
		return p;
	}
	@SuppressWarnings("unchecked")
	public <P extends Page> P page(Class<P> pageCls) {
		return (P) pages.get(pageCls);
	}
	
	@Override public void close() { driver.close(); }
	@Override public WebElement findElement(By arg0) { return driver.findElement(arg0); }
	@Override public List<WebElement> findElements(By arg0) { return driver.findElements(arg0); }
	@Override public void get(String arg0) { driver.get(arg0); }
	@Override public String getCurrentUrl() { return driver.getCurrentUrl(); }
	@Override public String getPageSource() { return driver.getPageSource(); }
	@Override public String getTitle() { return driver.getTitle(); }
	@Override public String getWindowHandle() { return driver.getWindowHandle(); }
	@Override public Set<String> getWindowHandles() { return driver.getWindowHandles(); }
	@Override public Options manage() { return driver.manage(); }
	@Override public Navigation navigate() { return driver.navigate(); }
	@Override public void quit() { driver.quit(); }
	@Override public TargetLocator switchTo() { return driver.switchTo(); }

	@Override public Keyboard getKeyboard() { return ((HasInputDevices) driver).getKeyboard(); }
	@Override public Mouse getMouse() { return ((HasInputDevices) driver).getMouse(); }
}
