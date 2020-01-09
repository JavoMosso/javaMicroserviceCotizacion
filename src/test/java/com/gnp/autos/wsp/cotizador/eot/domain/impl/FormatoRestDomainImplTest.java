
package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

import com.gnp.autos.wsp.cotizador.eot.client.DescuentoNominaClient;
import com.gnp.autos.wsp.cotizador.eot.client.FormatoImpresionClient;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.descuentonomina.DescuentoNominaResp;
import com.gnp.autos.wsp.cotizador.eot.model.descuentonomina.ResultadoParcialidad;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.negocio.formatoimpresion.model.FormatoImpReq;
import com.gnp.autos.wsp.negocio.neg.model.ElementoNeg;
import com.gnp.autos.wsp.negocio.neg.model.PaqueteNeg;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;
import com.google.gson.Gson;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class FormatoRestDomainImplTest {

    /** The str cotizacion negocio. */
    private String strCotizacionNegocio;

    /** The str cotizacion negocio. */
    private String strFormatoImp;

    /** The str cotizacion negocio. */
    private String strUmoResp;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotizacionNegocio;

    /** The cotizacion negocio. */
    private FormatoImpReq formatoImpReq;

    /** The catalogo domain. */
    @InjectMocks
    private FormatoRestDomainImpl formatoDomain;

    /** The formato imp client. */
    @Mock
    private FormatoImpresionClient fFormatoImpClient;

    /** The descuento nomina client. */
    @Mock
    private DescuentoNominaClient descuentoNominaClient;

    /** The umo service DN. */
    private UmoServiceResp umoServiceDN;

    /**
     * Sets the file.
     *
     * @param myRes the new file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/cotizacionNegocioResult.json")
    public void setFile(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strCotizacionNegocio = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sets the file.
     *
     * @param myRes the new file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/formatoimpResp.json")
    public void setFileFormato(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strFormatoImp = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sets the file.
     *
     * @param myRes the new file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/umoServiceDNResp.json")
    public void setFileUmo(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strUmoResp = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Inits the mocks.
     */
    @Before
    public void initMocks() {
        formatoDomain = new FormatoRestDomainImpl();
        ReflectionTestUtils.setField(formatoDomain, "urlTransaccionIntermedia", "http://tx");
        ReflectionTestUtils.setField(formatoDomain, "urlformatoImpService", "http://fimp");
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Preparar test.
     */
    @Before
    public void prepararTest() {
        Gson gson = new Gson();
        this.cotizacionNegocio = gson.fromJson(strCotizacionNegocio, CotizacionNegocio.class);
        this.formatoImpReq = gson.fromJson(strFormatoImp, FormatoImpReq.class);
        this.umoServiceDN = gson.fromJson(strUmoResp, UmoServiceResp.class);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getFormatoReqTest() {
        when(fFormatoImpClient.getFormatoImp(Matchers.any())).thenReturn(this.formatoImpReq);
        DescuentoNominaResp descNom = new DescuentoNominaResp();
        ResultadoParcialidad resultadoParcialidad = new ResultadoParcialidad();
        resultadoParcialidad.setFecProximoPago("20180101");
        resultadoParcialidad.setMontoParcialidad(new BigDecimal(3456));
        resultadoParcialidad.setNumParcialidades(12);
        descNom.setResultadoParcialidad(resultadoParcialidad);
        when(descuentoNominaClient.getDescuentoNomina(Matchers.any())).thenReturn(descNom);
        when(descuentoNominaClient.getDescuentoNomina(Matchers.any(), Matchers.any())).thenReturn(descNom);

        CotizacionNegocio actual = formatoDomain.getFormatoImp(this.cotizacionNegocio);
        assertNotNull(actual);

        this.cotizacionNegocio.getCotizaNegReq().getPersonas().parallelStream()
                .filter(p -> p.getTipo().equalsIgnoreCase(Constantes.STR_CONTRATANTE))
                .forEach(p -> p.setTipoPersona("M"));
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setAltoRiesgo("1");
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setPctDedAltoRiesgo("15");
        this.cotizacionNegocio.getCotizaNegReq().getPaquetes().get(0).setDescPaquete("");
        this.cotizacionNegocio.getCotizaNegReq().getPaquetes().get(0).setCvePaquete("");
        this.cotizacionNegocio.getCotizaNegReq().getPaquetes().get(1).setDescPaquete("");
        this.cotizacionNegocio.getCotizaNegReq().getPaquetes().get(2).setCvePaquete("");
        this.formatoImpReq.getPaquetes().get(0).setDescPaquete("");
        when(fFormatoImpClient.getFormatoImp(Matchers.any())).thenReturn(this.formatoImpReq);
        this.cotizacionNegocio.getCotizaNegReq().getElementos()
                .add(new ElementoNeg(Constantes.CVE_TIPO_NOMINA, "19", "19"));
        this.cotizacionNegocio.setUmoService(this.umoServiceDN);
        this.cotizacionNegocio.getCotizaNegReq().setViaPago(Constantes.CVE_DESC_NOMINA);
        actual = formatoDomain.getFormatoImp(this.cotizacionNegocio);
        assertNotNull(actual);

        this.cotizacionNegocio.getCotizaNegReq().getElementos()
                .add(new ElementoNeg(Constantes.CVE_TIPO_NOMINA, "7", "7"));
        this.cotizacionNegocio.getCotizaNegReq().setViaPago(Constantes.CVE_BANCARIO_NOMINA);

        actual = formatoDomain.getFormatoImp(this.cotizacionNegocio);
        assertNotNull(actual);

        this.cotizacionNegocio.getCotizaNegReq().setElementos(null);
        this.cotizacionNegocio.setTid(null);

        actual = formatoDomain.getFormatoImp(this.cotizacionNegocio);
        assertNotNull(actual);

    }

    @Test
    public void getPaqueteOrderTest() {
        List<PaqueteNeg> paquetes = new ArrayList<>();
        PaqueteNeg paq = new PaqueteNeg();
        paq.setCvePaquete("PRP0000222");
        paq.setPreferente(false);
        paq.setMtoTotal(5000.0);
        paquetes.add(paq);

        PaqueteNeg paq2 = new PaqueteNeg();
        paq2.setCvePaquete("PRP0000226");
        paquetes.add(paq2);

        PaqueteNeg paq3 = new PaqueteNeg();
        paq3.setCvePaquete("PRP0000221");
        paq3.setPreferente(false);
        paq3.setMtoTotal(1000.0);

        paquetes.add(paq3);

        List<PaqueteNeg> paqlst = formatoDomain.getPaqueteOrder(paquetes);
        paqlst.forEach(System.out::println);
        assertNotNull(paqlst);
    }

}
