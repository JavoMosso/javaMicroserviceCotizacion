
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
import org.springframework.ws.client.core.WebServiceTemplate;

import com.gnp.autos.wsp.cotizador.eot.client.soap.FoliadorClient;
import com.gnp.autos.wsp.cotizador.eot.foliador.wsdl.FoliadorResponse;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.google.gson.Gson;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class FoliadorDomainImplTest {

    /** The str cotizacion negocio. */
    private String strCotizacionNegocio;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotizacionNegocio;

    /** The catalogo domain. */
    @InjectMocks
    private FoliadorDomainImpl foliadorDomain;

    /** The catalogo client. */
    @Mock
    private FoliadorClient foliadorClient;

    /** The web service template. */
    @Mock
    private WebServiceTemplate webServiceTemplate;

    /**
     * Sets the file.
     *
     * @param myRes the new file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/cotizacionNegocioCat.json")
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
        foliadorDomain = new FoliadorDomainImpl();
        ReflectionTestUtils.setField(foliadorDomain, "urlTInter", "http://txlogger");
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Preparar test.
     */
    @Before
    public void prepararTest() {
        Gson gson = new Gson();
        this.cotizacionNegocio = gson.fromJson(strCotizacionNegocio, CotizacionNegocio.class);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCatalogoWSPReqTest() {

        String actual = foliadorDomain.getFolio(this.cotizacionNegocio);
        assertNotNull(actual);
        this.cotizacionNegocio.getCotizaNegReq().setIdCotizacion("");
        foliadorClient.setWebServiceTemplate(webServiceTemplate);
        when(webServiceTemplate.marshalSendAndReceive(Matchers.any())).thenReturn("834972423");
        FoliadorResponse folResp = new FoliadorResponse();
        folResp.setIDTRANSACCION("CIANE12345645");
        when(foliadorClient.getFolio(Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(folResp);
        actual = foliadorDomain.getFolio(this.cotizacionNegocio);
        assertNotNull(actual);
    }

}
