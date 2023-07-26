package com.ximalaya.wa.model.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MESSAGE")
public class MonitorResult extends ReportResult {

    public MonitorResult(List<Data> datasCommon, List<Data> datasReport) {
        super(datasCommon, datasReport);
    }

}
