package org.runwb.lib.selen;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.runwb.lib.selen.Selen.Page;

public abstract class SelenPage {
	
	public WebDriver driver;
	public String url;
	public String name;
	public boolean highlightObj = false;
	private boolean bound = false;
	public final Ready ready = new Ready();

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
			Selen.Page.Obj o = null;
			try {
				for (Field f : getClass().getFields())
					if (f.getType().isAssignableFrom(Selen.Page.Obj.class)) {
						o = (Selen.Page.Obj) f.get(this);
						if (o != null)
							o.name = f.getName();
					}
				for (Field f : getClass().getFields())
					if (f.getType().isAssignableFrom(Selen.Page.Obj.class)) {
						o = (Selen.Page.Obj) f.get(this);
						if (o != null)
							if (o.late == null || o.late == false)
								o.bind();
					}
			} catch (Exception e) {
				throw new Selen.Xn("page not loading with expected obj: " + o, e);
			}
			if (ready.check != null)
				if (!ready.waitFor())
					throw new Selen.Xn("page not ready after timeout of: " + ((float)ready.timeout) + "s");
				
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
	public Page.Obj obj(By by) { return obj(null, by, null); }
	public Page.Obj obj(Page.Obj container, By by) { return obj(container, by, null); }
	public Page.Obj obj(By by, Page.Obj.MultiChoose multiChoose) { return obj(null, by, multiChoose); }
	public Page.Obj obj(Page.Obj container, By by, Page.Obj.MultiChoose multiChoose) { return ((Selen.Page)this).new Obj(container, by, multiChoose); }

	public Selen.Page.Obj late(By by) { return late(null, by, null); }
	public Selen.Page.Obj late(Page.Obj container, By by) { return late(container, by, null); }
	public Selen.Page.Obj late(By by, Page.Obj.MultiChoose multiChoose) { return late(null, by, multiChoose); }
	public Selen.Page.Obj late(Page.Obj container, By by, Page.Obj.MultiChoose multiChoose) {
		Page.Obj obj = obj(container, by, multiChoose);
		obj.late = true;
		return obj;
	}
}
