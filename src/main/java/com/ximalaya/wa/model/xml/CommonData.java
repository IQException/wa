package com.ximalaya.wa.model.xml;

import java.util.List;

public class CommonData {

    private List<Data> commonDatas;
    private String filePath;
    private String fileName;

    public List<Data> getCommonDatas() {
        return commonDatas;
    }
    public void setCommonDatas(List<Data> commonDatas) {
        this.commonDatas = commonDatas;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
