
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

import com.gnp.autos.wsp.cotizador.eot.client.soap.RulesClient;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.RegAct;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ReglaActiva;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ServicioReglasAutosCotizadorResponse;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.Valida;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoRequest;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoResponse;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.google.gson.Gson;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class RulesDomainImplTest {

    /** The str cotizacion negocio. */
    private String strCotizacionNegocio;

    /** The cotizacion negocio. */
    private CotizacionNegocio cotizacionNegocio;

    /** The str band resp. */
    private String strCalcularPrimaReq;

    /** The str band resp. */
    private String strCalcularPrimaResp;

    /** The catalogo domain. */
    @InjectMocks
    private RulesDomainImpl rulesDomain;

    /** The catalogo client. */
    @Mock
    private RulesClient client;

    /** The web service template. */
    @Mock
    private WebServiceTemplate webServiceTemplate;

    /** The cal prima req. */
    private CalcularPrimaAutoRequest calPrimaReq;

    /** The cal prima resp. */
    private CalcularPrimaAutoResponse calPrimaResp;

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
     * Inits the mocks.
     */
    @Before
    public void initMocks() {
        rulesDomain = new RulesDomainImpl();
        ReflectionTestUtils.setField(rulesDomain, "urlTInter", "http://txlogger");
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
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getReglasWSPReqTest() {
        ServicioReglasAutosCotizadorResponse resp = new ServicioReglasAutosCotizadorResponse();
        Valida value = new Valida();
        value.setResultadoValida("CONTINUAR");
        resp.setVALIDA(value);
        resp.setOUTPUTSTRING("EERRRROR");

        RegAct e = new RegAct();
        e.setIdRegla("333");
        e.setMensaje("ddd");
        ReglaActiva reglasActs = new ReglaActiva();
        reglasActs.getRegAct().add(e);
        resp.setLISTAREGLASACTIVAS(reglasActs);
        when(client.getRules(Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(resp);

        CotizacionNegocio actual = rulesDomain.getRules(this.cotizacionNegocio);
        assertNotNull(actual);
        this.cotizacionNegocio.getCotizaNegReq().setIdCotizacion("");

        when(webServiceTemplate.marshalSendAndReceive(anyObject())).thenReturn("834972423");

        actual = rulesDomain.getRules(this.cotizacionNegocio);
        assertNotNull(actual);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = WSPXmlExceptionWrapper.class)
    public void getReglasExWSPReqTest() {
        ServicioReglasAutosCotizadorResponse resp = new ServicioReglasAutosCotizadorResponse();
        Valida value = new Valida();
        value.setResultadoValida("RECHAZAR");
        resp.setVALIDA(value);
        resp.setOUTPUTSTRING("EERRRROR");

        RegAct e = new RegAct();
        e.setIdRegla("333");
        e.setMensaje("ddd");
        ReglaActiva reglasActs = new ReglaActiva();
        reglasActs.getRegAct().add(e);
        resp.setLISTAREGLASACTIVAS(reglasActs);
        when(client.getRules(Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(resp);

        rulesDomain.getRules(this.cotizacionNegocio);

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getReglasAutoEliteWSPTest() {
        ServicioReglasAutosCotizadorResponse resp = new ServicioReglasAutosCotizadorResponse();
        Valida value = new Valida();
        value.setResultadoValida("CONTINUAR");
        resp.setVALIDA(value);
        resp.setOUTPUTSTRING("EERRRROR");

        RegAct e = new RegAct();
        e.setIdRegla(Constantes.ID_REGLA_ELITE);
        e.setMensaje("ddd");
        ReglaActiva reglasActs = new ReglaActiva();
        reglasActs.getRegAct().add(e);
        resp.setLISTAREGLASACTIVAS(reglasActs);
        when(client.getRules(Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(resp);

        CotizacionNegocio actual = rulesDomain.getRules(this.cotizacionNegocio);
        assertNotNull(actual);

        // si todos son elite entonces quito todos los paquetes y manda excepción
        this.cotizacionNegocio.getUmoService().getDominios().getCombinaciones().get(0).getPaquetes()
                .forEach(p -> p.getCoberturas().forEach(c -> c.setElite(true)));

        rulesDomain.getRules(this.cotizacionNegocio);

    }
}
