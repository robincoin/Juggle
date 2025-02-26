
package net.somta.juggle.core.model;

import net.somta.juggle.core.enums.RequestTypeEnum;
import java.util.List;

/**
 * 方法类，承载一个Http的请求信息
 * @author husong
 * @since 1.0.0
 */
public class Method {

    private Long suiteId;

    private Long methodId;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求类型 GET POST
     */
    private RequestTypeEnum requestType;

    /**
     * 请求内容类型 application/json
     */
    private String requestContentType;

    /**
     * input parameter schema
     */
    private List<InputParameter> inputParamSchemas;

    /**
     * header的填充规则
     */
    private List<FillStruct> headerFillRules;

    /**
     * 入参的填充规则
     */
    private List<FillStruct> inputFillRules;

    /**
     * 出参的填充规则
     */
    private List<FillStruct> outputFillRules;

    public Long getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(Long suiteId) {
        this.suiteId = suiteId;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequestTypeEnum getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestTypeEnum requestType) {
        this.requestType = requestType;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public List<InputParameter> getInputParamSchemas() {
        return inputParamSchemas;
    }

    public void setInputParamSchemas(List<InputParameter> inputParamSchemas) {
        this.inputParamSchemas = inputParamSchemas;
    }

    public List<FillStruct> getHeaderFillRules() {
        return headerFillRules;
    }

    public void setHeaderFillRules(List<FillStruct> headerFillRules) {
        this.headerFillRules = headerFillRules;
    }

    public List<FillStruct> getInputFillRules() {
        return inputFillRules;
    }

    public void setInputFillRules(List<FillStruct> inputFillRules) {
        this.inputFillRules = inputFillRules;
    }

    public List<FillStruct> getOutputFillRules() {
        return outputFillRules;
    }

    public void setOutputFillRules(List<FillStruct> outputFillRules) {
        this.outputFillRules = outputFillRules;
    }

}
