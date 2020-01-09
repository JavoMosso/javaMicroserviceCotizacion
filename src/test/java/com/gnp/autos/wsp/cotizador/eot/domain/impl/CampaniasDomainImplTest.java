
package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static org.junit.Assert.assertEquals;
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

import com.gnp.autos.wsp.cotizador.eot.client.CampaniasClient;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.campain.CampaniaResp;
import com.google.gson.Gson;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class CampaniasDomainImplTest {

    /** The str cotizacion negocio. */
    private String strCotizacionNegocio;

    /** The str band resp. */
    private String strCampaniaResp;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotizacionNegocio;

    /** The band resp. */
    private CampaniaResp campaniaResp;

    /** The bandera domain. */
    @InjectMocks
    private CampaniasDomainImpl campaniaDomain;

    /** The campanias client. */
    @Mock
    private CampaniasClient campaniasClient;

    /**
     * Sets the file.
     *
     * @param myRes the new file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/cotizacionNegocioBandera.json")
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
    @Value("classpath:/campaniaResp.json")
    public void setFileCatResp(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strCampaniaResp = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Inits the mocks.
     */
    @Before
    public void initMocks() {
        campaniaDomain = new CampaniasDomainImpl();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Preparar test.
     */
    @Before
    public void prepararTest() {
        Gson gson = new Gson();
        this.cotizacionNegocio = gson.fromJson(strCotizacionNegocio, CotizacionNegocio.class);
        this.campaniaResp = gson.fromJson(strCampaniaResp, CampaniaResp.class);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCampaniaWSPReqTest() {
        when(campaniasClient.getCampanias(Matchers.any())).thenReturn(this.campaniaResp);
        when(campaniasClient.getCampanias(Matchers.any(), Matchers.any())).thenReturn(this.campaniaResp);

        CampaniaResp actual = campaniaDomain.getCampana(this.cotizacionNegocio);
        assertNotNull(actual);
        this.cotizacionNegocio.setTid(null);
        actual = campaniaDomain.getCampana(this.cotizacionNegocio);
        assertEquals(actual.getReglas(), this.campaniaResp.getReglas());
    }

    /**
     * Gets the campania err WSP test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCampaniaErrWSPTest() {
        when(campaniasClient.getCampanias(Matchers.any())).thenReturn(this.campaniaResp);
        when(campaniasClient.getCampanias(Matchers.any(), Matchers.any())).thenReturn(this.campaniaResp);

        this.cotizacionNegocio.getUmoService().getDominios().getCombinaciones().get(0).setPaquetes(null);
        campaniaDomain.getCampana(this.cotizacionNegocio);
    }

}
