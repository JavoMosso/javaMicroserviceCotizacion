package com.gnp.autos.wsp.cotizador.eot.services;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.gnp.autos.wsp.cotizador.eot.domain.CotizacionDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.gnp.autos.wsp.errors.xml.ErrorXML;
import com.gnp.autos.wsp.negocio.cotizacion.model.req.TraductorReq;
import com.gnp.autos.wsp.negocio.cotizacion.model.resp.TraductorResp;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegResp;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class CotizaServiceTest {

    /** The catalogo domain. */
    @InjectMocks
    private CotizaService cotizaService;

    @Mock
    CotizacionDomain cotizaDomain;

    /** The str cot req. */
    private String strCotReq;

    /** The cot req. */
    private TraductorReq cotReq;

    /**
     * Inits the mocks.
     */
    @Before
    public void initMocks() {
        cotizaService = new CotizaService();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Sets the file cat resp.
     *
     * @param myRes the new file cat resp
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/cotizacionWSPReq.xml")
    public void setFileCatResp(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strCotReq = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Preparar test.
     */
    @Before
    public void prepararTest() {
        this.cotReq = Utileria.unmarshalXml(TraductorReq.class, strCotReq);
        CotizacionNegocio resp = new CotizacionNegocio();
        CotizaNegResp cotizaNegResp = new CotizaNegResp();
        resp.setCotizaNegResp(cotizaNegResp);
        TraductorResp traductorResp = new TraductorResp();
        resp.setTraductorResp(traductorResp);
        when(cotizaDomain.getCotizacion(Matchers.any())).thenReturn(resp);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getServiceWSPReqTest() {
        TraductorResp resp2 = cotizaService.cotizar(this.cotReq, null);
        assertNotNull(resp2);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getServiceNegWSPReqTest() {
        CotizaNegReq cotReq = new CotizaNegReq();
        CotizaNegResp resp = cotizaService.cotizaMuc(cotReq, null);
        assertNotNull(resp);
    }

    @Test(expected = WSPXmlExceptionWrapper.class)
    public void getServiceNegWSPReqErr1Test() {
        ErrorXML err = new ErrorXML();
        WSPXmlExceptionWrapper wsp = new WSPXmlExceptionWrapper(err);
        when(cotizaDomain.getCotizacion(Matchers.any())).thenThrow(wsp);
        TraductorResp resp = cotizaService.cotizar(this.cotReq, null);
        assertNotNull(resp);
    }

    @Test(expected = ExecutionError.class)
    public void getServiceNegWSPReqErr2Test() {
        ExecutionError wsp = new ExecutionError(0);
        when(cotizaDomain.getCotizacion(Matchers.any())).thenThrow(wsp);
        TraductorResp resp = cotizaService.cotizar(this.cotReq, null);
        assertNotNull(resp);
    }

    @Test(expected = WSPXmlExceptionWrapper.class)
    public void getServiceNegWSPReqErr3Test() {
        ErrorXML err = new ErrorXML();
        WSPXmlExceptionWrapper wsp = new WSPXmlExceptionWrapper(err);
        when(cotizaDomain.getCotizacion(Matchers.any())).thenThrow(wsp);
        TraductorResp resp = cotizaService.cotizar(this.cotReq, 1);
        assertNotNull(resp);
    }

    @Test(expected = ExecutionError.class)
    public void getServiceNegWSPReqErr4Test() {
        ExecutionError wsp = new ExecutionError(1, "hubo un erorr");
        when(cotizaDomain.getCotizacion(Matchers.any())).thenThrow(wsp);
        TraductorResp resp = cotizaService.cotizar(this.cotReq, 1);
        assertNotNull(resp);
    }

}
