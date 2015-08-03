package org.runwb.lib.selen;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public abstract class SelenSelen extends SelenWb {
	public WebDriver driver;
	public class Timeout {
		Deque<Integer> stack = new LinkedList<>();
		int cur = 30;
		{
			stack.push(cur);
		}
		public void pop() {
			cur = stack.pop();
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
}
