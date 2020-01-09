
package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
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
import org.springframework.ws.client.core.WebServiceTemplate;

import com.gnp.autos.wsp.cotizador.eot.client.soap.MucClient;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoRequest;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoResponse;
import com.gnp.autos.wsp.negocio.neg.model.ElementoNeg;
import com.google.gson.Gson;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class MucDomainImplTest {

    /** The str cotizacion negocio. */
    private String strCotizacionNegocio;

    /** The str band resp. */
    private String strCalcularPrimaReq;

    /** The str band resp. */
    private String strCalcularPrimaResp;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotizacionNegocio;

    /** The catalogo domain. */
    @InjectMocks
    private MucDomainImpl mucDomain;

    /** The catalogo client. */
    @Mock
    private MucClient client;

    /** The web service template. */
    @Mock
    private WebServiceTemplate webServiceTemplate;

    /** The cal prima req. */
    private CalcularPrimaAutoRequest calPrimaReq;

    /** The cal prima resp. */
    private CalcularPrimaAutoResponse calPrimaResp;

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
     * Inits the mocks.
     */
    @Before
    public void initMocks() {
        mucDomain = new MucDomainImpl();
        ReflectionTestUtils.setField(mucDomain, "urlTInter", "http://txlogger");
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
        client.setWebServiceTemplate(webServiceTemplate);
        when(webServiceTemplate.marshalSendAndReceive(anyObject())).thenReturn("834972423");
        this.calPrimaResp.getPETICION().get(0).getDATOSPRODUCTOS().get(0).setNOMBREPRODUCTO("AMPLIA-17-17");
        when(client.getCalcular(Matchers.any(), Matchers.any())).thenReturn(this.calPrimaResp);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCotizacionWSPTest() {

        CotizacionNegocio actual = mucDomain.getCalculoMuc(this.cotizacionNegocio);
        assertNotNull(actual);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionErrWSPTest() {

        this.cotizacionNegocio.getUmoService().getDominios().getCobranzas().setCobranzas(null);
        mucDomain.getCalculoMuc(this.cotizacionNegocio);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCotizacionPeriodicidadWSPTest() {
        this.cotizacionNegocio.getCotizaNegReq().setPeriodicidad("A");
        CotizacionNegocio actual = mucDomain.getCalculoMuc(this.cotizacionNegocio);
        assertNotNull(actual);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionPeriodicidadErrWSPTest() {
        // si pogno otra periodicidad truena
        this.cotizacionNegocio.getCotizaNegReq().setPeriodicidad("W");
        mucDomain.getCalculoMuc(this.cotizacionNegocio);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionCobranzaUmoErrWSPTest() {
        this.cotizacionNegocio.getUmoService().getDominios().setCobranzas(null);
        mucDomain.getCalculoMuc(this.cotizacionNegocio);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionCobranzaUmo2ErrWSPTest() {
        this.cotizacionNegocio.getUmoService().getDominios().getCobranzas().setCobranzas(null);
        mucDomain.getCalculoMuc(this.cotizacionNegocio);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionCobranzaUmo3ErrWSPTest() {
        this.cotizacionNegocio.getCotizaNegReq().setViaPago("32432");
        mucDomain.getCalculoMuc(this.cotizacionNegocio);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionConductorErrWSPTest() {
        this.cotizacionNegocio.getCotizaNegReq().getPersonas().forEach(c -> c.setTipo(Constantes.STR_CONTRATANTE));
        mucDomain.getCalculoMuc(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionDerPolErrWSPTest() {
        this.cotizacionNegocio.getUmoService().getDominios().getCobranzas().getCondiciones().getAgrupadorDerechoPoliza()
                .setDerechosPoliza(null);
        mucDomain.getCalculoMuc(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionDerPolNoEmisionErrWSPTest() {
        this.cotizacionNegocio.getUmoService().getDominios().getCobranzas().getCondiciones().getAgrupadorDerechoPoliza()
                .getDerechosPoliza().parallelStream().forEach(c -> c.getTransaccion().setNombre("Otra"));
        mucDomain.getCalculoMuc(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionPaqErrWSPTest() {
        this.cotizacionNegocio.getCotizaNegReq().getPaquetes().forEach(c -> c.setCvePaquete("jgjhgjhgj"));
        mucDomain.getCalculoMuc(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCotizacionTipoCargaWSPTest() {
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setTipoCarga("B");
        CotizacionNegocio actual = mucDomain.getCalculoMuc(this.cotizacionNegocio);
        assertNotNull(actual);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCotizacionDescuentoWSPTest() {
        this.cotizacionNegocio.getRespMuc().getPETICION().get(0).getDATOSPRODUCTOS().parallelStream()
                .forEach(p -> p.getDESCUENTO().removeIf(d -> true));
        CotizacionNegocio actual = mucDomain.getCalculoMuc(this.cotizacionNegocio);
        assertNotNull(actual);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionDescWSPTest() {
        this.cotizacionNegocio.getCotizaNegReq().getDescuentos().forEach(d -> d.setCveDescuento("CSASS"));
        mucDomain.getCalculoMuc(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCotizacionDesc2WSPTest() {
        this.cotizacionNegocio.getCotizaNegReq().getDescuentos().forEach(d -> d.setCveDescuento("PODESCAM"));
        CotizacionNegocio actual = mucDomain.getCalculoMuc(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCotizacionDesc3WSPTest() {
        this.cotizacionNegocio.getCotizaNegReq().setDescuentos(null);
        CotizacionNegocio actual = mucDomain.getCalculoMuc(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionCob1WSPTest() {
        this.cotizacionNegocio.getTransformacionNegocio().getPaquetes().forEach(p -> p.setCvePaquete("rfgfe"));
        mucDomain.getCalculoMuc(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionCob2WSPTest() {
        this.cotizacionNegocio.getCotizaNegReq().getPersonas().forEach(c -> c.setTipo(Constantes.STR_CONDUCTOR));
        mucDomain.getCalculoMuc(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionCob3WSPTest() {
        this.cotizacionNegocio.getTransformacionNegocio().getPaquetes().get(0).getCoberturas()
                .forEach(c -> c.setCveCobertura("768876"));
        mucDomain.getCalculoMuc(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCotizacionElementosWSPTest() {

        this.cotizacionNegocio.getCotizaNegReq().getElementos()
                .add(new ElementoNeg(Constantes.STR_VERSION_NEGOCIO, "18", "18"));
        this.cotizacionNegocio.getCotizaNegReq().getElementos()
                .add(new ElementoNeg(Constantes.STR_DERECHO_POLIZA, "450", "450"));
        this.cotizacionNegocio.getCotizaNegReq().getElementos()
                .add(new ElementoNeg(Constantes.STR_CVE_TARIFA, "1", "1"));
        this.cotizacionNegocio.getCotizaNegReq().getElementos()
                .add(new ElementoNeg(Constantes.STR_FCH_TARIFA, "20180415", "20180415"));
        CotizacionNegocio actual = mucDomain.getCalculoMuc(this.cotizacionNegocio);
        assertNotNull(actual);
    }
}
