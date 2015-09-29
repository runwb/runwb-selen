package org.runwb.lib.selen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.runwb.lib.selen.Selen.Page;

public abstract class SelenPageSelect extends Selen.Page.Obj {
	public final Function<WebElement, List<WebElement>> choices;

	public SelenPageSelect(Page page, By by, Function<WebElement, List<WebElement>> choices) {
		super(page, by);
		this.choices = choices;
	}
	public void select(String choice) {
		click();
		for (WebElement c : choices.apply(elem()))
			if (c.getText().equals(choice)) {
				c.click();
				return;
			}
		throw new RuntimeException("choice \"" + choice + "\" not found");
	}
	public void select(int i) {
		click();
		choices.apply(elem()).get(i - 1).click();
	}
	public List<String> choices() {
		click();
		List<String> rv = new ArrayList<>();
		choices.apply(elem()).forEach((e)->rv.add(e.getText()));
		click();
		return rv;
	}
}
