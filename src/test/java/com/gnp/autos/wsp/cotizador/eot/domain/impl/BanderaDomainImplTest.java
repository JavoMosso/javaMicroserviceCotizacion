
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

import com.gnp.autos.wsp.cotizador.eot.client.BanderasClient;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.banderas.BanderasAutosDecisionService;
import com.google.gson.Gson;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class BanderaDomainImplTest {

    /** The str cotizacion negocio. */
    private String strCotizacionNegocio;

    /** The str band resp. */
    private String strBandResp;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotizacionNegocio;

    /** The band resp. */
    private BanderasAutosDecisionService bandResp;

    /** The bandera domain. */
    @InjectMocks
    private BanderaDomainImpl banderaDomain;

    /** The banderas client. */
    @Mock
    private BanderasClient banderasClient;

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
    @Value("classpath:/banderaRes.json")
    public void setFileCatResp(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strBandResp = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Inits the mocks.
     */
    @Before
    public void initMocks() {
        banderaDomain = new BanderaDomainImpl();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Preparar test.
     */
    @Before
    public void prepararTest() {
        Gson gson = new Gson();
        this.cotizacionNegocio = gson.fromJson(strCotizacionNegocio, CotizacionNegocio.class);
        this.bandResp = gson.fromJson(strBandResp, BanderasAutosDecisionService.class);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCatalogoWSPReqTest() {
        when(banderasClient.getBanderas(Matchers.any())).thenReturn(this.bandResp);
        when(banderasClient.getBanderas(Matchers.any(), Matchers.any())).thenReturn(this.bandResp);

        BanderasAutosDecisionService actual = banderaDomain.actualizaBandera(this.cotizacionNegocio);
        assertNotNull(actual);
        this.cotizacionNegocio.setTid(null);
        actual = banderaDomain.actualizaBandera(this.cotizacionNegocio);
        assertNotNull(actual.getVehiculo());
        assertNotNull(actual.getNegocioModelo());
        assertEquals(actual.getListCoberturas(), this.bandResp.getListCoberturas());

    }

}
