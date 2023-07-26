package com.ximalaya.wa.model.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("DATA")
public class Data {
	@XStreamImplicit(itemFieldName="ITEM")
	private List<Item> items;
	@XStreamAlias("CONDITION")
	private Condition condition;
	@XStreamImplicit(itemFieldName="DATASET")
	private List<DataSet> dataSets;
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	public Condition getCondition() {
		return condition;
	}
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	public List<DataSet> getDataSets() {
		return dataSets;
	}
	public void setDataSets(List<DataSet> dataSets) {
		this.dataSets = dataSets;
	}
	
}
