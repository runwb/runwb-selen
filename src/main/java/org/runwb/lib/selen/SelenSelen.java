package org.runwb.lib.selen;

import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.runwb.lib.selen.Selen.Page;

public abstract class SelenSelen extends SelenWb implements WebDriver, HasInputDevices {
	public WebDriver driver;
	Map<Class<? extends Page>, Page> pages = new LinkedHashMap<>();
	
	public static interface Closed { void run(); }
	public class Timeout {
		Deque<Integer> stack = new LinkedList<>();
		int cur = 30;
		public void pop() {
			cur = stack.pop();
			driver.manage().timeouts().implicitlyWait(cur, TimeUnit.SECONDS);
		}
		public void push(int seconds) {
			stack.push(cur);
			cur = seconds;
			driver.manage().timeouts().implicitlyWait(cur, TimeUnit.SECONDS);
		}
		public void oneoff(int seconds, Closed exec) {
			stack.push(seconds);
			Exception hold = null;
			try {
				exec.run();
			} catch (Exception e) {
				hold = e;
			}
			stack.pop();
			if (hold != null)
				throw new RuntimeException(hold);
		}
	}
	public Timeout timeout = new Timeout();
	public int timeout() { return timeout.stack.peek(); }

	SelenSelen(WebDriver driver) {
		driver(driver);
	}
	
	public void driver(WebDriver driver) {
		this.driver = driver;
		this.driver.manage().timeouts().implicitlyWait(timeout.cur, TimeUnit.SECONDS);
	}
	
	public Page.Obj findElement(Integer timeout, By by) {
		if (timeout != null)
			this.timeout.push(timeout);
		Page.Obj obj = findElement(by);
		if (timeout != null)
			this.timeout.pop();
		return obj;
	}
	public void bindPage(Page p) { bindPage(p, null); }
	@SuppressWarnings("unchecked")
	public void bindPage(Page p, String url) {
		p.driver = this;
		p.bind(url);
		Class<? extends Page> pCsl = p.getClass();
		while (pCsl.isAnonymousClass())
			pCsl = (Class<? extends Page>) pCsl.getSuperclass();
		pages.put(pCsl, p);
	}

	public void bindPage(Class<? extends Page> pageCls) { bindPage(pageCls, null); }
	public void bindPage(Class<? extends Page> pageCls, String url) {
		bindPage(Selen.newPage(pageCls, this, url));
	}
	@SuppressWarnings("unchecked")
	public <P extends Page> P page(Class<P> pageCls) {
		return (P) pages.get(pageCls);
	}
	
	@Override public void close() { driver.close(); }
	@Override public Page.Obj findElement(By arg0) {
		try {
			WebElement e = driver.findElement(arg0);
			return new Page(){}.new Obj(e);
		} catch (NoSuchElementException e) {
			return new Page(){}.new NullObj(e);
		}
	}
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
