package com.gnp.autos.wsp.cotizador.eot.client.soap;

import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.JAXBElement;

import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.gnp.autos.wsp.cotizador.eot.foliador.wsdl.Foliador;
import com.gnp.autos.wsp.cotizador.eot.foliador.wsdl.FoliadorResponse;
import com.gnp.autos.wsp.cotizador.eot.foliador.wsdl.ObjectFactory;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.gnp.autos.wsp.errors.xml.ErrorXML;
import com.gnp.autos.wsp.negocio.log.TransaccionIntermedia;
import com.gnp.autos.wsp.negocio.log.model.ErrorLog;

/**
 * The Class FoliadorClient.
 */
public class FoliadorClient extends WebServiceGatewaySupport {

    /**
     * Gets the folio.
     *
     * @param objReq    the obj req
     * @param tid       the tid
     * @param urlTInter the url T inter
     * @return the folio
     */
    @SuppressWarnings("unchecked")
    public FoliadorResponse getFolio(final Foliador objReq, final Integer tid, final String urlTInter) {
        ObjectFactory objF = new ObjectFactory();
        JAXBElement<Foliador> objFolio = objF.createFoliador(objReq);
        JAXBElement<FoliadorResponse> response;
        Timestamp tsReq = new Timestamp(new Date().getTime());
        try {
            response = (JAXBElement<FoliadorResponse>) getWebServiceTemplate().marshalSendAndReceive(objFolio,
                    new SoapActionCallback("urn:Foliador"));
            TransaccionIntermedia.guardarTransaccion(urlTInter, tid, "FL", objFolio, response,
                    getWebServiceTemplate().getDefaultUri(), tsReq, getWebServiceTemplate().getMarshaller());
            return response.getValue();
        } catch (SoapFaultClientException ex) {
            logger.info(ex);
            ErrorXML errXML = manejaError(ex.getFaultStringOrReason(), urlTInter, tid, objFolio, tsReq);
            throw new WSPXmlExceptionWrapper(errXML);
        } catch (WebServiceIOException ex) {
            logger.info(ex);
            ErrorXML errXML = manejaError(ex.getMessage(), urlTInter, tid, objFolio, tsReq);
            throw new WSPXmlExceptionWrapper(errXML);
        }
    }

    /**
     * Maneja error.
     *
     * @param msgError  the msg error
     * @param urlTInter the url T inter
     * @param tId       the t id
     * @param objFolio  the obj folio
     * @param tsReq     the ts req
     * @return the error XML
     */
    final ErrorXML manejaError(final String msgError, final String urlTInter, final Integer tId,
            final JAXBElement<Foliador> objFolio, final Timestamp tsReq) {

        ErrorLog errlog = new ErrorLog();
        String msgErrorT = msgError;
        errlog.setError(msgErrorT);
        TransaccionIntermedia.guardarTransaccionError(urlTInter, tId, "FL", objFolio, errlog,
                getWebServiceTemplate().getDefaultUri(), tsReq, getWebServiceTemplate().getMarshaller());
        if (Utileria.existeValor(tId)) {
            msgErrorT += "(" + tId.toString() + ")";
        }

        ErrorXML wspxml = new ErrorXML();
        wspxml.setClave(1);
        wspxml.setError(msgErrorT);
        wspxml.setOrigen("SERVICIO FOLIADOR EOT");
        wspxml.setNow(new Date());
        return wspxml;

    }
}
