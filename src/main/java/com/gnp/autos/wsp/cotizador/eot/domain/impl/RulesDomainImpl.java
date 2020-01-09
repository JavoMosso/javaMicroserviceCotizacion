package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.gnp.autos.wsp.cotizador.eot.client.soap.RulesClient;
import com.gnp.autos.wsp.cotizador.eot.domain.RulesDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.Anexo;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.Cabecera;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.Cobertura;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.Cobranza;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.Cotizacion;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.NegocioOperable2;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.Poliza;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ProductoCotizador;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ServicioReglasAutosCotizadorData;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ServicioReglasAutosCotizadorRequest;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ServicioReglasAutosCotizadorResponse;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.Vehiculo;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.gnp.autos.wsp.errors.xml.ErrorXML;
import com.gnp.autos.wsp.negocio.neg.model.CoberturaNeg;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.PaqueteNeg;
import com.gnp.autos.wsp.negocio.neg.model.PersonaNeg;
import com.gnp.autos.wsp.negocio.umoservice.model.CoberturaUmo;

/**
 * The Class RulesDomainImpl.
 */
@Component
@Qualifier("rules")
public class RulesDomainImpl implements RulesDomain {

    /** The client. */
    @Autowired
    private RulesClient client;

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
        Utileria.getRegistraLogTime("Inicia Rules");
        Integer tid = cotizacionNegocio.getTid();
        ServicioReglasAutosCotizadorRequest requestDatos = obtenRequestDatos(cotizacionNegocio);
        ServicioReglasAutosCotizadorResponse respSOAP;
        respSOAP = client.getRules(requestDatos, tid, urlTInter);

        if (!respSOAP.getVALIDA().getResultadoValida().equalsIgnoreCase(Constantes.STR_CONTINUAR)) {
            String causas = respSOAP.getVALIDA().getCausas().stream().collect(Collectors.joining());
            ErrorXML ewsp = new ErrorXML();
            ewsp.setClave(1);
            ewsp.setError(causas);
            ewsp.setNow(new Date());
            ewsp.setOrigen("SERVICIO VALIDAS");
            throw new WSPXmlExceptionWrapper(ewsp);
        }
        actualizaElite(respSOAP, cotizacionNegocio);

        return cotizacionNegocio;
    }

    /**
     * Obten request datos.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the servicio reglas autos cotizador request
     */
    private ServicioReglasAutosCotizadorRequest obtenRequestDatos(final CotizacionNegocio cotizacionNegocio) {
        ServicioReglasAutosCotizadorRequest requestDatos = new ServicioReglasAutosCotizadorRequest();

        Cabecera cab = new Cabecera();
        Anexo cabA = new Anexo();

        CotizaNegReq objReq = cotizacionNegocio.getCotizaNegReq();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder;
        try {
            builder = dbf.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element e = doc.createElement("ID_TRANSACCION");
            e.setNodeValue(objReq.getIdCotizacion());
            e.setTextContent(objReq.getIdCotizacion());
            cab.getAny().add(e);

            Element e2 = doc.createElement("CVE_TRANSACCION");
            e2.setTextContent("1");
            cab.getAny().add(e2);

            Element eleA = doc.createElement("ID_LINEA_OPERATIVA");
            eleA.setNodeValue("");
            cabA.getAny().add(eleA);
        } catch (ParserConfigurationException e1) {
            throw new ExecutionError(Constantes.ERROR_4, e1);
        }

        requestDatos.setCabecera(cab);
        requestDatos.setCabeceraAnexo(cabA);

        NegocioOperable2 nop = new NegocioOperable2();
        nop.setIdNegocioOperable(objReq.getIdUMO());

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

        Poliza pol = new Poliza();
        Cobranza cob = new Cobranza();
        cob.setFormaPago(objReq.getPeriodicidad());
        cob.setConductoPago(objReq.getViaPago());
        XMLGregorianCalendar fecIni = Utileria.getXMLDate(objReq.getIniVig());
        XMLGregorianCalendar fecFin = Utileria.getXMLDate(objReq.getFinVig());
        pol.setTsVigenciaInicial(fecIni);
        pol.setTsVigenciaFinal(fecFin);
        pol.setCobranza(cob);

        List<ProductoCotizador> productosCot = objReq.getPaquetes().parallelStream().map(this::addProducto)
                .collect(Collectors.toList());

        cot.getListaProductos().addAll(productosCot);

        ve.setEstadoCirculacion(cotizacionNegocio.getCatalogoResp().getRegionTarificacion());

        ServicioReglasAutosCotizadorData srd = new ServicioReglasAutosCotizadorData();
        srd.setCOTIZACION(cot);
        srd.setNEGOCIOOPERABLE(nop);
        srd.setVEHICULO(ve);
        srd.setPOLIZA(pol);

        requestDatos.setServicioReglasAutosCotizadorData(srd);

        return requestDatos;
    }

    /**
     * Sets the vehiculo.
     *
     * @param objReq the obj req
     * @return the vehiculo
     */
    private static Vehiculo setVehiculo(final CotizaNegReq objReq) {
        Vehiculo ve = new Vehiculo();

        ve.setTipo(objReq.getVehiculo().getSubRamo());
        ve.setModelo(objReq.getVehiculo().getModelo());
        ve.setArmadora(objReq.getVehiculo().getArmadora());
        ve.setClaveCarroceria(objReq.getVehiculo().getCarroceria());
        ve.setValor(Utileria.getValorDoubleZero(Utileria.soloNumeros(objReq.getVehiculo().getValorVehiculo())));
        ve.setTipoValor(objReq.getVehiculo().getValorConvenido());
        ve.setMarca(objReq.getVehiculo().getVersion());
        ve.setClave(objReq.getVehiculo().getTipoVehiculo());
        ve.setProcedencia(objReq.getVehiculo().getSubRamo());
        ve.setTipoValor(objReq.getVehiculo().getTipoValorVehiculo());
        ve.setUso(objReq.getVehiculo().getUso());
        ve.setTipoCarga(objReq.getVehiculo().getTipoCarga());

        return ve;
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
        Cobertura cob = new Cobertura();

        cob.setClave(cobP.getCveCobertura());
        cob.setMtoSumaAsegurada(Utileria.getValorDouble(cobP.getSa()));
        cob.setMtoDeducible(Utileria.getValorDouble(cobP.getDeducible()));
        cob.setBanContratada(true);

        return cob;
    }

    /**
     * Actualiza elite.
     *
     * @param respRules         the resp rules
     * @param cotizacionNegocio the cotizacion negocio
     */
    private void actualizaElite(final ServicioReglasAutosCotizadorResponse respRules,
            final CotizacionNegocio cotizacionNegocio) {
        respRules.getLISTAREGLASACTIVAS().getRegAct().stream()
                .filter(p -> p.getIdRegla().equalsIgnoreCase(Constantes.ID_REGLA_ELITE)).findFirst()
                .ifPresent(reglaElite -> {
                    Utileria.getRegistraLogTime("Es auto elite" + reglaElite.getStatus());
                    cotizacionNegocio.getCotizaNegReq().getPaquetes().removeIf(p -> cotizacionNegocio.getUmoService()
                            .getDominios().getCombinaciones().get(0).getPaquetes().parallelStream()
                            .filter(pU -> pU.getProductoPersonalizado().equalsIgnoreCase(p.getCvePaquete()))
                            .anyMatch(paqUmo -> p.getCoberturas().stream()
                                    .anyMatch(q -> paqUmo.getCoberturas().parallelStream().filter(
                                            cU -> cU.getCobertura().getClave().equalsIgnoreCase(q.getCveCobertura()))
                                            .filter(cU -> Optional.ofNullable(cU.getElite()).isPresent())
                                            .anyMatch(CoberturaUmo::getElite))));
                    if (cotizacionNegocio.getCotizaNegReq().getPaquetes().isEmpty()) {
                        throw new ExecutionError(Constantes.ERROR_37, "No aplica paquetes Elite");
                    }
                });

    }

}
