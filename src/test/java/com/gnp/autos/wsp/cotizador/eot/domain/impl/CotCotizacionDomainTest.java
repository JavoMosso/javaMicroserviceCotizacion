
package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static com.gnp.autos.wsp.negocio.util.ResourceFileUtils.parseFromURL;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.gnp.autos.wsp.cotizador.eot.client.CotCotizacionClient;
import com.gnp.autos.wsp.cotizador.eot.client.SicaNegociosClient;
import com.gnp.autos.wsp.cotizador.eot.domain.CatalogoDomain;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoRequest;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoResponse;
import com.gnp.autos.wsp.negocio.neg.model.ElementoReq;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;
import com.gnp.autos.wsp.negocio.util.ResourceFileUtils;
import com.gnp.autos.wsp.negocio.util.Utils;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class CotCotizacionDomainTest {
    /** The catalogo client. */
    @Mock
    private CotCotizacionClient cotCotizacionClient;
    
    /** The sica negocios client. */
    @Mock
    private SicaNegociosClient sicaNegociosClient;
    
    /** The catalogo domain. */
    @Mock
    private CatalogoDomain catalogoDomain;
    
    /** The catalogo domain. */
    @InjectMocks
    private CotCotizacionDomainImpl cotCotizacionDomain;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotNeg;
    
    /**
     * Prepare.
     *
     * @throws FileNotFoundException the file not found exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void prepare() throws FileNotFoundException, IOException {
        cotNeg = parseFromURL("classpath:cotizacionNegocioResult.json", CotizacionNegocio.class);
        cotNeg.setRespMuc(Utileria.unmarshalXmlPaq(CalcularPrimaAutoResponse.class,
                ResourceFileUtils.getResource("classpath:calcularPrimaRespCotCotizacion.xml")));
        cotNeg.setReqMuc(Utileria.unmarshalXmlPaq(CalcularPrimaAutoRequest.class,
                ResourceFileUtils.getResource("classpath:calcularPrimaReqCotCotizacion.xml")));
        
        when(cotCotizacionClient.guardar(any())).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
        
        UmoServiceResp nodo = new UmoServiceResp();
        nodo.setCodigoPromocion("COP0000332");
        nodo.setNombre("Nueva Internet");
        UmoServiceResp nodos = new UmoServiceResp();
        nodos.setHijos(asList(nodo));
        when(sicaNegociosClient.getNodos(any(), anyInt())).thenReturn(nodos);
        
        Map<String, List<ElementoReq>> multiResp = Utils.asMap(new Object[][] {
            {"VEHICULOS", asList(elemento("03", "VERSION", "NISSAN 370Z TOURING AUT."))},
            {"SUB_RAMO", asList(elemento("01", "VEHICULOS RESIDENTES"))},
            {"TIPO_VEHICULO", asList(elemento("AUT", "TIPO_VEHICULO"))},
            {"USO_VEHICULO", asList(elemento("01", "Particular"))},
            {"FORMA_INDEMNIZACION", asList(elemento("01", "VALOR COMERCIAL"))},
            {"VIA_PAGO", asList(elemento("IN", "Intermediario"))},
            {"TIPO_CARGA_VEHICULO", asList(elemento("A", "Carga no peligrosa"))}});
        when(catalogoDomain.getCatalogos(any())).thenReturn(multiResp);
    }
    
    /**
     * Elemento.
     *
     * @param clave the clave
     * @param nombre the nombre
     * @return the elemento req
     */
    private ElementoReq elemento(final String clave, final String nombre) {
        return elemento(clave, nombre, null);
    }
    
    /**
     * Elemento.
     *
     * @param clave the clave
     * @param nombre the nombre
     * @param valor the valor
     * @return the elemento req
     */
    private ElementoReq elemento(final String clave, final String nombre, final String valor) {
        return new ElementoReq(nombre, clave, valor);
    }

    /**
     * Guardar.
     */
    @Test
    public void guardar() {
        cotCotizacionDomain.guardar(cotNeg);
        assertNotNull(cotNeg.getTid());
    }
    
    @Test
    public void agentesEmpty() {
        cotNeg.getUmoService().getDominios().getIntermediarios().setAgentes(new ArrayList<>());
        cotCotizacionDomain.guardar(cotNeg);
        assertNotNull(cotNeg.getTid());
    }
}