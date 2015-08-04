package org.runwb.lib.selen;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.runwb.lib.selen.Selen.Page;

public abstract class SelenPage {
	
	public class Obj extends SelenObj {
		public Obj(By by) { super(by); }
		public Obj(WebElement elem) { super(elem); }

		@Override public Page page() {
			return (Page) SelenPage.this;
		}
	}
	
	public WebDriver driver;
	public String url;
	public String name;
	public boolean highlightObj = false;
	private boolean bound = false;

	final void bind (String url) {
		if (!bound) {
			bound = true;
			this.url = url;
			if (name == null)
				name = getClass().getSimpleName();
			if (this.url != null)
				driver.get(this.url);
			System.out.println();
			System.out.println("Page:" + this.name + " - checking on page");
			long start = System.currentTimeMillis();
			try {
				for (Field f : getClass().getFields())
					if (f.getType().isAssignableFrom(Selen.Page.Obj.class)) {
						Selen.Page.Obj o = (Selen.Page.Obj) f.get(this);
						if (o != null) {
							o.name = f.getName();
							if (o.late == null || o.late == false) {
								long startObj = System.currentTimeMillis();
								System.out.print("Obj:" + f.getName() + " - looking for... ");
								o.elem = driver.findElement(o.by);
								if (o.elem instanceof Page.NullObj)
									throw ((Page.NullObj) o.elem).exception;
								System.out.println("found after " + ((float)(System.currentTimeMillis() - startObj) / 1000) + "s");
							}
						}
					}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			System.out.println("Page:" + this.name + " - loaded after " + ((float)(System.currentTimeMillis() - start) / 1000) + "s");
		}
	}
	public Map<String, Selen.Page.Obj> objs() {
		try {
			Map<String, Selen.Page.Obj> objs = new LinkedHashMap<>();
			for(Field field : getClass().getFields())
				if (field.getType().isAssignableFrom(Selen.Page.Obj.class))
					objs.put(field.getName(), (Selen.Page.Obj) field.get(this));
			return objs;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void highlightObjs() {
		for (Page.Obj o : objs().values())
			o.highlightObj();
	}
	public Page.Obj obj(By by) {
		return ((Selen.Page)this).new Obj(by);
	}
	public Selen.Page.Obj late(By by) {
		Page.Obj obj = ((Page)this).new Obj(by);
		obj.late = true;
		return obj;
	}
}
