package org.orcid.frontend.web.controllers;

import java.util.Map;

import jakarta.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;

import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice(assignableTypes = PublicRecordApiController.class)
public class PublicRecordApiExceptionHandler {

    private static final String LOCATION_HEADER = "location";
    private static final String PRIMARY_ORCID_HEADER = "x-orcid-primary";

    @Resource
    private OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleThrowable(Throwable t, HttpServletRequest request) {
        if (OrcidDeprecatedException.class.isAssignableFrom(t.getClass())) {
            return handleDeprecated((OrcidDeprecatedException) t, request);
        }
        Object orcidError = orcidCoreExceptionMapper.getOrcidError(t, OrcidCoreExceptionMapper.V3);
        int statusCode = ((OrcidError) orcidError).getResponseCode();
        return ResponseEntity.status(statusCode).body(orcidError);
    }

    private ResponseEntity<Object> handleDeprecated(OrcidDeprecatedException t, HttpServletRequest request) {
        Object orcidError = orcidCoreExceptionMapper.getOrcidError(t, OrcidCoreExceptionMapper.V3);
        Map<String, String> params = t.getParams();

        int statusCode = Response.Status.MOVED_PERMANENTLY.getStatusCode();
        String method = request.getMethod();
        if (method != null && !"GET".equals(method)) {
            statusCode = Response.Status.CONFLICT.getStatusCode();
            OrcidError v3Error = (OrcidError) orcidError;
            orcidError = orcidCoreExceptionMapper.getDeprecatedOrcidErrorV3(v3Error.getErrorCode(), statusCode, params);
        }

        HttpHeaders headers = new HttpHeaders();
        if (params != null && params.containsKey(OrcidDeprecatedException.ORCID)) {
            String primaryOrcid = OrcidStringUtils.getOrcidNumber(params.get(OrcidDeprecatedException.ORCID));
            if (primaryOrcid != null) {
                headers.add(PRIMARY_ORCID_HEADER, primaryOrcid);
            }
            String location = getPrimaryRecordLocation(request, params);
            if (location != null) {
                headers.add(LOCATION_HEADER, location);
            }
        }
        return ResponseEntity.status(statusCode).headers(headers).body(orcidError);
    }

    private String getPrimaryRecordLocation(HttpServletRequest request, Map<String, String> params) {
        String deprecatedOrcid = OrcidStringUtils.getOrcidNumber(request.getRequestURI());
        String primaryOrcid = OrcidStringUtils.getOrcidNumber(params.get(OrcidDeprecatedException.ORCID));
        String originalRequest = request.getRequestURL().toString();
        if (OrcidUrlManager.isSecure(request)) {
            if (originalRequest.startsWith("http:")) {
                originalRequest = originalRequest.replaceFirst("http:", "https:");
            }
        }
        return originalRequest.replace(deprecatedOrcid, primaryOrcid);
    }
}

