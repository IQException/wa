package com.ximalaya.wa.model.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.ximalaya.wa.config.Dict;

@XStreamAlias("MESSAGE")
public class ManageResult {

	@XStreamAlias("DATASET")
	private DataSet common;
	@XStreamAlias("DATASET")
	private DataSet result;
	
	public ManageResult(List<Data> dtcommon,List<Data> dtresults){
		 this.common =new DataSet();
		 common.setName(Dict.DS_COMMOM);
		 common.setDatas(dtcommon);
		 this.result =new DataSet();
		 result.setName(Dict.DS_REPORT);
		 result.setDatas(dtresults);
	}

	public DataSet getCommon() {
		return common;
	}

	public void setCommon(DataSet common) {
		this.common = common;
	}

	public DataSet getResult() {
		return result;
	}

	public void setResult(DataSet result) {
		this.result = result;
	}
	
}
