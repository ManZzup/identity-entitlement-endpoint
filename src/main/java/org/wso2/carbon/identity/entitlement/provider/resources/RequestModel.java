package org.wso2.carbon.identity.entitlement.provider.resources;

import javax.xml.bind.annotation.*;

/**
 * Created by manujith on 5/22/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "subject",
        "action",
        "resource",
        "environment"
})
@XmlRootElement(name="DecisionRequest")
public class RequestModel {
    @XmlElement(required = false)
    private String subject;
    @XmlElement(required = false)
    private String action;
    @XmlElement(required = false)
    private String resource;
    @XmlElement(required = false)
    private String[] environment;



    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String[] getEnvironment() {
        return environment;
    }

    public void setEnvironment(String[] environment) {
        this.environment = environment;
    }
}

