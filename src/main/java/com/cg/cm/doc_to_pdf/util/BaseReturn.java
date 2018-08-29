package com.cg.cm.doc_to_pdf.util;

public class BaseReturn {

    private Object data;
    private String state = "success";
    private String msg = "转换成功！";

    public BaseReturn(String state, String msg) {
        this.state = state;
        this.msg = msg;
    }
    public BaseReturn(Object data) {
        this.data = data;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
