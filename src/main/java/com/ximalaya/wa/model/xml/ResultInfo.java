package com.ximalaya.wa.model.xml;

public class ResultInfo<T> {

    
    private T result;
    private String fileName;
    private int recordNum;

    
    public T getResult() {
        return result;
    }
    public void setResult(T result) {
        this.result = result;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public int getRecordNum() {
        return recordNum;
    }
    public void setRecordNum(int recordNum) {
        this.recordNum = recordNum;
    }
    
}
