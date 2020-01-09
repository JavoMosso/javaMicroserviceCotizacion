package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.gnp.autos.wsp.cotizador.eot.client.soap.RulesProdClient;
import com.gnp.autos.wsp.cotizador.eot.domain.RulesDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.Anexo;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.Cabecera;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.Cobertura;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.Cobranza;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.Cotizacion;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.NegocioOperable2;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.Poliza;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.ProductoCotizador;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.ServicioReglasAutosProductosData;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.ServicioReglasAutosProductosRequest;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.ServicioReglasAutosProductosResponse;
import com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl.Vehiculo;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.negocio.neg.model.CoberturaNeg;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.PaqueteNeg;
import com.gnp.autos.wsp.negocio.neg.model.PersonaNeg;

/**
 * The Class RulesDomainImpl.
 */
@Component
@Primary
@Qualifier("rulesProd")
public class RulesProdDomainImpl implements RulesDomain {

    /** The client. */
    @Autowired
    private RulesProdClient client;

    /** The url T inter. */
    @Value("${wsp_url_TransaccionIntermedia}")
    private String urlTInter;

    /**
     * Gets the rules.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the rules
     */
    @Override
    public final CotizacionNegocio getRules(final CotizacionNegocio cotizacionNegocio) {
        Utileria.getRegistraLogTime("Inicia Rules Productos");
        Integer tid = cotizacionNegocio.getTid();
        ServicioReglasAutosProductosRequest requestDatos = obtenRequestDatosProd(cotizacionNegocio);
        ServicioReglasAutosProductosResponse respSOAP;
        respSOAP = client.getRulesProd(requestDatos, tid, urlTInter);

        cotizacionNegocio.getCotizaNegReq().getPaquetes().removeIf(paq -> respSOAP.getProductosDisponibles()
                .getIDPRODUCTOPERSONALIZADO().parallelStream().noneMatch(p -> p.equalsIgnoreCase(paq.getCvePaquete())));

        if (cotizacionNegocio.getCotizaNegReq().getPaquetes().isEmpty()) {
            throw new ExecutionError(Constantes.ERROR_37, "No existen paquetes para esta cotizacion");
        }
        return cotizacionNegocio;
    }

    /**
     * Obten request datos.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the servicio reglas autos cotizador request
     */
    private ServicioReglasAutosProductosRequest obtenRequestDatosProd(final CotizacionNegocio cotizacionNegocio) {
        ServicioReglasAutosProductosRequest requestDatos = new ServicioReglasAutosProductosRequest();

        Cabecera cabProd = new Cabecera();
        Anexo cabAProd = new Anexo();

        CotizaNegReq objReq = cotizacionNegocio.getCotizaNegReq();

        DocumentBuilderFactory dbfProd = DocumentBuilderFactory.newInstance();

        DocumentBuilder builderProd;
        try {
            builderProd = dbfProd.newDocumentBuilder();
            Document doc = builderProd.newDocument();
            Element e = doc.createElement("ID_TRANSACCION");
            e.setNodeValue(objReq.getIdCotizacion());
            e.setTextContent(objReq.getIdCotizacion());
            cabProd.getAny().add(e);

            Element e2 = doc.createElement("CVE_TRANSACCION");
            e2.setTextContent("1");
            cabProd.getAny().add(e2);

            Element eleA = doc.createElement("ID_LINEA_OPERATIVA");
            eleA.setNodeValue("");
            cabAProd.getAny().add(eleA);
        } catch (ParserConfigurationException e1) {
            throw new ExecutionError(Constantes.ERROR_4, e1);
        }

        requestDatos.setCabecera(cabProd);
        requestDatos.setCabeceraAnexo(cabAProd);

        NegocioOperable2 nop = new NegocioOperable2();
        nop.setIdNegocioOperable(objReq.getIdUMO());
        nop.setVersionNegocio(cotizacionNegocio.getCotizaNegReq().getIdVersionNegocio().toString());
        nop.setIdCodigoPromocion(cotizacionNegocio.getCotizaNegReq().getCodigoPromocion());

        Cotizacion cot = new Cotizacion();

        for (PersonaNeg per : objReq.getPersonas()) {
            if (per.getTipo().equals(Constantes.STR_CONTRATANTE)) {
                cot.setTipoPersonaSolicitante(per.getTipoPersona());
            }
            if (per.getTipo().equals(Constantes.STR_CONDUCTOR)) {
                String str = per.getEdad();
                BigInteger bi = new BigInteger(str);
                cot.setEdadConductorHabitual(bi);
            }
        }

        Vehiculo ve = setVehiculo(objReq);

        Poliza polPro = new Poliza();
        Cobranza cob = new Cobranza();
        cob.setFormaPago(objReq.getPeriodicidad());
        cob.setConductoPago(objReq.getViaPago());
        XMLGregorianCalendar fecIni = Utileria.getXMLDate(objReq.getIniVig());
        XMLGregorianCalendar fecFin = Utileria.getXMLDate(objReq.getFinVig());
        polPro.setTsVigenciaInicial(fecIni);
        polPro.setTsVigenciaFinal(fecFin);
        polPro.setCobranza(cob);

        List<ProductoCotizador> productosCot = objReq.getPaquetes().parallelStream().map(this::addProducto)
                .collect(Collectors.toList());

        cot.getListaProductos().addAll(productosCot);

        ve.setEstadoCirculacion(cotizacionNegocio.getCatalogoResp().getRegionTarificacion());

        ServicioReglasAutosProductosData srd = new ServicioReglasAutosProductosData();
        srd.setCOTIZACION(cot);
        srd.setNEGOCIOOPERABLE(nop);
        srd.setVEHICULO(ve);
        srd.setPOLIZA(polPro);

        requestDatos.setServicioReglasAutosProductosData(srd);

        return requestDatos;
    }

    /**
     * Sets the vehiculo.
     *
     * @param objReq the obj req
     * @return the vehiculo
     */
    private static Vehiculo setVehiculo(final CotizaNegReq objReq) {
        Vehiculo veh = new Vehiculo();

        veh.setTipo(objReq.getVehiculo().getSubRamo());
        veh.setModelo(objReq.getVehiculo().getModelo());
        veh.setArmadora(objReq.getVehiculo().getArmadora());
        veh.setClaveCarroceria(objReq.getVehiculo().getCarroceria());
        veh.setValor(Utileria.getValorDoubleZero(Utileria.soloNumeros(objReq.getVehiculo().getValorVehiculo())));
        veh.setTipoValor(objReq.getVehiculo().getValorConvenido());
        veh.setMarca(objReq.getVehiculo().getVersion());
        veh.setClave(objReq.getVehiculo().getTipoVehiculo());
        veh.setProcedencia(objReq.getVehiculo().getSubRamo());
        veh.setTipoValor(objReq.getVehiculo().getTipoValorVehiculo());
        veh.setUso(objReq.getVehiculo().getUso());
        veh.setTipoCarga(objReq.getVehiculo().getTipoCarga());

        return veh;
    }

    /**
     * Adds the producto.
     *
     * @param dP the d P
     * @return the producto cotizador
     */
    public final ProductoCotizador addProducto(final PaqueteNeg dP) {
        ProductoCotizador pdCot = new ProductoCotizador();
        pdCot.setId(dP.getCvePaquete());

        List<Cobertura> cbs = dP.getCoberturas().parallelStream().map(this::addCobertura).collect(Collectors.toList());
        pdCot.getCoberturas().addAll(cbs);
        return pdCot;
    }

    /**
     * Adds the cobertura.
     *
     * @param cobP the cob P
     * @return the cobertura
     */
    public final Cobertura addCobertura(final CoberturaNeg cobP) {
        Cobertura cobPro = new Cobertura();

        cobPro.setClave(cobP.getCveCobertura());
        cobPro.setMtoSumaAsegurada(Utileria.getValorDouble(cobP.getSa()));
        cobPro.setMtoDeducible(Utileria.getValorDouble(cobP.getDeducible()));
        cobPro.setBanContratada(true);

        return cobPro;
    }

}
