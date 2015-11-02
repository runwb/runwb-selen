package org.runwb.lib.selen;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.runwb.lib.selen.Selen.Page;
import org.runwb.lib.selen.Selen.Page.Obj;
import org.runwb.lib.selen.Selen.Page.Obj.PickFromList;

public abstract class SelenPage {
	
	public WebDriver driver;
	public String name;
	public boolean highlightObj = false;
	public final Ready ready = new Ready();

	final void bind (WebDriver driver) {
		bind(driver, null);
	}
	final void bind (WebDriver driver, Double readyCheckTimeoutS) {
		this.driver = driver;
		if (name == null)
			name = name(((Selen.Page)this).getClass());
		System.out.println();
		System.out.println("Page:" + this.name + " - checking on page");
		long start = System.currentTimeMillis();
		Obj o = null;
		try {
			for (Field f : getClass().getFields())
				if (Obj.class.isAssignableFrom(f.getType())) {
					f.setAccessible(true);
					o = (Obj) f.get(this);
					if (o != null)
						o.name = f.getName();
				}
			for (Field f : getClass().getFields())
				if (Obj.class.isAssignableFrom(f.getType())) {
					f.setAccessible(true);
					o = (Obj) f.get(this);
					if (o != null) {
						if (!o.late) {
							long startObj = System.currentTimeMillis();
							System.out.println("Obj:" + o.name + " - finding... ");
							o.bind();
							if (o.xnNoElem != null)
								throw o.xnNoElem;
							System.out.println("Obj:" + o.name + " - found after " + ((float)(System.currentTimeMillis() - startObj) / 1000) + "s");
						}
					}
				}
		} catch (UnhandledAlertException xnAlert) {
			//TODO simplify
			try {
				throw new Selen.Xn.UnexpectedAlert(driver.switchTo().alert());
			} catch (Exception xn) {
				throw new Selen.Xn.UnexpectedAlert(xnAlert);
			}
		} catch (Exception e) {
			throw new Selen.Xn.PageUnavailable("page not loading with expected obj: " + o, e);
		}
		if (ready.check != null)
			if (!ready.waitFor(readyCheckTimeoutS))
				throw new Selen.Xn.PageUnavailable("page not ready after timeout of: " + ((float)ready.timeoutS) + "s", null);
			
		System.out.println("Page:" + this.name + " - loaded after " + ((float)(System.currentTimeMillis() - start) / 1000) + "s");
	}
	public static String name(Class<? extends Page> pgCls) {
		String name = pgCls.getSimpleName();
		Class<?> cls = pgCls.getEnclosingClass();
		while (cls != null) {
			name = cls.getSimpleName() + "." + name;
			cls = cls.getEnclosingClass();
		}
		return name;
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
//	public Page.Obj obj(SearchContext container, By by) { return obj(by).inside(container); }

//	@SuppressWarnings("unchecked")
//	public <P extends Page> Page.Target<P> target(Class<P> target, By by) { return new Target<>((P)this, target, by); }
	
	public Page.Select select(By by, Function<WebElement, List<WebElement>> choices) { return new Page.Select((Selen.Page)this, by, choices); }

	public static PickFromList pickByText(String txt) {
		return (list)-> {
			for (WebElement e : list)
				if (e.getText().equals(txt))
					return e;
			return null;
		};
	}
}
