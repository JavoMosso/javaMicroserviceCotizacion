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
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.MBException;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.ObjectFactory;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.ServicioReglasAutosProductosRequest;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.ServicioReglasAutosProductosResponse;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.gnp.autos.wsp.errors.xml.ErrorXML;
import com.gnp.autos.wsp.negocio.log.TransaccionIntermedia;
import com.gnp.autos.wsp.negocio.log.model.ErrorLog;

/**
 * The Class RulesClient.
 */
public class RulesProdClient extends WebServiceGatewaySupport {

    /**
     * Gets the rules.
     *
     * @param objReq    the obj req
     * @param tid       the tid
     * @param urlTInter the url T inter
     * @return the rules
     */
    @SuppressWarnings("unchecked")
    public ServicioReglasAutosProductosResponse getRulesProd(final ServicioReglasAutosProductosRequest objReq,
            final Integer tid, final String urlTInter) {
        ObjectFactory objF = new ObjectFactory();
        JAXBElement<ServicioReglasAutosProductosRequest> objRules = objF
                .createServicioReglasAutosProductosRequest(objReq);
        JAXBElement<ServicioReglasAutosProductosResponse> response;
        Timestamp tsReq = new Timestamp(new Date().getTime());

        try {
            Utileria.pintaJAXBToXML(objRules, Constantes.PRINT_RG_REQ);
            response = (JAXBElement<ServicioReglasAutosProductosResponse>) getWebServiceTemplate()
                    .marshalSendAndReceive(objRules,
                            new SoapActionCallback("http://gnp.com.mx/esb/autos/reglas/ServicioReglasAutosProductos"));

            TransaccionIntermedia.guardarTransaccion(urlTInter, tid, "RP", objRules, response,
                    getWebServiceTemplate().getDefaultUri(), tsReq, getWebServiceTemplate().getMarshaller());
            Utileria.pintaJAXBToXML(response, Constantes.PRINT_RG_RESP);
            return response.getValue();
        } catch (SoapFaultClientException sf) {
            logger.info(sf);
            MBException mbE = errorInterpProd(sf);
            ErrorXML errXML = manejaErrorProd(mbE.getException() + "->" + mbE.getCompensation(), urlTInter, tid,
                    objRules, tsReq);
            throw new WSPXmlExceptionWrapper(errXML);
        } catch (WebServiceIOException ex) {
            logger.info(ex);
            ErrorXML errXML = manejaErrorProd(ex.getMessage(), urlTInter, tid, objRules, tsReq);
            throw new WSPXmlExceptionWrapper(errXML);
        }
    }

    /**
     * Error interp.
     *
     * @param sfCE the sf
     * @return the MB exception
     */
    private MBException errorInterpProd(final SoapFaultClientException sfCE) {
        Logger.getRootLogger().info(sfCE);
        MBException mbEx = null;
        try {
            SoapFaultDetail dtlsProd = sfCE.getSoapFault().getFaultDetail();
            SoapFaultDetailElement detailElementChld = dtlsProd.getDetailEntries().next();
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
     * @param msgError     the msg error
     * @param urlTInter    the url T inter
     * @param tid          the tid
     * @param objRulesProd the obj rules
     * @param tsReq        the ts req
     * @return the error XML
     */
    final ErrorXML manejaErrorProd(final String msgError, final String urlTInter, final Integer tid,
            final JAXBElement<ServicioReglasAutosProductosRequest> objRulesProd, final Timestamp tsReq) {

        ErrorLog errlogProd = new ErrorLog();
        String msgErrorT = msgError;
        errlogProd.setError(msgErrorT);
        TransaccionIntermedia.guardarTransaccionError(urlTInter, tid, "RP", objRulesProd, errlogProd,
                getWebServiceTemplate().getDefaultUri(), tsReq, getWebServiceTemplate().getMarshaller());

        if (Utileria.existeValor(tid)) {
            msgErrorT += "(" + tid.toString() + ")";
        }

        ErrorXML wspxmlProd = new ErrorXML();
        wspxmlProd.setClave(1);
        wspxmlProd.setError(msgErrorT);
        wspxmlProd.setOrigen("SERVICIO REGLAS EOT");
        wspxmlProd.setNow(new Date());
        return wspxmlProd;

    }

}
