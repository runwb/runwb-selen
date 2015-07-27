package org.runwb.lib.selen;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.runwb.lib.selen.Selen.Obj;

public abstract class SelenPage {
	public WebDriver driver;
	public String url;
	public String name;
	public boolean highlightObj = false;
	private boolean initialized = false;

	@SuppressWarnings("unchecked")
	final <P extends Selen.Page> P bind (WebDriver driver, String url) {
		if (!initialized) {
			initialized = true;
			this.driver = driver;
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
					if (f.getType().isAssignableFrom(Selen.Obj.class)) {
						Selen.Obj o = (Obj) f.get(this);
						if (o != null) {
							long startObj = System.currentTimeMillis();
							System.out.print("Obj:" + f.getName() + " - looking for... ");
							o.elem = driver.findElement(o.by);
							o.name = f.getName();
							o.pg = (Selen.Page) this;
							System.out.println("found after " + ((float)(System.currentTimeMillis() - startObj) / 1000) + "s");
						}
					}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			System.out.println("Page:" + this.name + " - loaded after " + ((float)(System.currentTimeMillis() - start) / 1000) + "s");
		}
		return (P)this;
	}
	public Map<String, Selen.Obj> objs() {
		try {
			Map<String, Selen.Obj> objs = new LinkedHashMap<>();
			for(Field field : getClass().getFields())
				if (field.getType().isAssignableFrom(Selen.Obj.class))
					objs.put(field.getName(), (Selen.Obj) field.get(this));
			return objs;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void highlightObjs() {
		for (Selen.Obj o : objs().values())
			o.highlightObj();
	}
}
