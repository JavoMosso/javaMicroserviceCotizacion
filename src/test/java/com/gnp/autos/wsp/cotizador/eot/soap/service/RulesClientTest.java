package com.gnp.autos.wsp.cotizador.eot.soap.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Locale;

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
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.mime.Attachment;
import org.springframework.ws.mime.AttachmentException;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapBodyException;
import org.springframework.ws.soap.SoapEnvelope;
import org.springframework.ws.soap.SoapEnvelopeException;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.SoapFaultException;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderException;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.w3c.dom.Document;

import com.gnp.autos.wsp.cotizador.eot.client.soap.RulesClient;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.MBException;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ObjectFactory;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ServicioReglasAutosCotizadorRequest;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ServicioReglasAutosCotizadorResponse;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class RulesClientTest {

    /** The catalogo domain. */
    @InjectMocks
    private RulesClient client;

    /** The web service template. */
    @Mock
    private WebServiceTemplate webServiceTemplate;

    @Mock
    private Jaxb2Marshaller marshaller;

    /**
     * Inits the mocks.
     */
    @Before
    public void initMocks() {
        client = new RulesClient();
        // webServiceTemplate.setUnmarshaller(marshaller);
        // webServiceTemplate.setMarshaller(marshaller);
        // client.setDefaultUri("http:ss");
        // client.setMarshaller(marshaller);
        webServiceTemplate.setDefaultUri("http://algo");
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getServiceWSPReqTest() {
        ServicioReglasAutosCotizadorRequest objReq = new ServicioReglasAutosCotizadorRequest();
        Integer tid = 245;
        String urlTInter = "";
        ServicioReglasAutosCotizadorResponse t = new ServicioReglasAutosCotizadorResponse();
        JAXBElement<ServicioReglasAutosCotizadorResponse> response = new ObjectFactory()
                .createServicioReglasAutosCotizadorResponse(t);
        when(webServiceTemplate.marshalSendAndReceive(any(JAXBElement.class), Matchers.any())).thenReturn(response);

        ServicioReglasAutosCotizadorResponse resp = client.getRules(objReq, tid, urlTInter);
        assertNotNull(resp);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     * @return the service exc WSP req test
     * @throws IOException
     * @throws XmlMappingException
     */
    @Test(expected = WSPXmlExceptionWrapper.class)
    public void getServiceExcWSPReqTest() throws XmlMappingException, IOException {
        ServicioReglasAutosCotizadorRequest objReq = new ServicioReglasAutosCotizadorRequest();
        Integer tid = null;
        String urlTInter = "";
        when(webServiceTemplate.marshalSendAndReceive(any(JAXBElement.class), Matchers.any()))
                .thenThrow(new SoapFaultClientException(getMessageSoa()));
        MBException mbE = new MBException();
        mbE.setDescription("Error mb");
        JAXBElement<MBException> detail = new ObjectFactory().createMbException(mbE);

        marshaller.setContextPath("com.gnp.autos.wsp.cotizador.eot.wsdl");
        client.getWebServiceTemplate().setMarshaller(marshaller);
        client.getWebServiceTemplate().setUnmarshaller(marshaller);

        when(webServiceTemplate.getUnmarshaller()).thenReturn(marshaller);
        when(marshaller.unmarshal(Matchers.any())).thenReturn(detail);
        client.getRules(objReq, tid, urlTInter);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     * @return the service exc WSP req test
     */
    @Test(expected = ExecutionError.class)
    public void getServiceExc2WSPReqTest() {
        ServicioReglasAutosCotizadorRequest objReq = new ServicioReglasAutosCotizadorRequest();
        Integer tid = 245;
        String urlTInter = "";
        when(webServiceTemplate.marshalSendAndReceive(any(JAXBElement.class), Matchers.any()))
                .thenThrow(new SoapFaultClientException(getMessageSoa()));
        client.getRules(objReq, tid, urlTInter);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = WSPXmlExceptionWrapper.class)
    public void getServiceExWSPReqTest() {
        ServicioReglasAutosCotizadorRequest objReq = new ServicioReglasAutosCotizadorRequest();
        Integer tid = 245;
        String urlTInter = "";
        when(webServiceTemplate.marshalSendAndReceive(any(JAXBElement.class), Matchers.any()))
                .thenThrow(new WebServiceIOException("Error en el servicio"));
        client.getRules(objReq, tid, urlTInter);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = WSPXmlExceptionWrapper.class)
    public void getService2ExWSPReqTest() {
        ServicioReglasAutosCotizadorRequest objReq = new ServicioReglasAutosCotizadorRequest();
        Integer tid = null;
        String urlTInter = "";
        when(webServiceTemplate.marshalSendAndReceive(any(JAXBElement.class), Matchers.any()))
                .thenThrow(new WebServiceIOException("Error en el servicio"));
        client.getRules(objReq, tid, urlTInter);

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
                return new SoapBody() {

                    @Override
                    public void removeAttribute(QName name) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public Source getSource() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public QName getName() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public String getAttributeValue(QName name) {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public Iterator<QName> getAllAttributes() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public void addNamespaceDeclaration(String prefix, String namespaceUri) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void addAttribute(QName name, String value) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public boolean hasFault() {
                        // TODO Auto-generated method stub
                        return false;
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
                    public SoapFault getFault() {

                        return new SoapFault() {

                            @Override
                            public void removeAttribute(QName name) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public Source getSource() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public QName getName() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String getAttributeValue(QName name) {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Iterator<QName> getAllAttributes() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public void addNamespaceDeclaration(String prefix, String namespaceUri) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void addAttribute(QName name, String value) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void setFaultActorOrRole(String faultActor) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public String getFaultStringOrReason() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public SoapFaultDetail getFaultDetail() {

                                return new SoapFaultDetail() {

                                    @Override
                                    public void removeAttribute(QName name) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public Source getSource() {

                                        return new Source() {

                                            @Override
                                            public void setSystemId(String systemId) {
                                                // TODO Auto-generated method stub

                                            }

                                            @Override
                                            public String getSystemId() {
                                                // TODO Auto-generated method stub
                                                return "1";
                                            }
                                        };
                                    }

                                    @Override
                                    public QName getName() {
                                        // TODO Auto-generated method stub
                                        return null;
                                    }

                                    @Override
                                    public String getAttributeValue(QName name) {
                                        // TODO Auto-generated method stub
                                        return null;
                                    }

                                    @Override
                                    public Iterator<QName> getAllAttributes() {
                                        // TODO Auto-generated method stub
                                        return null;
                                    }

                                    @Override
                                    public void addNamespaceDeclaration(String prefix, String namespaceUri) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void addAttribute(QName name, String value) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public Result getResult() {
                                        // TODO Auto-generated method stub
                                        return null;
                                    }

                                    @Override
                                    public Iterator<SoapFaultDetailElement> getDetailEntries() {
                                        Iterator<SoapFaultDetailElement> lstElem = new Iterator<SoapFaultDetailElement>() {

                                            @Override
                                            public SoapFaultDetailElement next() {

                                                return new SoapFaultDetailElement() {

                                                    @Override
                                                    public void removeAttribute(QName name) {
                                                        // TODO Auto-generated method stub

                                                    }

                                                    @Override
                                                    public Source getSource() {
                                                        // TODO Auto-generated method stub

                                                        return new Source() {

                                                            @Override
                                                            public void setSystemId(String systemId) {
                                                                // TODO Auto-generated method stub

                                                            }

                                                            @Override
                                                            public String getSystemId() {
                                                                StringBuilder s = new StringBuilder();
                                                                s.append(
                                                                        "<NS2:mbException xmlns:NS2='http://gnp.com.mx/esb/frameworkesb/mbException/MBException'>");
                                                                s.append("<errorCode>server</errorCode>");
                                                                s.append(
                                                                        "<description>Error Servicio Autos Cotizador</description>");
                                                                s.append("<errorActor/>");
                                                                s.append(
                                                                        "<exception>Error Servicio Autos Cotizador</exception>");
                                                                s.append("</NS2:mbException>");

                                                                return s.toString();
                                                            }
                                                        };
                                                    }

                                                    @Override
                                                    public QName getName() {
                                                        // TODO Auto-generated method stub
                                                        return null;
                                                    }

                                                    @Override
                                                    public String getAttributeValue(QName name) {
                                                        // TODO Auto-generated method stub
                                                        return null;
                                                    }

                                                    @Override
                                                    public Iterator<QName> getAllAttributes() {
                                                        // TODO Auto-generated method stub
                                                        return null;
                                                    }

                                                    @Override
                                                    public void addNamespaceDeclaration(String prefix,
                                                            String namespaceUri) {
                                                        // TODO Auto-generated method stub

                                                    }

                                                    @Override
                                                    public void addAttribute(QName name, String value) {
                                                        // TODO Auto-generated method stub

                                                    }

                                                    @Override
                                                    public Result getResult() {
                                                        // TODO Auto-generated method stub
                                                        return null;
                                                    }

                                                    @Override
                                                    public void addText(String text) {
                                                        // TODO Auto-generated method stub

                                                    }
                                                };
                                            }

                                            @Override
                                            public boolean hasNext() {
                                                // TODO Auto-generated method stub
                                                return false;
                                            }
                                        };
                                        return lstElem;
                                    }

                                    @Override
                                    public SoapFaultDetailElement addFaultDetailElement(QName name) {
                                        // TODO Auto-generated method stub
                                        return null;
                                    }
                                };
                            }

                            @Override
                            public QName getFaultCode() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String getFaultActorOrRole() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public SoapFaultDetail addFaultDetail() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                        };
                    }

                    @Override
                    public SoapFault addVersionMismatchFault(String faultStringOrReason, Locale locale)
                            throws SoapFaultException {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public SoapFault addServerOrReceiverFault(String faultStringOrReason, Locale locale)
                            throws SoapFaultException {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public SoapFault addMustUnderstandFault(String faultStringOrReason, Locale locale)
                            throws SoapFaultException {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public SoapFault addClientOrSenderFault(String faultStringOrReason, Locale locale)
                            throws SoapFaultException {
                        // TODO Auto-generated method stub
                        return null;
                    }
                };

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
