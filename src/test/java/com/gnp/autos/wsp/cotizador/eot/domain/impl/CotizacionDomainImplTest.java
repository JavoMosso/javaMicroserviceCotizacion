
package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.gnp.autos.wsp.cotizador.eot.domain.BanderaDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.CampaniasDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.CatalogoDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.FoliadorDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.FormatoImpresionDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.MucDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.RulesDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.TransformacionDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.UmoServiceDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.ValidaDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.ValidaNegocioDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.banderas.BanderasAutosDecisionService;
import com.gnp.autos.wsp.cotizador.eot.model.campain.CampaniaResp;
import com.gnp.autos.wsp.cotizador.eot.model.transformacion.TransformacionNeg;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoRequest;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoResponse;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.gnp.autos.wsp.errors.xml.ErrorXML;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoWSPResp;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;
import com.google.gson.Gson;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class CotizacionDomainImplTest {

    /** The str cotizacion negocio. */
    private String strCotizacionNegocioCat;

    /** The str cotizacion negocio. */
    private String strCotizacionNegocioBan;

    /** The str band resp. */
    private String strBandResp;

    /** The str band resp. */
    private String strCatResp;

    /** The str band resp. */
    private String strVarTransResp;

    /** The str band resp. */
    private String strUmoServiceResp;

    /** The str band resp. */
    private String strCampaniaResp;

    /** The str band resp. */
    private String strCotizacionRespFin;

    /** The str band resp. */
    private String strBandARResp;

    /** The str band resp. */
    private String strCalcularPrimaReq;

    /** The str band resp. */
    private String strCalcularPrimaResp;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotizacionNegocioCat;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotizacionNegocioBan;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotizacionNegocioFin;

    /** The band resp. */
    private BanderasAutosDecisionService bandResp;

    /** The band resp. */
    private BanderasAutosDecisionService bandARResp;

    /** The band resp. */
    private TransformacionNeg transformacionNeg;

    /** The band resp. */
    private CatalogoWSPResp catResp;

    /** The band resp. */
    private CampaniaResp campResp;

    /** The band resp. */
    private UmoServiceResp umoServiceResp;

    /** The cal prima req. */
    private CalcularPrimaAutoRequest calPrimaReq;

    /** The cal prima resp. */
    private CalcularPrimaAutoResponse calPrimaResp;

    /** The bandera domain. */
    @InjectMocks
    private CotizacionDomainImpl cotizacionDomain;

    /** The umo service domain. */
    @Mock
    private UmoServiceDomain umoServiceDomain;

    /** The valida data. */
    @Mock
    private ValidaDomain validaData;

    /** The valida negocio. */
    @Mock
    private ValidaNegocioDomain validaNegocio;

    /** The bandera domain. */
    @Mock
    private BanderaDomain banderaDomain;

    /** The rules client. */
    @Mock
    @Qualifier("rules")
    private RulesDomain rulesClient;

    /** The rules client. */
    @Mock
    @Qualifier("rulesProd")
    private RulesDomain rulesProdClient;

    /** The folio cot. */
    @Mock
    private FoliadorDomain folioCot;

    /** The muc domain. */
    @Mock
    private MucDomain mucDomain;

    /** The formato impresion domain. */
    @Mock
    private FormatoImpresionDomain formatoImpresionDomain;

    /** The catalogo domain. */
    @Mock
    private CatalogoDomain catalogoDomain;

    /** The campania domain. */
    @Mock
    private CampaniasDomain campaniaDomain;

    /** The transformacion domain. */
    @Mock
    private TransformacionDomain transformacionDomain;

    /**
     * Sets the file.
     *
     * @param myRes the new file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/cotizacionNegocioCat.json")
    public void setFileCat(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strCotizacionNegocioCat = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sets the file.
     *
     * @param myRes the new file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/cotizacionNegocioBandera.json")
    public void setFileBan(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strCotizacionNegocioBan = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sets the file cat resp.
     *
     * @param myRes the new file cat resp
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/banderaRes.json")
    public void setFileBanResp(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strBandResp = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sets the file cat resp.
     *
     * @param myRes the new file cat resp
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/banderaARRes.json")
    public void setFileBanARResp(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strBandARResp = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sets the file cat resp.
     *
     * @param myRes the new file cat resp
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/catalogoWSPResp.xml")
    public void setFileCatResp(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strCatResp = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sets the file cat resp.
     *
     * @param myRes the new file cat resp
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/variableTransResp.json")
    public void setFileVarTransResp(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strVarTransResp = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sets the file cat resp.
     *
     * @param myRes the new file cat resp
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/umoServiceResp.json")
    public void setFileUmoResp(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strUmoServiceResp = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sets the file cat resp.
     *
     * @param myRes the new file cat resp
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/campaniaResp.json")
    public void setFileCampResp(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strCampaniaResp = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sets the file cat resp.
     *
     * @param myRes the new file cat resp
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/cotizacionNegocioResult.json")
    public void setFileCotResult(final Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strCotizacionRespFin = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
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
        cotizacionDomain = new CotizacionDomainImpl();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Preparar test.
     */
    @Before
    public void prepararTest() {
        Gson gson = new Gson();
        this.cotizacionNegocioCat = gson.fromJson(strCotizacionNegocioCat, CotizacionNegocio.class);
        this.cotizacionNegocioBan = gson.fromJson(strCotizacionNegocioBan, CotizacionNegocio.class);
        this.cotizacionNegocioFin = gson.fromJson(strCotizacionRespFin, CotizacionNegocio.class);

        this.campResp = gson.fromJson(strCampaniaResp, CampaniaResp.class);
        this.bandResp = gson.fromJson(strBandResp, BanderasAutosDecisionService.class);
        this.bandARResp = gson.fromJson(strBandARResp, BanderasAutosDecisionService.class);
        this.catResp = Utileria.unmarshalXml(CatalogoWSPResp.class, strCatResp);

        this.calPrimaReq = Utileria.unmarshalXmlPaq(CalcularPrimaAutoRequest.class, strCalcularPrimaReq);
        this.calPrimaResp = Utileria.unmarshalXmlPaq(CalcularPrimaAutoResponse.class, strCalcularPrimaResp);

        this.cotizacionNegocioFin.setReqMuc(calPrimaReq);
        this.cotizacionNegocioFin.setRespMuc(calPrimaResp);
        this.transformacionNeg = gson.fromJson(strVarTransResp, TransformacionNeg.class);
        this.umoServiceResp = gson.fromJson(strUmoServiceResp, UmoServiceResp.class);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCotizacionTest() {
        when(validaData.valida(Matchers.any())).thenReturn(this.cotizacionNegocioCat);
        when(validaNegocio.valida(Matchers.any())).thenReturn(this.cotizacionNegocioBan);
        when(catalogoDomain.getCatalogoWSP(Matchers.any())).thenReturn(this.catResp);
        when(banderaDomain.actualizaBandera(Matchers.any())).thenReturn(this.bandResp);
        when(campaniaDomain.getCampana(Matchers.any())).thenReturn(this.campResp);
        when(transformacionDomain.getVariablesTransformacion(Matchers.any())).thenReturn(this.transformacionNeg);
        when(folioCot.getFolio(Matchers.any())).thenReturn("CIANNE343758697");
        when(umoServiceDomain.getDatosNegocio(Matchers.any())).thenReturn(this.umoServiceResp);
        when(formatoImpresionDomain.getFormatoImp(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        CotizacionNegocio actual = cotizacionDomain.getCotizacion(this.cotizacionNegocioCat);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCotizacionARTest() {
        when(validaData.valida(Matchers.any())).thenReturn(this.cotizacionNegocioCat);
        when(validaNegocio.valida(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        when(catalogoDomain.getCatalogoWSP(Matchers.any())).thenReturn(this.catResp);
        when(banderaDomain.actualizaBandera(Matchers.any())).thenReturn(this.bandARResp);
        when(campaniaDomain.getCampana(Matchers.any())).thenReturn(this.campResp);
        when(transformacionDomain.getVariablesTransformacion(Matchers.any())).thenReturn(this.transformacionNeg);
        when(folioCot.getFolio(Matchers.any())).thenReturn("CIANNE343758697");
        when(umoServiceDomain.getDatosNegocio(Matchers.any())).thenReturn(this.umoServiceResp);
        when(formatoImpresionDomain.getFormatoImp(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        CotizacionNegocio actual = cotizacionDomain.getCotizacion(this.cotizacionNegocioCat);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getCotizacionErrTest() {
        when(validaData.valida(Matchers.any())).thenReturn(this.cotizacionNegocioCat);
        when(validaNegocio.valida(Matchers.any())).thenReturn(this.cotizacionNegocioBan);
        when(catalogoDomain.getCatalogoWSP(Matchers.any())).thenReturn(this.catResp);
        when(banderaDomain.actualizaBandera(Matchers.any())).thenReturn(this.bandResp);
        when(campaniaDomain.getCampana(Matchers.any())).thenReturn(this.campResp);
        when(transformacionDomain.getVariablesTransformacion(Matchers.any())).thenReturn(this.transformacionNeg);
        when(folioCot.getFolio(Matchers.any())).thenReturn("");
        when(umoServiceDomain.getDatosNegocio(Matchers.any())).thenReturn(this.umoServiceResp);
        cotizacionDomain.getCotizacion(this.cotizacionNegocioCat);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getCotizacionCA3Test() {

        this.cotizacionNegocioCat.getCotizaNegReq().getVehiculo().setTipoVehiculo("CA3");
        this.cotizacionNegocioFin.getCotizaNegReq().getVehiculo().setTipoVehiculo("CA3");

        this.bandResp.getVehiculo().setTipoVehiculo("CA3");

        when(validaData.valida(Matchers.any())).thenReturn(this.cotizacionNegocioCat);
        when(validaNegocio.valida(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        when(catalogoDomain.getCatalogoWSP(Matchers.any())).thenReturn(this.catResp);
        when(banderaDomain.actualizaBandera(Matchers.any())).thenReturn(this.bandResp);
        when(campaniaDomain.getCampana(Matchers.any())).thenReturn(this.campResp);
        when(transformacionDomain.getVariablesTransformacion(Matchers.any())).thenReturn(this.transformacionNeg);
        when(folioCot.getFolio(Matchers.any())).thenReturn("CIANNE343758697");
        when(umoServiceDomain.getDatosNegocio(Matchers.any())).thenReturn(this.umoServiceResp);
        when(formatoImpresionDomain.getFormatoImp(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        CotizacionNegocio actual = cotizacionDomain.getCotizacion(this.cotizacionNegocioCat);
        assertNotNull(actual);

        // si no trae derecho de poliza
        this.cotizacionNegocioFin.getReqMuc().getDATOSCOTIZACION().get(0).getDATOSSOLICITUD()
                .setPOLITICADERECHOPOLIZA(null);
        actual = cotizacionDomain.getCotizacion(this.cotizacionNegocioCat);
        assertNotNull(actual);

        // sin descuento de campania
        this.campResp.setDescuentoTotal("0");
        this.cotizacionNegocioFin.getCotizaNegReq().setDescuentos(null);
        when(campaniaDomain.getCampana(Matchers.any())).thenReturn(this.campResp);
        when(validaNegocio.valida(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        when(formatoImpresionDomain.getFormatoImp(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        actual = cotizacionDomain.getCotizacion(this.cotizacionNegocioCat);
        assertNotNull(actual);

    }

    @Test
    public void getCotizacionSinDescTest() {
        this.cotizacionNegocioFin.getCotizaNegReq().setDescuentos(null);
        when(validaData.valida(Matchers.any())).thenReturn(this.cotizacionNegocioCat);
        when(validaNegocio.valida(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        when(catalogoDomain.getCatalogoWSP(Matchers.any())).thenReturn(this.catResp);
        when(banderaDomain.actualizaBandera(Matchers.any())).thenReturn(this.bandResp);
        when(campaniaDomain.getCampana(Matchers.any())).thenReturn(this.campResp);
        when(transformacionDomain.getVariablesTransformacion(Matchers.any())).thenReturn(this.transformacionNeg);
        when(folioCot.getFolio(Matchers.any())).thenReturn("CIANNE343758697");
        when(umoServiceDomain.getDatosNegocio(Matchers.any())).thenReturn(this.umoServiceResp);
        when(formatoImpresionDomain.getFormatoImp(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        CotizacionNegocio actual = cotizacionDomain.getCotizacion(this.cotizacionNegocioCat);
        assertNotNull(actual);

    }

    @Test(expected = ExecutionError.class)
    public void getCotizacionErrFuturoTest() {
        when(validaData.valida(Matchers.any())).thenReturn(this.cotizacionNegocioCat);
        when(validaNegocio.valida(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        when(catalogoDomain.getCatalogoWSP(Matchers.any())).thenThrow(new ExecutionError(0));
        when(banderaDomain.actualizaBandera(Matchers.any())).thenReturn(this.bandResp);
        when(campaniaDomain.getCampana(Matchers.any())).thenReturn(this.campResp);
        when(transformacionDomain.getVariablesTransformacion(Matchers.any())).thenReturn(this.transformacionNeg);
        when(folioCot.getFolio(Matchers.any())).thenReturn("CIANNE343758697");
        when(umoServiceDomain.getDatosNegocio(Matchers.any())).thenReturn(this.umoServiceResp);
        when(formatoImpresionDomain.getFormatoImp(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        cotizacionDomain.getCotizacion(this.cotizacionNegocioCat);
    }

    @Test(expected = ExecutionError.class)
    public void getCotizacionErrValidaTest() {
        when(validaData.valida(Matchers.any())).thenReturn(this.cotizacionNegocioCat);
        when(validaNegocio.valida(Matchers.any())).thenThrow(new ExecutionError(0));
        when(catalogoDomain.getCatalogoWSP(Matchers.any())).thenThrow(new ExecutionError(0));
        when(banderaDomain.actualizaBandera(Matchers.any())).thenReturn(this.bandResp);
        when(campaniaDomain.getCampana(Matchers.any())).thenReturn(this.campResp);
        when(transformacionDomain.getVariablesTransformacion(Matchers.any())).thenReturn(this.transformacionNeg);
        when(folioCot.getFolio(Matchers.any())).thenReturn("CIANNE343758697");
        when(umoServiceDomain.getDatosNegocio(Matchers.any())).thenReturn(this.umoServiceResp);
        when(formatoImpresionDomain.getFormatoImp(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        cotizacionDomain.getCotizacion(this.cotizacionNegocioCat);
    }

    @Test(expected = WSPXmlExceptionWrapper.class)
    public void getCotizacionErrFuturo2Test() {
        when(validaData.valida(Matchers.any())).thenReturn(this.cotizacionNegocioCat);
        when(validaNegocio.valida(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        when(catalogoDomain.getCatalogoWSP(Matchers.any())).thenThrow(new WSPXmlExceptionWrapper(new ErrorXML()));
        when(banderaDomain.actualizaBandera(Matchers.any())).thenReturn(this.bandResp);
        when(campaniaDomain.getCampana(Matchers.any())).thenReturn(this.campResp);
        when(transformacionDomain.getVariablesTransformacion(Matchers.any())).thenReturn(this.transformacionNeg);
        when(folioCot.getFolio(Matchers.any())).thenReturn("CIANNE343758697");
        when(umoServiceDomain.getDatosNegocio(Matchers.any())).thenReturn(this.umoServiceResp);
        when(formatoImpresionDomain.getFormatoImp(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        cotizacionDomain.getCotizacion(this.cotizacionNegocioCat);
    }
    
    @Test(expected = ExecutionError.class)
    public void getCotizacionRuntimeExceptionTest() {
        when(validaData.valida(Matchers.any())).thenReturn(this.cotizacionNegocioCat);
        when(validaNegocio.valida(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        when(catalogoDomain.getCatalogoWSP(Matchers.any())).thenThrow(new RuntimeException());
        when(banderaDomain.actualizaBandera(Matchers.any())).thenReturn(this.bandResp);
        when(campaniaDomain.getCampana(Matchers.any())).thenReturn(this.campResp);
        when(transformacionDomain.getVariablesTransformacion(Matchers.any())).thenReturn(this.transformacionNeg);
        when(folioCot.getFolio(Matchers.any())).thenReturn("CIANNE343758697");
        when(umoServiceDomain.getDatosNegocio(Matchers.any())).thenReturn(this.umoServiceResp);
        when(formatoImpresionDomain.getFormatoImp(Matchers.any())).thenReturn(this.cotizacionNegocioFin);
        cotizacionDomain.getCotizacion(this.cotizacionNegocioCat);
    }
}