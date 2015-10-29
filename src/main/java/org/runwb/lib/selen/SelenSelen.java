package org.runwb.lib.selen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.runwb.lib.selen.Selen.Page;
import org.runwb.lib.selen.Selen.Page.Obj;
import org.runwb.lib.selen.Selen.Page.NullObj;

public abstract class SelenSelen extends SelenWb implements WebDriver, HasInputDevices {
	public WebDriver driver;
	public final SelenSync sync = new SelenSync((Selen)this);
//	Map<Class<? extends Page>, Page> pages = new LinkedHashMap<>();

	public Play play = new Play();
	public static interface Closed { void run(); }
	public class Timeout {
		Deque<Double> stack = new LinkedList<>();
		double cur = 30;
		{
			reset();
		}

		public void pop() {
			cur = stack.pop();
			reset();
		}
		public void push(double seconds) {
			stack.push(cur);
			cur = seconds;
			reset();
		}
		void override(double override) {
			driver.manage().timeouts().implicitlyWait(Math.round(override * 1000), TimeUnit.MILLISECONDS);
		}
		void reset() {
			driver.manage().timeouts().implicitlyWait(Math.round(cur * 1000), TimeUnit.MILLISECONDS);
		}
	}
	public final Timeout timeout;
	public double timeout() { return timeout.cur; }

	SelenSelen(WebDriver driver) {
		this.driver = driver;
		timeout = new Timeout();
	}
	
	public Page.Obj findElement(Integer timeout, By by) {
		if (timeout != null)
			this.timeout.push(timeout);
		Page.Obj obj = findElement(by);
		if (timeout != null)
			this.timeout.pop();
		return obj;
	}
/*	public void bindPage(Page p) { bindPage(p, null); }
	@SuppressWarnings("unchecked")
	public void bindPage(Page p, String url) {
		p.driver = this;
		p.bind(url);
		Class<? extends Page> pCsl = p.getClass();
		while (pCsl.isAnonymousClass())
			pCsl = (Class<? extends Page>) pCsl.getSuperclass();
		pages.put(pCsl, p);
	}*/

	@Deprecated
	public <P extends Page> P bindPage(Class<P> pageCls) {
		return page(pageCls);
	}
	@Deprecated
	public <P extends Page> P bindPage(P p) {
		return page(p);
	}
	@Deprecated
	public <P extends Page> P bindPage(Class<P> pageCls, String url) {
		get(url);
		return page(pageCls);
	}
	public <P extends Page> P page(P p) {
		p.bind(this);
		return p;
	}
	
	public <P extends Page> P page(Class<P> pageCls) {
//		P p = (P) pages.get(pageCls);
//		if (p == null)
//		return (P) bindPage(pageCls);
//		return (P) pages.get(pageCls);
		try {
			Constructor<P> cntr = pageCls.getDeclaredConstructor();
			cntr.setAccessible(true);
			P p = (P) cntr.newInstance();
//			p.driver = this;
			p.bind(this);
			return p;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override public void close() { driver.close(); }
	@Override public Page.Obj findElement(By arg0) {
		try {
			WebElement e = driver.findElement(arg0);
			return new Obj(new Page(){}, e, arg0);
		} catch (NoSuchElementException e) {
			return new NullObj(new Page(){}, e);
		}
	}
	@Override public List<WebElement> findElements(By arg0) { return driver.findElements(arg0); }
	@Override public void get(String arg0) { action(); driver.get(arg0); }
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
	
	public InputStream getScreenshot() {
		byte[] s = ((FirefoxDriver)driver).getScreenshotAs(OutputType.BYTES);
		return new ByteArrayInputStream(s);
	}
	public void capture (int timeoutS, String...strs) {
		long timeout = timeoutS * 1000;
		File[] files = new File[strs.length];
		boolean[] dones = new boolean[strs.length];
		PrintWriter[] pws = new PrintWriter[strs.length];
		for (int i=0; i<strs.length; i++) {
			try {
				files[i] = File.createTempFile("selen capture" + i + "_", ".txt");
				pws[i] = new PrintWriter(files[i]);
				pws[i].println(strs[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		long start = System.currentTimeMillis();
		boolean allDone = false;
		while (System.currentTimeMillis() - start < timeout || allDone) {
			try {
				allDone = true;
				String html = findElement(0, By.cssSelector("html")).getAttribute("innerHTML");
				for (int i=0; i<strs.length; i++) {
					if (!dones[i])
						if (html.contains(strs[i])) {
							pws[i].println((System.currentTimeMillis() - start) / 1000 + "s");
							pws[i].append(html);
							dones[i] = true;
						}
					allDone = allDone && dones[i];
				}
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		
		for (int i=0; i<strs.length; i++) {
			System.out.println(files[i]);
			pws[i].flush();
			pws[i].close();
		}
	}
	public void captureRapid (double intervalS, int times) {
		long interval = Math.round(intervalS * 1000);
		for (int i=0; i<times; i++) {
			try {
				File file = File.createTempFile("selen capture rapid" + i + "_", ".txt");
				PrintWriter pw = new PrintWriter(file);
				String html = findElement(0, By.cssSelector("html")).getAttribute("innerHTML");
				pw.append(html);
				pw.flush();
				pw.close();
				System.out.println(file);
				Thread.sleep(interval);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	void action() {
		if (play.paused()) {
			System.out.println("\"page action paused...");
			StackTraceElement[] stes = Thread.currentThread().getStackTrace();
			for (int i=1; i<stes.length; i++) {
				StackTraceElement ste = stes[i];
				if (!ste.getClassName().startsWith(getClass().getPackage().getName())) {
					System.out.println(ste);
					break;
				}
			}
			play.proceed();
			System.out.println("\"page action continue");
		}
		else
			play.proceed();
	}
}
