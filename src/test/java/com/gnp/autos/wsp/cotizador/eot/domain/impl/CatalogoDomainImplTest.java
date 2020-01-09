
package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static com.google.common.collect.ImmutableMap.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.gnp.autos.wsp.cotizador.eot.client.CatalogoClient;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoWSPResp;
import com.gnp.autos.wsp.negocio.catalogo.model.MultiCatalogosResp;
import com.gnp.autos.wsp.negocio.neg.model.ElementoReq;
import com.gnp.autos.wsp.negocio.util.ResourceFileUtils;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class CatalogoDomainImplTest {
    /** The catalogo client. */
    @Mock
    private CatalogoClient catalogoClient;
    
    /** The catalogo domain. */
    @InjectMocks
    private CatalogoDomainImpl catalogoDomain;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotNeg;
    
    /** The cat resp. */
    private CatalogoWSPResp catResp;
    
    /** The multi resp. */
    private MultiCatalogosResp multiResp;
    
    /**
     * Prepare.
     *
     * @throws FileNotFoundException the file not found exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void prepare() throws FileNotFoundException, IOException {
        ReflectionTestUtils.setField(catalogoDomain, "urlTransaccionIntermedia", "http://tx");
        ReflectionTestUtils.setField(catalogoDomain, "urlCatalogo", "http://cat");
        
        this.cotNeg = ResourceFileUtils.parseFromURL("classpath:cotizacionNegocioCat.json", CotizacionNegocio.class);
        this.catResp = ResourceFileUtils.unmarshalFromURL("classpath:catalogoWSPResp.xml", CatalogoWSPResp.class);
        this.multiResp = ResourceFileUtils.unmarshalFromURL("classpath:multiCatalogosResp.xml", 
                MultiCatalogosResp.class);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCatalogoWSPReqTest() {
        when(catalogoClient.getCatalogoWSP(any())).thenReturn(this.catResp);

        CatalogoWSPResp actual = catalogoDomain.getCatalogoWSP(this.cotNeg);
        assertNotNull(actual);
        assertNotNull(actual.getEstadoCirculacion());
        assertNotNull(actual.getRegionTarificacion());
        assertEquals(actual.getValorVehiculo(), this.catResp.getValorVehiculo());
    }

    /**
     * Gets the catalogo WSP error test.
     *
     */
    @Test(expected = WSPXmlExceptionWrapper.class)
    public void getCatalogoWSPErrorTest() {
        this.catResp.setStrError("no se encontro informacion");
        when(catalogoClient.getCatalogoWSP(any())).thenReturn(this.catResp);
        CatalogoWSPResp actual = catalogoDomain.getCatalogoWSP(this.cotNeg);
        assertNull(actual);
    }

    /**
     * Gets the catalogo WSP error cond test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCatalogoWSPErrorCondTest() {
        this.cotNeg.getCotizaNegReq().getPersonas().removeIf(p -> !p.getTipo().isEmpty());
        CatalogoWSPResp actual = catalogoDomain.getCatalogoWSP(this.cotNeg);
        assertNull(actual);
    }
    
    /**
     * Gets the catalogos.
     *
     */
    @Test
    public void getCatalogos() {
        when(catalogoClient.getCatalogos(any())).thenReturn(this.multiResp);
        Map<String, Map<String, String>> req = new LinkedHashMap<>();
        req.put("VEHICULOS", of("TIPO_VEHICULO", "AUT"));
        req.put("PAIS", of("PAIS", "MEX"));
        Map<String, List<ElementoReq>> resp = catalogoDomain.getCatalogos(req);
        assertEquals("AUTOMOVIL", resp.get("VEHICULOS").get(0).getValor());
    }
}