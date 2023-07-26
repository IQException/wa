package com.ximalaya.wa.model.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("ITEM")
public class Item {

    @XStreamAsAttribute
    private String key;
    @XStreamAsAttribute
    private String val;
    @XStreamAsAttribute
    private String fmt;
    @XStreamAsAttribute
    private String chn;
    @XStreamAsAttribute
    private String eng;
    @XStreamAsAttribute
    private String rmk;
    @XStreamAsAttribute
    private String datatype;
    @XStreamAsAttribute
    private String length;
    @XStreamAsAttribute
    private String nullable;
    @XStreamAsAttribute
    private String primarykey;
    @XStreamAsAttribute
    private String alias;
    @XStreamAsAttribute
    @XStreamAlias("default")
    private String defaultValue;
    @XStreamAsAttribute
    private String dstkey;
    @XStreamAsAttribute
    private String sort;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getFmt() {
        return fmt;
    }

    public void setFmt(String fmt) {
        this.fmt = fmt;
    }

    public String getChn() {
        return chn;
    }

    public void setChn(String chn) {
        this.chn = chn;
    }

    public String getEng() {
        return eng;
    }

    public void setEng(String eng) {
        this.eng = eng;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public String getPrimarykey() {
        return primarykey;
    }

    public void setPrimarykey(String primarykey) {
        this.primarykey = primarykey;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDstkey() {
        return dstkey;
    }

    public void setDstkey(String dstkey) {
        this.dstkey = dstkey;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
    public static  Builder Builder(){
        return new Builder();
    }
    public static class Builder {
        private Item item = new Item();

        public Builder key(String key) {
            item.key = key;
            return this;
        }

        public Builder val(String val) {
            item.val = val;
            return this;
        }

        public Builder fmt(String fmt) {
            item.fmt = fmt;
            return this;

        }
        
        public Item build(){
            return item;
        }
    }
}
