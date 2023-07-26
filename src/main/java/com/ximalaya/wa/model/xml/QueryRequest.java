package com.ximalaya.wa.model.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
@XStreamAlias("MESSAGE")
public class QueryRequest {
	@XStreamImplicit(itemFieldName="DATASET")
	private List<DataSet> dataSets;
	public QueryRequest(){}
	public List<DataSet> getDataSets() {
		return dataSets;
	}
	public void setDataSets(List<DataSet> dataSets) {
		this.dataSets = dataSets;
	}
	
}
 