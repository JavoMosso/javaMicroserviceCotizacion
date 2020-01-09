package com.gnp.autos.wsp.cotizador.eot.client.soap;

import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;

import org.apache.log4j.Logger;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.MBException;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ObjectFactory;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ServicioReglasAutosCotizadorRequest;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ServicioReglasAutosCotizadorResponse;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.gnp.autos.wsp.errors.xml.ErrorXML;
import com.gnp.autos.wsp.negocio.log.TransaccionIntermedia;
import com.gnp.autos.wsp.negocio.log.model.ErrorLog;

/**
 * The Class RulesClient.
 */
public class RulesClient extends WebServiceGatewaySupport {

    /**
     * Gets the rules.
     *
     * @param objReq    the obj req
     * @param tid       the tid
     * @param urlTInter the url T inter
     * @return the rules
     */
    @SuppressWarnings("unchecked")
    public ServicioReglasAutosCotizadorResponse getRules(final ServicioReglasAutosCotizadorRequest objReq,
            final Integer tid, final String urlTInter) {
        ObjectFactory objF = new ObjectFactory();
        JAXBElement<ServicioReglasAutosCotizadorRequest> objRules = objF
                .createServicioReglasAutosCotizadorRequest(objReq);
        JAXBElement<ServicioReglasAutosCotizadorResponse> response;
        Timestamp tsReq = new Timestamp(new Date().getTime());

        try {
            Utileria.pintaJAXBToXML(objRules, Constantes.PRINT_RG_REQ);
            response = (JAXBElement<ServicioReglasAutosCotizadorResponse>) getWebServiceTemplate()
                    .marshalSendAndReceive(objRules,
                            new SoapActionCallback("http://gnp.com.mx/esb/autos/reglas/ServicioReglasAutosCotizador"));

            TransaccionIntermedia.guardarTransaccion(urlTInter, tid, "RG", objRules, response,
                    getWebServiceTemplate().getDefaultUri(), tsReq, getWebServiceTemplate().getMarshaller());
            Utileria.pintaJAXBToXML(response, Constantes.PRINT_RG_RESP);
            return response.getValue();
        } catch (SoapFaultClientException sf) {
            logger.info(sf);
            MBException mbE = errorInterp(sf);
            ErrorXML errXML = manejaError(mbE.getException() + "->" + mbE.getCompensation(), urlTInter, tid, objRules,
                    tsReq);
            throw new WSPXmlExceptionWrapper(errXML);
        } catch (WebServiceIOException ex) {
            logger.info(ex);
            ErrorXML errXML = manejaError(ex.getMessage(), urlTInter, tid, objRules, tsReq);
            throw new WSPXmlExceptionWrapper(errXML);
        }
    }

    /**
     * Error interp.
     *
     * @param sf the sf
     * @return the MB exception
     */
    private MBException errorInterp(final SoapFaultClientException sf) {
        Logger.getRootLogger().info(sf);
        MBException mbEx = null;
        try {
            SoapFaultDetail dtls = sf.getSoapFault().getFaultDetail();
            SoapFaultDetailElement detailElementChld = dtls.getDetailEntries().next();
            Source detailSrc = detailElementChld.getSource();
            @SuppressWarnings("unchecked")
            JAXBElement<MBException> detail = (JAXBElement<MBException>) getWebServiceTemplate().getUnmarshaller()
                    .unmarshal(detailSrc);
            mbEx = detail.getValue();
            return mbEx;
        } catch (Exception ex1) {
            logger.info(ex1);
            throw new ExecutionError(Constantes.ERROR_37, ex1.getMessage());
        }
    }

    /**
     * Maneja error.
     *
     * @param msgError  the msg error
     * @param urlTInter the url T inter
     * @param tid       the tid
     * @param objRules  the obj rules
     * @param tsReq     the ts req
     * @return the error XML
     */
    final ErrorXML manejaError(final String msgError, final String urlTInter, final Integer tid,
            final JAXBElement<ServicioReglasAutosCotizadorRequest> objRules, final Timestamp tsReq) {

        ErrorLog errlog = new ErrorLog();
        String msgErrorT = msgError;
        errlog.setError(msgErrorT);
        TransaccionIntermedia.guardarTransaccionError(urlTInter, tid, "RG", objRules, errlog,
                getWebServiceTemplate().getDefaultUri(), tsReq, getWebServiceTemplate().getMarshaller());

        if (Utileria.existeValor(tid)) {
            msgErrorT += "(" + tid.toString() + ")";
        }

        ErrorXML wspxml = new ErrorXML();
        wspxml.setClave(1);
        wspxml.setError(msgErrorT);
        wspxml.setOrigen("SERVICIO REGLAS EOT");
        wspxml.setNow(new Date());
        return wspxml;

    }

}
