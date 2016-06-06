/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.entitlement.provider.resources;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.ParsingException;
import org.wso2.carbon.identity.entitlement.EntitlementException;
import org.wso2.carbon.identity.entitlement.dto.EntitledResultSetDTO;
import org.wso2.carbon.identity.entitlement.pdp.EntitlementEngine;
import org.wso2.carbon.identity.entitlement.policy.search.PolicySearch;
import org.wso2.carbon.identity.entitlement.provider.EntitlementConstants;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class DecisionResource extends AbstractResource {
    private static Log log = LogFactory.getLog(DecisionResource.class);

    @POST
    @Path("by-xacml")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public String getDecision(@HeaderParam(EntitlementConstants.ACCEPT_HEADER) String format,
                            @HeaderParam(EntitlementConstants.AUTHENTICATION_TYPE_HEADER) String authMechanism,
                            @HeaderParam(EntitlementConstants.AUTHORIZATION_HEADER) String authorization,
                            String xacmlRequest) {

        log.info("recieved :" + xacmlRequest);
        EntitlementEngine entitlementEngine = EntitlementEngine.getInstance();
        try {
            return entitlementEngine.evaluate(xacmlRequest);
        } catch (ParsingException e) {
            log.error("Error occurred while evaluating XACML request", e);
            return "Error occurred while evaluating XACML request";
        } catch (Exception e) {
            log.error("Parse Error occurred while evaluating XACML request", e);
            return "Error occurred while evaluating XACML request";
        }

    }

    @POST
    @Path("by-attrib")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public String getDecisionByAttributes(@HeaderParam(EntitlementConstants.ACCEPT_HEADER) String format,
                              @HeaderParam(EntitlementConstants.AUTHENTICATION_TYPE_HEADER) String authMechanism,
                              @HeaderParam(EntitlementConstants.AUTHORIZATION_HEADER) String authorization,
                              RequestModel request) {
//        log.info(request.getAction());
//        log.info(request.getResource());
//        log.info(request.getEnvironment());

        EntitlementEngine entitlementEngine = EntitlementEngine.getInstance();
        try {
//            log.info("engine output " + entitlementEngine.evaluate("admin","foo",
//                    "read",null));
            return entitlementEngine.evaluate(request.getSubject(),request.getResource(),
                                              request.getAction(),request.getEnvironment());
        } catch (ParsingException e) {
            log.error("Error occurred while evaluating XACML request", e);
            return "Error occurred while evaluating XACML request";
        } catch (Exception e) {
            log.error("Parse Error occurred while evaluating XACML request", e);
            return "Error occurred while evaluating XACML request";
        }

    }

    @POST
    @Path("by-attrib-boolean")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public boolean getBooleanDecision(@HeaderParam(EntitlementConstants.ACCEPT_HEADER) String format,
                                          @HeaderParam(EntitlementConstants.AUTHENTICATION_TYPE_HEADER) String authMechanism,
                                          @HeaderParam(EntitlementConstants.AUTHORIZATION_HEADER) String authorization,
                                          RequestModel request) {

        EntitlementEngine entitlementEngine = EntitlementEngine.getInstance();
        try {
            String response = entitlementEngine.evaluate(request.getSubject(), request.getResource(),
                                                         request.getAction(), null);
            if (response.contains("Permit")) {
                return true;
            }
            return false;
        } catch (ParsingException e) {
            log.error("Error occurred while evaluating XACML request", e);
            return false;
        } catch (Exception e) {
            log.error("Parse Error occurred while evaluating XACML request", e);
            return false;
        }

    }

    @POST
    @Path("entitled-attribs")
    @Produces({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public EntitledAttributesResponseModel getEntitledAttributes(@HeaderParam(EntitlementConstants.ACCEPT_HEADER) String format,
                                                      @HeaderParam(EntitlementConstants.AUTHENTICATION_TYPE_HEADER) String authMechanism,
                                                      @HeaderParam(EntitlementConstants.AUTHORIZATION_HEADER) String authorization,
                                                      EntitledAttributesRequestModel request) {

        if (request.getSubjectName() == null) {
            log.error("Invalid input data - either the user name or role name should be non-null");
            return null;
        }

        try{
            PolicySearch policySearch = EntitlementEngine.getInstance().getPolicySearch();
            EntitledResultSetDTO resultsSet = policySearch.getEntitledAttributes(request.getSubjectName(), request.getResourceName(),
                    request.getSubjectId(), request.getAction(),request.isEnableChildSearch());
            EntitledAttributesResponseModel response = new EntitledAttributesResponseModel();
            response.setEntitledResultSetDTO(resultsSet);
            return response;
        }catch(EntitlementException e){
            log.error("Error occured while retrieving entitled attributes");
            return null;
        }


    }

    @POST
    @Path("entitlements-all")
    @Produces({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public AllEntitlementsResponseModel getAllEntitlements(@HeaderParam(EntitlementConstants.ACCEPT_HEADER) String format,
                                                              @HeaderParam(EntitlementConstants.AUTHENTICATION_TYPE_HEADER) String authMechanism,
                                                              @HeaderParam(EntitlementConstants.AUTHORIZATION_HEADER) String authorization,
                                                              AllEntitlementsRequestModel request) {

        log.info(request.getGivenAttributes()[0].getAttributeId());
        log.info(request.getGivenAttributes()[0].getAttributeDataType());
        log.info(request.getGivenAttributes()[0].getAttributeValue());
        log.info(request.getGivenAttributes()[0].getCategory());

        PolicySearch policySearch = EntitlementEngine.getInstance().getPolicySearch();
        EntitledResultSetDTO resultSet = policySearch.getEntitledAttributes(request.getIdentifier(), request.getGivenAttributes());
        AllEntitlementsResponseModel response = new AllEntitlementsResponseModel();
        response.setEntitledResultSetDTO(resultSet);
        return response;
    }

}
