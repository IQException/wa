package com.ximalaya.wa.model.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.ximalaya.wa.config.Dict;
@XStreamAlias("MESSAGE")
public class QueryResponse {

	@XStreamAlias("DATASET")
	private DataSet common;
	@XStreamAlias("DATASET")
	private DataSet status;
	@XStreamAlias("DATASET")
	private DataSet result;
	
	public QueryResponse(List<Data> dtcommon,List<Data> dtstatus){
		 this.common =new DataSet();
		 common.setName(Dict.DS_COMMOM);
		 common.setDatas(dtcommon);
		 this.status =new DataSet();
		 status.setName(Dict.DS_QUERY_STATUS);
		 status.setDatas(dtstatus);
	}
	
	public QueryResponse(List<Data> dtcommon,List<Data> dtstatus,List<Data> dtresult){
		 this.common =new DataSet();
		 common.setName(Dict.DS_COMMOM);
		 common.setDatas(dtcommon);
		 this.status =new DataSet();
		 status.setName(Dict.DS_QUERY_STATUS);
		 status.setDatas(dtstatus);
		 this.result = new DataSet();
		 result.setName(Dict.DS_QUERY_RESULT);
		 result.setDatas(dtresult);
	}

	public DataSet getCommon() {
		return common;
	}

	public void setCommon(DataSet common) {
		this.common = common;
	}

	public DataSet getStatus() {
		return status;
	}

	public void setStatus(DataSet status) {
		this.status = status;
	}

	public DataSet getResult() {
		return result;
	}

	public void setResult(DataSet result) {
		this.result = result;
	}
	
}
