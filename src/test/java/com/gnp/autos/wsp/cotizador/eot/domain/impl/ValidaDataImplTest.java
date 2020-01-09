package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.errors.exceptions.WSPMultipleErrorException;
import com.google.gson.Gson;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class ValidaDataImplTest {

    /** The str cotizacion negocio. */
    private String strCotizacionNegocio;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotizacionNegocio;

    /** The catalogo domain. */
    @InjectMocks
    private ValidaDataImpl validaDomain;

    /**
     * Sets the file.
     *
     * @param myRes the new file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Value("classpath:/cotizacionNegocioValida.json")
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
        validaDomain = new ValidaDataImpl();
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
    public void getValidaWSPReqTest() {
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getValidaPWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getPersonas().forEach(p -> p.setSexo(Constantes.PERSONA_MASCULINO));
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaPerErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().setPersonas(null);
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaPerCondErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getPersonas().forEach(p -> p.setTipo(Constantes.STR_CONDUCTOR));
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaPerContErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getPersonas().forEach(p -> p.setTipo(Constantes.STR_CONTRATANTE));
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = WSPMultipleErrorException.class)
    public void getValidaVehModErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setModelo("1dfg2344,33");
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaVehModEErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setModelo("1dfg");
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaFecEErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().setIniVig("20001d15");
        this.cotizacionNegocio.getCotizaNegReq().setFinVig("20001d15");
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = WSPMultipleErrorException.class)
    public void getValidaFec2EErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().setIniVig("");
        this.cotizacionNegocio.getCotizaNegReq().setFinVig(null);
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaFec3EErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().setIniVig("20190624");
        this.cotizacionNegocio.getCotizaNegReq().setFinVig("20180624");
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaPerEErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getPersonas().forEach(p -> p.setTipoPersona("R"));
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getValidaPerE2ErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getPersonas().forEach(p -> p.setTipoPersona("M"));
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = WSPMultipleErrorException.class)
    public void getValidaPer2EErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getPersonas().forEach(p -> p.setTipoPersona(""));
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaPer3EErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getPersonas().forEach(p -> p.setSexo("F"));
        validaDomain.valida(this.cotizacionNegocio);

        this.cotizacionNegocio.getCotizaNegReq().getPersonas().forEach(p -> p.setSexo("T"));
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaVEhEErrWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setValorConvenido("");
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setValorVehiculo("124564");
        validaDomain.valida(this.cotizacionNegocio);

        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setValorVehiculo("12456sds4");
        validaDomain.valida(this.cotizacionNegocio);

    }
}
