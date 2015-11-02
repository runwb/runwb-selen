package org.runwb.lib.selen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SelenCapture {
	Selen selen;
	SelenCapture(Selen selen) {
		this.selen = selen;
	}
	public void domRapidFire (double intervalS, int times, Supplier<WebElement> elemSupplier) {
		selen.timeout.override(0.2);
		try {
			long interval = Math.round(intervalS * 1000);
			for (int i=0; i<times; i++) {
				try {
					File file = File.createTempFile("selen dom capture rapid" + i + "_", ".txt");
					PrintWriter pw = new PrintWriter(file);
					WebElement elem = elemSupplier.get();
					String html;
					try {
						html = elem.getAttribute("outerHTML");
					} catch(Exception xn) {
						StringWriter sw = new StringWriter();
						xn.printStackTrace(new PrintWriter(sw));
						html = sw.toString();
					}
	//				String html = selen.findElement(0, By.cssSelector("html")).getAttribute("innerHTML");
					pw.append(html);
					pw.flush();
					pw.close();
					System.out.println(file);
					Thread.sleep(interval);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} finally {
			selen.timeout.reset();
		}
		
	}
	public InputStream screenshot() {
		byte[] s = ((FirefoxDriver)selen.driver).getScreenshotAs(OutputType.BYTES);
		return new ByteArrayInputStream(s);
	}
}
