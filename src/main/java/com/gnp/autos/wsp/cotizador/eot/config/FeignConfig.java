package com.gnp.autos.wsp.cotizador.eot.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnp.autos.wsp.cotizador.eot.error.ErrorUmoService;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.gnp.autos.wsp.errors.xml.ErrorXML;
import com.gnp.autos.wsp.negocio.config.CustomRetryFeign;
import com.google.common.io.CharStreams;

import feign.Response;
import feign.Retryer;
import feign.codec.ErrorDecoder;

/**
 * The Class FeignConfig.
 */
@Configuration
public class FeignConfig {
    /** The connect timeout. */
    @Value("${feign.client.config.default.connectTimeout}")
    private Integer connectTimeout;

    /**
     * Retryer.
     *
     * @return the retryer
     */
    @Bean
    public Retryer retryer() {
        return new CustomRetryFeign(connectTimeout, 1);
    }

    /**
     * Error decoder WSP.
     *
     * @return the error decoder
     */
    @Bean
    public ErrorDecoder errorDecoderWSP() {
        return (method, response) -> {
            String text = getHttpError(response);
            if (response.status() == HttpStatus.SC_BAD_REQUEST
                    || response.status() == HttpStatus.SC_NOT_FOUND && method.contains("UmoServiceClient")) {
                if (response.headers().get("content-type").contains("application/xml")) {
                    parserXMLError(text);
                } else {
                    parserJsonError(text, method);
                }
            } else if (response.status() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                if (response.headers().get("content-type").contains("application/xml")) {
                    throw new ExecutionError(Constantes.ERROR_37,
                            "Error " + text + response.status() + "(" + method + ")");
                } else {
                    parserJsonError(text, method);
                }
            }
            throw new ExecutionError(Constantes.ERROR_37, "Error " + text + response.status() + "(" + method + ")");

        };
    }

    /**
     * Gets the http error.
     *
     * @param response the response
     * @return the http error
     * @throws ExecutionError the execution error
     */
    private static String getHttpError(final Response response) {
        String text = null;
        if (response.body() != null) {
            try (Reader reader = new InputStreamReader(response.body().asInputStream())) {
                text = CharStreams.toString(reader);
            } catch (IOException e1) {
                Logger.getRootLogger().error(e1);
                throw new ExecutionError(Constantes.ERROR_37, e1.getMessage());
            }
        }
        return text;
    }

    /**
     * Parser XML error.
     *
     * @param text the text
     * @throws ExecutionError the execution error
     */
    private void parserXMLError(final String text) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(ErrorXML.class);
            ErrorXML errXML = (ErrorXML) jaxbContext.createUnmarshaller().unmarshal(new StringReader(text));
            throw new WSPXmlExceptionWrapper(errXML);
        } catch (JAXBException e) {
            Logger.getRootLogger().error(e);
            throw new ExecutionError(Constantes.ERROR_37, e.getMessage());
        }
    }

    /**
     * Parser json error.
     *
     * @param text   the text
     * @param method the method
     * @throws ExecutionError the execution error
     */
    private void parserJsonError(final String text, final String method) {
        ObjectMapper om = new ObjectMapper();
        try {
            if (method.contains("UmoServiceClient")) {
                ErrorUmoService objErrGJson = om.readValue(text, ErrorUmoService.class);
                ErrorXML objErrWSP = new ErrorXML();
                objErrWSP.setClave(1);
                objErrWSP.setError(objErrGJson.getError() + "->" + objErrGJson.getMessage());
                objErrWSP.setOrigen("UMO SERVICES");
                objErrWSP.setNow(new Date());
                throw new WSPXmlExceptionWrapper(objErrWSP);
            } else {
                ErrorXML objErrJson = om.readValue(text, ErrorXML.class);
                if (objErrJson.getOrigen().equalsIgnoreCase(Constantes.RUNTIME)) {
                    objErrJson.setOrigen(method);
                }
                throw new WSPXmlExceptionWrapper(objErrJson);
            }
        } catch (IOException e) {
            Logger.getRootLogger().error(e);
            throw new ExecutionError(Constantes.ERROR_37, e.getMessage());
        }
    }
}