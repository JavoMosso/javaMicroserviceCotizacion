package com.gnp.autos.wsp.cotizador.eot.domain.impl;

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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StreamUtils;

import com.gnp.autos.wsp.cotizador.eot.client.UmoServiceClient;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoRequest;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoResponse;
import com.gnp.autos.wsp.negocio.neg.model.ElementoNeg;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;
import com.google.gson.Gson;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class UmoServiceDomainImplTest {

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
    private UmoServiceImpl umoDomain;

    /** The catalogo client. */
    @Mock
    private UmoServiceClient umoClient;

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
        umoDomain = new UmoServiceImpl();
        ReflectionTestUtils.setField(umoDomain, "urlTransaccionIntermedia", "http://tx");
        ReflectionTestUtils.setField(umoDomain, "urlUmoService", "http://umo");
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

        UmoServiceResp resp = this.cotizacionNegocio.getUmoService();
        when(umoClient.getUmoService(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any()))
                .thenReturn(resp);
        when(umoClient.getUmoService(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(),
                Matchers.any())).thenReturn(resp);

        UmoServiceResp actual = umoDomain.getDatosNegocio(this.cotizacionNegocio);
        assertNotNull(actual);

        // sin codigo de promocion
        this.cotizacionNegocio.getCotizaNegReq().setCodigoPromocion("");
        actual = umoDomain.getDatosNegocio(this.cotizacionNegocio);
        assertNotNull(actual);

        this.cotizacionNegocio.getCotizaNegReq().getPersonas()
                .forEach(p -> p.setTipoPersona(Constantes.TIPO_PERSONA_MORAL));
        actual = umoDomain.getDatosNegocio(this.cotizacionNegocio);
        assertNotNull(actual);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getTransfWSPVersionReqTest() {

        UmoServiceResp resp = this.cotizacionNegocio.getUmoService();
        when(umoClient.getUmoServiceVersion(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(),
                Matchers.any(), Matchers.any())).thenReturn(resp);
        when(umoClient.getUmoServiceVersionCodPromo(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(),
                Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(resp);

        this.cotizacionNegocio.getCotizaNegReq().getElementos()
                .add(new ElementoNeg(Constantes.STR_VERSION_NEGOCIO, "18", "18"));
        this.cotizacionNegocio.getCotizaNegReq().getElementos()
                .add(new ElementoNeg(Constantes.STR_DERECHO_POLIZA, "450", "450"));
        this.cotizacionNegocio.getCotizaNegReq().getElementos()
                .add(new ElementoNeg(Constantes.STR_CVE_TARIFA, "1", "1"));
        this.cotizacionNegocio.getCotizaNegReq().getElementos()
                .add(new ElementoNeg(Constantes.STR_FCH_TARIFA, "20180415", "20180415"));

        UmoServiceResp actual = umoDomain.getDatosNegocio(this.cotizacionNegocio);
        assertNotNull(actual);

        // sin codigo de promocion
        this.cotizacionNegocio.getCotizaNegReq().setCodigoPromocion("");
        actual = umoDomain.getDatosNegocio(this.cotizacionNegocio);
        assertNotNull(actual);

        this.cotizacionNegocio.getCotizaNegReq().getPersonas()
                .forEach(p -> p.setTipoPersona(Constantes.TIPO_PERSONA_MORAL));
        actual = umoDomain.getDatosNegocio(this.cotizacionNegocio);
        assertNotNull(actual);

    }

}
