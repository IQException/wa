package com.ximalaya.wa.model.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("CONDITION")
public class Condition {
	@XStreamImplicit(itemFieldName="ITEM")
	private List<Item> items;
	@XStreamImplicit(itemFieldName="CONDITION")
	private List<Condition> conditions;
	
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	public List<Condition> getConditions() {
		return conditions;
	}
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	
	
	
}
