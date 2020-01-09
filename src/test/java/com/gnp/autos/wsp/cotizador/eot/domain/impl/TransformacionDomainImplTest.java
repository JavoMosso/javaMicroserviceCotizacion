package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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

import com.gnp.autos.wsp.cotizador.eot.client.TransformacionClient;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.transformacion.TransformacionNeg;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoRequest;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoResponse;
import com.google.gson.Gson;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class TransformacionDomainImplTest {

    /** The str cotizacion negocio. */
    private String strCotizacionNegocio;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotizacionNegocio;

    /** The str band resp. */
    private String strCalcularPrimaReq;

    /** The str band resp. */
    private String strCalcularPrimaResp;

    /** The catalogo domain. */
    @InjectMocks
    private TransformacionDomainImpl transformacionDomain;

    /** The catalogo client. */
    @Mock
    private TransformacionClient transfClient;

    /** The cal prima req. */
    private CalcularPrimaAutoRequest calPrimaReq;

    /** The cal prima resp. */
    private CalcularPrimaAutoResponse calPrimaResp;

    /**
     * Sets the file cat resp.
     *
     * @param myRes the new file cat resp
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/calcularPrimaReq.xml")
    public void setFileCalculaPrimaReq(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strCalcularPrimaReq = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sets the file cat resp.
     *
     * @param myRes the new file cat resp
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/calcularPrimaResp.xml")
    public void setFileCalculaPrimaResp(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strCalcularPrimaResp = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sets the file.
     *
     * @param myRes the new file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/cotizacionNegocioReglas.json")
    public void setFile(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strCotizacionNegocio = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Inits the mocks.
     */
    @Before
    public void initMocks() {
        transformacionDomain = new TransformacionDomainImpl();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Preparar test.
     */
    @Before
    public void prepararTest() {
        Gson gson = new Gson();
        this.cotizacionNegocio = gson.fromJson(strCotizacionNegocio, CotizacionNegocio.class);
        this.calPrimaReq = Utileria.unmarshalXmlPaq(CalcularPrimaAutoRequest.class, strCalcularPrimaReq);
        this.calPrimaResp = Utileria.unmarshalXmlPaq(CalcularPrimaAutoResponse.class, strCalcularPrimaResp);
        this.cotizacionNegocio.setReqMuc(calPrimaReq);
        this.cotizacionNegocio.setRespMuc(calPrimaResp);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getTransfWSPReqTest() {

        TransformacionNeg resp = new TransformacionNeg();
        resp.setPaquetes(new ArrayList<>());
        resp.setVariablesTrans(new ArrayList<>());
        when(transfClient.getTransformacion(Matchers.any(), Matchers.any())).thenReturn(resp);
        when(transfClient.getTransformacion(Matchers.any())).thenReturn(resp);

        TransformacionNeg actual = transformacionDomain.getVariablesTransformacion(this.cotizacionNegocio);
        assertNotNull(actual);

        this.cotizacionNegocio.setTid(null);
        actual = transformacionDomain.getVariablesTransformacion(this.cotizacionNegocio);
        assertNotNull(actual);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getTransPaquetefErrWSPReqTest() {

        TransformacionNeg resp = new TransformacionNeg();
        resp.setPaquetes(new ArrayList<>());
        resp.setVariablesTrans(new ArrayList<>());
        when(transfClient.getTransformacion(Matchers.any(), Matchers.any())).thenReturn(resp);
        when(transfClient.getTransformacion(Matchers.any())).thenReturn(resp);

        this.cotizacionNegocio.getUmoService().getDominios().getCombinaciones().get(0).getPaquetes()
                .forEach(c -> c.setProductoPersonalizado("ddddd"));
        transformacionDomain.getVariablesTransformacion(this.cotizacionNegocio);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getTransCondfErrWSPReqTest() {

        TransformacionNeg resp = new TransformacionNeg();
        resp.setPaquetes(new ArrayList<>());
        resp.setVariablesTrans(new ArrayList<>());
        when(transfClient.getTransformacion(Matchers.any(), Matchers.any())).thenReturn(resp);
        when(transfClient.getTransformacion(Matchers.any())).thenReturn(resp);

        this.cotizacionNegocio.getCotizaNegReq().getPersonas().forEach(p -> p.setTipo(Constantes.STR_CONTRATANTE));
        transformacionDomain.getVariablesTransformacion(this.cotizacionNegocio);

    }

}
