package com.ximalaya.wa.model.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.ximalaya.wa.config.Dict;

@XStreamAlias("MESSAGE")
public class ReportResult {
	@XStreamAlias("DATASET")
	private DataSet common;
	@XStreamAlias("DATASET")
	private DataSet report;
	public ReportResult(List<Data> datasCommon,List<Data> datasReport){
		 this.common =new DataSet();
		 common.setName(Dict.DS_COMMOM);
		 common.setDatas(datasCommon);
		 this.report =new DataSet();
		 report.setName(Dict.DS_REPORT);
		 report.setDatas(datasReport);
	}
	public DataSet getCommon() {
		return common;
	}
	public void setCommon(DataSet common) {
		this.common = common;
	}
	public DataSet getReport() {
		return report;
	}
	public void setReport(DataSet report) {
		this.report = report;
	}

}
