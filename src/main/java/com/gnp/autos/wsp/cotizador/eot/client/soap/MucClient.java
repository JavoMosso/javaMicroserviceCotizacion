package com.gnp.autos.wsp.cotizador.eot.client.soap;

import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.JAXBElement;

import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoRequest;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoResponse;
import com.gnp.autos.wsp.cotizador.eot.wsdl.ObjectFactory;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.gnp.autos.wsp.errors.xml.ErrorXML;
import com.gnp.autos.wsp.negocio.log.TransaccionIntermedia;
import com.gnp.autos.wsp.negocio.log.model.ErrorLog;

/**
 * The Class MucClient.
 */
public class MucClient extends WebServiceGatewaySupport {

    /**
     * Gets the calcular.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @param urlTInter         the url T inter
     * @return the calcular
     */
    @SuppressWarnings("unchecked")
    public CalcularPrimaAutoResponse getCalcular(final CotizacionNegocio cotizacionNegocio, final String urlTInter) {

        CalcularPrimaAutoRequest objReq = cotizacionNegocio.getReqMuc();
        JAXBElement<CalcularPrimaAutoResponse> response = null;
        JAXBElement<CalcularPrimaAutoRequest> objCalc = null;
        Timestamp tsReq = new Timestamp(new Date().getTime());
        try {
            ObjectFactory objF = new ObjectFactory();
            objCalc = objF.createCalcularPrimaAutoRequest(objReq);
            Utileria.pintaJAXBToXML(objCalc, Constantes.PRINT_MUC_REQ);
            response = (JAXBElement<CalcularPrimaAutoResponse>) getWebServiceTemplate().marshalSendAndReceive(objCalc,
                    new SoapActionCallback("urn:CalcularPrimaAuto"));

            TransaccionIntermedia.guardarTransaccion(urlTInter, cotizacionNegocio.getTid(), "MU", objCalc, response,
                    getWebServiceTemplate().getDefaultUri(), tsReq, getWebServiceTemplate().getMarshaller());
            Utileria.pintaJAXBToXML(response, Constantes.PRINT_MUC_RESP);
            return response.getValue();
        } catch (SoapFaultClientException ex) {
            logger.info(ex);
            ErrorXML errXML = manejaError(ex.getFaultStringOrReason(), urlTInter, cotizacionNegocio.getTid(), objCalc,
                    tsReq);
            throw new WSPXmlExceptionWrapper(errXML);
        } catch (WebServiceIOException ex) {
            logger.info(ex);
            ErrorXML errXML = manejaError(ex.getMessage(), urlTInter, cotizacionNegocio.getTid(), objCalc, tsReq);
            throw new WSPXmlExceptionWrapper(errXML);
        }

    }

    /**
     * Maneja error.
     *
     * @param msgError  the msg error
     * @param urlTInter the url T inter
     * @param tId       the t id
     * @param objCalc   the obj calc
     * @param tsReq     the ts req
     * @return the error XML
     */
    final ErrorXML manejaError(final String msgError, final String urlTInter, final Integer tId,
            final JAXBElement<CalcularPrimaAutoRequest> objCalc, final Timestamp tsReq) {
        ErrorLog errlog = new ErrorLog();
        String msgErrorT = msgError;
        errlog.setError(msgErrorT);
        TransaccionIntermedia.guardarTransaccionError(urlTInter, tId, "MU", objCalc, errlog,
                getWebServiceTemplate().getDefaultUri(), tsReq, getWebServiceTemplate().getMarshaller());

        if (Utileria.existeValor(tId)) {
            msgErrorT += "(" + tId.toString() + ")";
        }

        ErrorXML errXML = new ErrorXML();
        errXML.setClave(1);
        errXML.setError(msgErrorT);
        errXML.setOrigen("SERVICIO MUC EOT");
        errXML.setNow(new Date());
        return errXML;

    }

}
