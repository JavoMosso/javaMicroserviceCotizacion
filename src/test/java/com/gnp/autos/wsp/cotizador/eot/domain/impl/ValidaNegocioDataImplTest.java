package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
import com.gnp.autos.wsp.negocio.neg.model.CoberturaNeg;
import com.gnp.autos.wsp.negocio.umoservice.model.Descuento;
import com.gnp.autos.wsp.negocio.umoservice.model.DescuentoUmo;
import com.gnp.autos.wsp.negocio.umoservice.model.Transaccion;
import com.gnp.autos.wsp.negocio.umoservice.model.Transacciones;
import com.google.gson.Gson;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class ValidaNegocioDataImplTest {

    /** The str cotizacion negocio. */
    private String strCotizacionNegocio;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotizacionNegocio;

    /** The catalogo domain. */
    @InjectMocks
    private ValidaNegocioDomainImpl validaDomain;

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
        validaDomain = new ValidaNegocioDomainImpl();
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
    @Test(expected = ExecutionError.class)
    public void getValidaUmoExeWSPReqTest() {
        this.cotizacionNegocio.getUmoService().setEsUmo(false);
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaSubRWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setSubRamo("02");
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setValorVehiculo("");
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setFormaIndemnizacion("02");
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getValidaSubRaWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setSubRamo("02");
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setValorVehiculo("233232");
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setFormaIndemnizacion("02");
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getValidaSubRamWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setValorVehiculo("233232");
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setFormaIndemnizacion("02");
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaSubR2WSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setValorVehiculo("");
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setFormaIndemnizacion("02");
        this.cotizacionNegocio.getCotizaNegReq().setCveHerramienta("WSP");
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getValidaSubR3WSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setValorVehiculo("232323");
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setFormaIndemnizacion("04");
        this.cotizacionNegocio.getCotizaNegReq().setCveHerramienta("WSP");
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaFIndExcWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getVehiculo().setFormaIndemnizacion("09");
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaFPagoExcWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().setViaPago("RN");
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaPeriodoExcWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().setPeriodicidad("L");
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getValidaPeriodo2ExcWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().setPeriodicidad("M");
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getValidaPaquetesWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().setPaquetes(null);
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaPaquetesUmoWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().setPaquetes(null);
        this.cotizacionNegocio.getUmoService().getDominios().getCombinaciones().get(0).setPaquetes(null);
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaPaquetes2WSPReqTest() {
        this.cotizacionNegocio.getUmoService().getDominios().getCombinaciones().get(0).getPaquetes()
                .forEach(p -> p.setProductoPersonalizado(""));
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getValidaPaquetes3WSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getPaquetes().forEach(p -> p.setDescPaquete(""));
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaPaquetes4WSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getPaquetes()
                .forEach(p -> p.getCoberturas().forEach(c -> c.setCveCobertura("12345")));
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaAdaptacionesWSPReqTest() {
        CoberturaNeg cob = new CoberturaNeg();
        cob.setCveCobertura("0000000894");
        this.cotizacionNegocio.getCotizaNegReq().getPaquetes().forEach(p -> p.getCoberturas().add(cob));
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaAdaptaciones2WSPReqTest() {
        CoberturaNeg cob = new CoberturaNeg();
        cob.setCveCobertura("0000000894");
        cob.setSa("12345");
        this.cotizacionNegocio.getCotizaNegReq().getPaquetes().forEach(p -> p.getCoberturas().add(cob));
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaAdaptaciones3WSPReqTest() {
        CoberturaNeg cob = new CoberturaNeg();
        cob.setCveCobertura("0000000894");
        cob.setSa("dssd");
        this.cotizacionNegocio.getCotizaNegReq().getPaquetes().forEach(p -> p.getCoberturas().add(cob));
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getValidaDescuentoWSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().setDescuentos(null);
        CotizacionNegocio actual = validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaDescuento2WSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getDescuentos().forEach(d -> d.setCveDescuento("PODSMMS"));
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaDescuento3WSPReqTest() {
        this.cotizacionNegocio.getCotizaNegReq().getDescuentos().forEach(d -> d.setValor("89"));
        validaDomain.valida(this.cotizacionNegocio);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getValidaDescuento4WSPReqTest() {
        Descuento desc = new Descuento();
        DescuentoUmo descuento = new DescuentoUmo();
        descuento.setCodigo(Constantes.STR_VADESVOL);
        descuento.setNombre("DESCUENTO X VOLUMEN");
        desc.setDescuento(descuento);
        List<Transacciones> transacciones = new ArrayList<>();
        Transacciones tx = new Transacciones();
        Transaccion transaccion = new Transaccion();
        transaccion.setId(1);
        transaccion.setNombre("Emision");
        tx.setTransaccion(transaccion);
        tx.setValor(12.5);
        transacciones.add(tx);
        desc.setTransacciones(transacciones);
        this.cotizacionNegocio.getUmoService().getDominios().getCobranzas().getDescuentos().add(desc);
        validaDomain.valida(this.cotizacionNegocio);

        // sin descuento en umo
        this.cotizacionNegocio.getUmoService().getDominios().getCobranzas().setDescuentos(null);
        validaDomain.valida(this.cotizacionNegocio);
        assertNotNull(this.cotizacionNegocio);
    }

}
