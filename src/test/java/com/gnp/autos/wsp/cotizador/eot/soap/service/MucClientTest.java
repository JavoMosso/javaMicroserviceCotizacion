package com.gnp.autos.wsp.cotizador.eot.soap.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamSource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.mime.Attachment;
import org.springframework.ws.mime.AttachmentException;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapBodyException;
import org.springframework.ws.soap.SoapEnvelope;
import org.springframework.ws.soap.SoapEnvelopeException;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderException;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.w3c.dom.Document;

import com.gnp.autos.wsp.cotizador.eot.client.soap.MucClient;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoRequest;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoResponse;
import com.gnp.autos.wsp.cotizador.eot.wsdl.ObjectFactory;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class MucClientTest {

    /** The catalogo domain. */
    @InjectMocks
    private MucClient client;

    /** The web service template. */
    @Mock
    private WebServiceTemplate webServiceTemplate;

    private CotizacionNegocio cotizacionNegocio;

    /**
     * Inits the mocks.
     */
    @Before
    public void initMocks() {
        client = new MucClient();
        this.cotizacionNegocio = new CotizacionNegocio();
        CalcularPrimaAutoRequest reqMuc = new CalcularPrimaAutoRequest();
        this.cotizacionNegocio.setReqMuc(reqMuc);
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getServiceWSPReqTest() {

        String urlTInter = "";
        CalcularPrimaAutoResponse t = new CalcularPrimaAutoResponse();
        JAXBElement<CalcularPrimaAutoResponse> response = new ObjectFactory().createCalcularPrimaAutoResponse(t);
        when(webServiceTemplate.marshalSendAndReceive(any(JAXBElement.class), Matchers.any())).thenReturn(response);

        CalcularPrimaAutoResponse resp = client.getCalcular(this.cotizacionNegocio, urlTInter);
        assertNotNull(resp);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     * @return the service exc WSP req test
     */
    @Test(expected = WSPXmlExceptionWrapper.class)
    public void getServiceExcWSPReqTest() {
        String urlTInter = "";
        when(webServiceTemplate.marshalSendAndReceive(any(JAXBElement.class), Matchers.any()))
                .thenThrow(new SoapFaultClientException(getMessageSoa()));
        client.getCalcular(this.cotizacionNegocio, urlTInter);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     * @return the service exc WSP req test
     */
    @Test(expected = WSPXmlExceptionWrapper.class)
    public void getServiceExc2WSPReqTest() {
        String urlTInter = "";
        this.cotizacionNegocio.setTid(12344);
        when(webServiceTemplate.marshalSendAndReceive(any(JAXBElement.class), Matchers.any()))
                .thenThrow(new SoapFaultClientException(getMessageSoa()));
        client.getCalcular(this.cotizacionNegocio, urlTInter);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = WSPXmlExceptionWrapper.class)
    public void getServiceExWSPReqTest() {
        String urlTInter = "";
        when(webServiceTemplate.marshalSendAndReceive(any(JAXBElement.class), Matchers.any()))
                .thenThrow(new WebServiceIOException("Error en el servicio"));
        client.getCalcular(this.cotizacionNegocio, urlTInter);

    }

    /**
     * Gets the message soa.
     *
     * @return the message soa
     */
    public SoapMessage getMessageSoa() {
        return new SoapMessage() {

            @Override
            public boolean hasFault() {
                return true;
            }

            @Override
            public String getFaultReason() {
                return "Esta mal";
            }

            @Override
            public QName getFaultCode() {
                return null;
            }

            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                // TODO Auto-generated method stub

            }

            @Override
            public Source getPayloadSource() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Result getPayloadResult() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isXopPackage() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public Iterator<Attachment> getAttachments() throws AttachmentException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Attachment getAttachment(String contentId) throws AttachmentException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean convertToXopPackage() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public Attachment addAttachment(String contentId, InputStreamSource inputStreamSource, String contentType) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Attachment addAttachment(String contentId, DataHandler dataHandler) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Attachment addAttachment(String contentId, File file) throws AttachmentException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setSoapAction(String soapAction) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setDocument(Document document) {
                // TODO Auto-generated method stub

            }

            @Override
            public SoapVersion getVersion() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public SoapHeader getSoapHeader() throws SoapHeaderException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public SoapBody getSoapBody() throws SoapBodyException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getSoapAction() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public SoapEnvelope getEnvelope() throws SoapEnvelopeException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Document getDocument() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }
}
