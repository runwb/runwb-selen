package org.runwb.lib.selen;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.runwb.lib.selen.Selen.Page;
import org.runwb.lib.selen.Selen.Page.Obj;
import org.runwb.lib.selen.Selen.Page.Target;

public abstract class SelenPage {
	
	public WebDriver driver;
//	public String url;
	public String name;
	public boolean highlightObj = false;
//	private boolean bound = false;
	public final Ready ready = new Ready();

	final void bind (WebDriver driver) {
		this.driver = driver;
		if (name == null)
			name = getClass().getSimpleName();
		System.out.println();
		System.out.println("Page:" + this.name + " - checking on page");
		long start = System.currentTimeMillis();
		Selen.Page.Obj o = null;
		try {
			for (Field f : getClass().getFields())
				if (Selen.Page.Obj.class.isAssignableFrom(f.getType())) {
					f.setAccessible(true);
					o = (Selen.Page.Obj) f.get(this);
					if (o != null)
						o.name = f.getName();
				}
			for (Field f : getClass().getFields())
				if (Selen.Page.Obj.class.isAssignableFrom(f.getType())) {
					f.setAccessible(true);
					o = (Selen.Page.Obj) f.get(this);
					if (o != null) {
						Obj.Late late = f.getAnnotation(Obj.Late.class);
						if ((late == null || !late.is()) && (o.late == null || o.late != true)) {
							long startObj = System.currentTimeMillis();
							System.out.println("Obj:" + o.name + " - finding... ");
							o.bind();
							if (o.noElemXn != null)
								throw o.noElemXn;
							System.out.println("Obj:" + o.name + " - found after " + ((float)(System.currentTimeMillis() - startObj) / 1000) + "s");
						}
					}
				}
		} catch (Exception e) {
			throw new Selen.Xn("page not loading with expected obj: " + o, e);
		}
		if (ready.check != null)
			if (!ready.waitFor())
				throw new Selen.Xn("page not ready after timeout of: " + ((float)ready.timeout) + "s");
			
		System.out.println("Page:" + this.name + " - loaded after " + ((float)(System.currentTimeMillis() - start) / 1000) + "s");
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
	public Page.Obj obj(By by) { return new Obj((Selen.Page)this, by); }
	public Page.Obj obj(SearchContext container, By by) { return obj(by).container(container); }
	@Deprecated public Page.Obj obj(By by, Page.Obj.MultiChoose multiChoose) { return obj(by).multiChoose(multiChoose); }
	@Deprecated public Page.Obj obj(SearchContext container, By by, Page.Obj.MultiChoose multiChoose) { return obj(by).container(container).multiChoose(multiChoose); }

	public <P extends Page> Page.Target<P> target(Class<P> target, By by) { return new Target<>((Selen.Page)this, target, by); }
	@Deprecated public <P extends Page> Page.Target<P> target(Class<P> target, SearchContext container, By by) { return target(target, by).container(container); }
	@Deprecated public <P extends Page> Page.Target<P> target(Class<P> target, By by, Page.Obj.MultiChoose multiChoose) { return target(target, by).multiChoose(multiChoose); }
	@Deprecated public <P extends Page> Page.Target<P> target(Class<P> target, SearchContext container, By by, Page.Obj.MultiChoose multiChoose) { return target(target, by).container(container).multiChoose(multiChoose); }

	@Deprecated public Page.Obj late(By by) { return obj(by).late(); }
	@Deprecated public Page.Obj late(SearchContext container, By by) { return obj(by).container(container).late(); }
	@Deprecated public Page.Obj late(By by, Page.Obj.MultiChoose multiChoose) { return obj(by).multiChoose(multiChoose).late(); }
	@Deprecated public Page.Obj late(SearchContext container, By by, Page.Obj.MultiChoose multiChoose) { return obj(by).container(container).multiChoose(multiChoose).late(); }
	
	public Page.Select select(By by, Function<WebElement, List<WebElement>> choices) { return new Page.Select((Selen.Page)this, by, choices); }
	@Deprecated public Page.Select select(SearchContext container, By by, Function<WebElement, List<WebElement>> choices) { return select(by, choices).container(container); }
	@Deprecated public Page.Select select(By by, Page.Obj.MultiChoose multiChoose, Function<WebElement, List<WebElement>> choices) { return select(by, choices).multiChoose(multiChoose); }
	@Deprecated public Page.Select select(SearchContext container, By by, Page.Obj.MultiChoose multiChoose, Function<WebElement, List<WebElement>> choices) { return select(by, choices).container(container).multiChoose(multiChoose); }

}
