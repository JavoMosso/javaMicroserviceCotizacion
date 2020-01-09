package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static com.gnp.autos.wsp.negocio.util.Utils.flatMapToList;
import static com.gnp.autos.wsp.negocio.util.Utils.ifNotEmpty;
import static com.gnp.autos.wsp.negocio.util.Utils.mapToList;
import static com.google.common.collect.ImmutableMap.of;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnp.autos.wsp.cotizador.eot.client.CotCotizacionClient;
import com.gnp.autos.wsp.cotizador.eot.client.SicaNegociosClient;
import com.gnp.autos.wsp.cotizador.eot.domain.CatalogoDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.CotCotizacionDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.CatalogoCobertura;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.ConductorHabitual;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.Contratante;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.Item;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.JsonSimplificado;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.Vehiculo;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.Cobertura;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.ConceptoEconomico;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.Cotizacion;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.DatosTransformacion;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.Descuento;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.DescuentoTarificador;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.DetalleCobranza;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.DetalleConductor;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.DetalleContratante;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.DetalleCotizacion;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.DetalleCotizacionPadre;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.DetalleDescuento;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.DetalleEtiqueta;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.DetalleTarificador;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.DetalleVehiculo;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.ElementoCot;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.ElementosCot;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.ElementosCotReq;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.Prima;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.Producto;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle.TotalPrima;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.CotCotizacionMapper;
import com.gnp.autos.wsp.cotizador.eot.util.JsonSimplificadoMapper;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CoberturaPrimaDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.ConceptoDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CotizacionDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.DescuentoDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.FormaPagoReciboDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.ProductosDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.TotalPrimaDto;
import com.gnp.autos.wsp.negocio.cotcotizacion.model.req.CotCotizacionReq;
import com.gnp.autos.wsp.negocio.cotizacion.model.resp.PaqueteResp;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.ElementoNeg;
import com.gnp.autos.wsp.negocio.neg.model.ElementoReq;
import com.gnp.autos.wsp.negocio.neg.model.PersonaNeg;
import com.gnp.autos.wsp.negocio.neg.model.VehiculoNeg;
import com.gnp.autos.wsp.negocio.umoservice.model.CoberturaUmo;
import com.gnp.autos.wsp.negocio.umoservice.model.Elemento;
import com.gnp.autos.wsp.negocio.umoservice.model.Estructura;
import com.gnp.autos.wsp.negocio.umoservice.model.ModeloNegocio;
import com.gnp.autos.wsp.negocio.umoservice.model.Negocio;
import com.gnp.autos.wsp.negocio.umoservice.model.Paquete;
import com.gnp.autos.wsp.negocio.umoservice.model.Tarifa;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;
import com.gnp.autos.wsp.negocio.util.DateUtils;
import com.gnp.autos.wsp.negocio.util.Utils;
import com.gnp.autos.wsp.negocio.util.XmlUtils;
import com.google.common.collect.ImmutableMap;

/**
 * The Class CotCotizacionDomainImpl.
 */
@Service
public class CotCotizacionDomainImpl implements CotCotizacionDomain {
    /** The cot cotizacion client. */
    @Autowired
    private CotCotizacionClient cotCotizacionClient;
    
    /** The sica negocios client. */
    @Autowired
    private SicaNegociosClient sicaNegociosClient;
    
    /** The catalogo domain. */
    @Autowired
    private CatalogoDomain catalogoDomain;
    
    /** The Constant TIPO_VEHICULO. */
    private static final String TIPO_VEHICULO = "TIPO_VEHICULO";
    
    /** The Constant SUB_RAMO. */
    private static final String SUB_RAMO = "SUB_RAMO";
    
    /** The Constant USO_VEHICULO. */
    private static final String USO_VEHICULO = "USO_VEHICULO";
    
    /** The Constant FORMA_INDEMNIZACION. */
    private static final String FORMA_INDEMNIZACION = "FORMA_INDEMNIZACION";
    
    /** The Constant VIA_PAGO. */
    private static final String VIA_PAGO = "VIA_PAGO";
    
    /** The Constant TIPO_CARGA_VEHICULO. */
    private static final String TIPO_CARGA_VEHICULO = "TIPO_CARGA_VEHICULO";
    
    /** The Constant MX_DATE_FORMAT. */
    private static final String MX_DATE_FORMAT = "dd/MM/yyyy";
    
    /** The Constant DESC_VEHICULO. */
    private static final String DESC_VEHICULO = "descVehiculo";
    
    /** The Constant DESC_SUBRAMO. */
    private static final String DESC_SUBRAMO = "descSubramo";
    
    /** The Constant DESC_TIPO. */
    private static final String DESC_TIPO = "descTipo";
    
    /** The Constant DESC_USO. */
    private static final String DESC_USO = "descUso";
    
    /** The Constant DESC_FORMA_INDEMNIZACION. */
    private static final String DESC_FORMA_INDEMNIZACION = "descFormaIndemnizacion";
    
    /** The Constant DESC_VIA_PAGO. */
    private static final String DESC_VIA_PAGO = "descViaPago";
    
    /** The Constant DESC_TIPO_CARGA_VEHICULO. */
    private static final String DESC_TIPO_CARGA_VEHICULO = "descTipoCarga";
    
    /** The Constant CVE_ORIGEN_COTIZACION_WSP. */
    private static final int CVE_ORIGEN_COTIZACION_WSP = 8;
    
    /** The Constant MAPEO_SEXO. */
    private static final Map<String, String> MAPEO_SEXO = ImmutableMap.of("V", "M", "M", "F");
    
    /**
     * Guardar.
     *
     * @param cotNeg the cot neg
     */
    @Override
    public void guardar(final CotizacionNegocio cotNeg) {
        CotCotizacionReq cotReq = new CotCotizacionReq();
        
        CotizaNegReq cotNegReq = cotNeg.getCotizaNegReq();
        UmoServiceResp umoResp = cotNeg.getUmoService();
        Estructura est = umoResp.getNegocio().getEstructura();
        
        PersonaNeg contratante = getPersona(cotNeg, Constantes.STR_CONTRATANTE);
        if (contratante.getTipoPersona().equals("J")) {
            contratante.setTipoPersona(Constantes.TIPO_PERSONA_MORAL);
        }
        
        PersonaNeg conductor = getPersona(cotNeg, Constantes.STR_CONDUCTOR);
        
        Map<String, String> descripciones = getDescripciones(cotNegReq);
        
        cotReq.setApellidoMaterno("");
        cotReq.setApellidoPaterno("");
        cotReq.setCveRamo("A");
        cotReq.setCveRol("1");
        cotReq.setCorreoElectronico("");
        cotReq.setCveEstatusCotizacion("1");
        cotReq.setCveOrigenCotizacion(CVE_ORIGEN_COTIZACION_WSP);
        cotReq.setCveSistema("WSP");
        cotReq.setFolioLegacy("");

        cotReq.setCodigoPostal(conductor.getDomicilio().getCodigoPostal());
        cotReq.setCveSexo(conductor.getSexo());
        cotReq.setEdadAsegurado(Integer.parseInt(conductor.getEdad()));
        
        cotReq.setCveProdComercial(est.getProducto().getClaveComercial().trim());
        cotReq.setCveProdTecnico(est.getProducto().getClaveTecnico());
        
        cotReq.setFchCancelacionCotizacion(Calendar.getInstance());
        cotReq.setFchInicioCotizacion(getCalendar(cotNegReq.getIniVig()).orElseThrow(
                () -> new ExecutionError(Constantes.ERROR_6, "CotCotizacion: no se pudo parsear la fecha de inicio")));
        cotReq.setFchFinCotizacion(getCalendar(cotNegReq.getFinVig()).orElseThrow(
                () -> new ExecutionError(Constantes.ERROR_6, "CotCotizacion: no se pudo parsear la fecha de fin")));

        cotReq.setIdAgente(cotNegReq.getUsuario());
        cotReq.setIdCodigoPromocion(cotNegReq.getCodigoPromocion());

        cotReq.setCodIntermediario(getCodIntermediario(cotNeg));

        CotCotizacionMapper.fillPrimas(cotReq, cotNeg);

        cotReq.setNombreCotizacion("COTIZACION WSP");
        cotReq.setNombres("");
        cotReq.setNumCotizacion(cotNegReq.getIdCotizacion());
        cotReq.setNumPolizaAnterior("");
        cotReq.setXmlDetalle(getXml(cotNeg, cotReq, descripciones));

        cotCotizacionClient.guardar(cotReq);
    }
    
    /**
     * Gets the persona.
     *
     * @param cotNeg the cot neg
     * @param tipo the tipo
     * @return the persona
     */
    private PersonaNeg getPersona(final CotizacionNegocio cotNeg, final String tipo) {
        return cotNeg.getCotizaNegReq().getPersonas().stream().filter(p -> tipo.equals(
                p.getTipo())).findFirst().orElseThrow(() -> new ExecutionError(Constantes.ERROR_34));
    }
    
    /**
     * Gets the descripciones.
     *
     * @param cotNegReq the cot neg req
     * @return the descripciones
     */
    private Map<String, String> getDescripciones(final CotizaNegReq cotNegReq) {
        VehiculoNeg vehiculo = cotNegReq.getVehiculo();
        
        Map<String, Map<String, String>> request = new LinkedHashMap<>();
        request.put("VEHICULOS", of(
                TIPO_VEHICULO, vehiculo.getTipoVehiculo(), 
                "ARMADORA", vehiculo.getArmadora(), 
                "CARROCERIA", vehiculo.getCarroceria(), 
                "VERSION", vehiculo.getVersion(), 
                "MODELO", vehiculo.getModelo()));
        request.put(SUB_RAMO, of(SUB_RAMO, vehiculo.getSubRamo()));
        request.put(TIPO_VEHICULO, of(TIPO_VEHICULO, vehiculo.getTipoVehiculo()));
        request.put(USO_VEHICULO, of(USO_VEHICULO, vehiculo.getUso()));
        request.put(FORMA_INDEMNIZACION, of(FORMA_INDEMNIZACION, vehiculo.getFormaIndemnizacion()));
        request.put(VIA_PAGO, of(VIA_PAGO, cotNegReq.getViaPago()));
        ifNotEmpty(vehiculo.getTipoCarga(), c -> {
            request.put(TIPO_CARGA_VEHICULO, of(TIPO_CARGA_VEHICULO, c));
        });
        
        Map<String, List<ElementoReq>> response = catalogoDomain.getCatalogos(request);
        
        Map<String, String> result = Utils.asMap(new String[][] {
            {DESC_VEHICULO, Utils.ifEmpty(getValorElementoCat(response, "VEHICULOS", "VERSION"), "--")},
            {DESC_SUBRAMO, getValorElementoCat(response, SUB_RAMO)},
            {DESC_TIPO, getValorElementoCat(response, TIPO_VEHICULO)},
            {DESC_USO, getValorElementoCat(response, USO_VEHICULO)},
            {DESC_FORMA_INDEMNIZACION, getValorElementoCat(response, FORMA_INDEMNIZACION)},
            {DESC_VIA_PAGO, getValorElementoCat(response, VIA_PAGO)}});
        ifNotEmpty(vehiculo.getTipoCarga(), c -> {
            result.put(DESC_TIPO_CARGA_VEHICULO, getValorElementoCat(response, TIPO_CARGA_VEHICULO));
        });
        return result;
    }
    
    /**
     * Gets the valor elemento cat.
     *
     * @param catalagos the catalagos
     * @param nombreCatalogo the nombre catalogo
     * @return the valor elemento cat
     */
    private String getValorElementoCat(final Map<String, List<ElementoReq>> catalagos, final String nombreCatalogo) {
        return getValorElementoCat(catalagos, nombreCatalogo, null);
    }
    
    /**
     * Gets the valor elemento cat.
     *
     * @param catalagos the catalagos
     * @param nombreCatalogo the nombre catalogo
     * @param nombreElemento the nombre elemento
     * @return the valor elemento cat
     */
    private String getValorElementoCat(final Map<String, List<ElementoReq>> catalagos, final String nombreCatalogo, 
            final String nombreElemento) {
        Stream<ElementoReq> cat = catalagos.get(nombreCatalogo).stream();
        if (nombreElemento == null) {
            return cat.map(e -> e.getNombre()).findFirst().orElse("");
        } else {
            return cat.filter(e -> e.getNombre().equals(nombreElemento)).map(e -> e.getValor()).findFirst().orElse("");
        }
    }
    
    /**
     * Find elemento.
     *
     * @param elementos the elementos
     * @param nombreElemento the nombre elemento
     * @return the string
     */
    private String findElemento(final List<ElementoNeg> elementos, final String nombreElemento) {
        return Utils.filterFirst(elementos, e -> nombreElemento.equals(e.getNombre())).map(
                e -> e.getValor()).orElse(null);
    }
    
    /**
     * Gets the calendar.
     *
     * @param dateStr the date str
     * @return the calendar
     */
    private static Optional<Calendar> getCalendar(final String dateStr) {
        Calendar c = Calendar.getInstance();
        Optional<Date> d = DateUtils.parseDate(dateStr, "yyyyMMdd");
        if (!d.isPresent()) {
            return Optional.empty();
        }
        c.setTime(d.get());
        return Optional.of(c);
    }

    /**
     * El codigo de intermediario a guardar en COT_COTIZACION sera el principal configurado en el SICA, si no hay uno 
     * configurado, se utilizara el codigo intermediario indicado en los elementos adicionales, y si este ultimo no
     * existe se coloca cadena vacia.
     *
     * @param cotNeg the cot neg
     * @return the cod intermediario
     */
    private String getCodIntermediario(final CotizacionNegocio cotNeg) {
        Optional<String> codIntermediarioSica = Utils.flatMap(cotNeg.getUmoService().getDominios().getIntermediarios(), 
                a -> Utils.filterFirst(a.getAgentes(), b -> b.isPrincipal())).map(
                        a -> a.getIntermediario().getCodigoIntermediario());
        
        String codIntermediario = codIntermediarioSica.orElse(
                findElemento(cotNeg.getCotizaNegReq().getElementos(), "COD_INTERMEDIARIO"));

        return Utils.ifEmpty(codIntermediario, "");
    }
    
    /**
     * Gets the xml.
     *
     * @param cotNeg the cot neg
     * @param cotReq the cot req
     * @param descripciones the descripciones
     * @return the xml
     */
    private String getXml(final CotizacionNegocio cotNeg, final CotCotizacionReq cotReq, 
            final Map<String, String> descripciones) {
        CotizaNegReq cotNegReq = cotNeg.getCotizaNegReq();
        List<PaqueteResp> paqsResp = cotNeg.getTraductorResp().getPaquetes().getPaquetes();
        String periodicidad = getPeriodicidad(paqsResp.get(0));
        
        List<ElementoCot> elementos = new ArrayList<>();
        elementos.add(new ElementoCot("MTO_PRIMA_TOTAL", CotCotizacionMapper.getMtoPrimaTotal(cotNeg).toString()));
        elementos.add(new ElementoCot("CVE_FORMA_PAGO", periodicidad));
        elementos.add(new ElementoCot("CDPRODCO", cotReq.getCveProdTecnico() + ":" + cotReq.getCveProdComercial()));
        elementos.add(new ElementoCot("PRODUCTO_CONFIGURADOR", 
                Utils.concat(", ", mapToList(paqsResp, p -> Utils.trim(p.getDescPaquete())))));
        elementos.add(new ElementoCot("ID_NEGOCIO_OPERABLE", cotNegReq.getIdUMO()));
        elementos.add(new ElementoCot("DESCRIPCION_VEHICULO", descripciones.get(DESC_VEHICULO)));
        elementos.add(new ElementoCot("json", getXmlDetalleCot(cotNeg, cotReq, descripciones)));

        ElementosCotReq elementosRoot = new ElementosCotReq(Arrays.asList(new ElementosCot(elementos)));
        
        return XmlUtils.marshal(elementosRoot, ElementosCotReq.class);
    }
    
    /**
     * Gets the periodicidad.
     *
     * @param paquete the paquete
     * @return the periodicidad
     */
    private String getPeriodicidad(final PaqueteResp paquete) {
        return paquete.getTotales().getTotales().get(0).getCvePeriodicidad();
    }
    
    /**
     * Gets the xml detalle cot.
     *
     * @param cotNeg the cot neg
     * @param cotReq the cot req
     * @param descripciones the descripciones
     * @return the xml detalle cot
     */
    private DetalleCotizacionPadre getXmlDetalleCot(final CotizacionNegocio cotNeg, final CotCotizacionReq cotReq,
            final Map<String, String> descripciones) {
        CotizaNegReq cotNegReq = cotNeg.getCotizaNegReq();
        UmoServiceResp umoResp = cotNeg.getUmoService();
        Negocio neg = umoResp.getNegocio();
        ModeloNegocio modeloNeg = neg.getModeloNegocio();
        Integer versionNeg = neg.getVersion();
        
        DetalleCotizacionPadre detalleCotPadre = new DetalleCotizacionPadre();
        DetalleCotizacion cot = new DetalleCotizacion();
        cot.setOrigen("8");
        cot.setDiasVigencia(DateUtils.daysBetween(cotReq.getFchInicioCotizacion().getTime(), 
                cotReq.getFchFinCotizacion().getTime()));
        cot.setCveHerramienta("WSP");
        cot.setUbicacionTrabajo("");
        cot.setIdModeloNegocio(modeloNeg.getId());
        cot.setModeloNegocio(modeloNeg.getDescripcion());
        cot.setIdNegocioOperable(cotNegReq.getIdUMO());
        cot.setVersionNegocio(versionNeg);
        cot.setIdNegocioComercial(neg.getEstructura().getNegocioComercial().getClave());
        
        cot.setDetalleTarificador(getDetalleTarificador(cotNeg));
        cot.setDetalleConductor(getDetalleConductor(cotNeg));
        cot.setDetalleContratante(getDetalleContratante(cotNeg));
        cot.setDetalleVehiculo(getDetalleVehiculo(cotNeg, descripciones.get(DESC_VEHICULO)));
        cot.setDetalleEtiquetas(getDetalleEtiquetas(cotNeg));
        cot.setDetalleDescuentos(getDescuentoDetalle(cotNeg));
        cot.setDetalleCobranza(getCobranzaDetalle(cotNeg));
        cot.setCveTipoIntermediario("");
        cot.setIdProductoPersonalizado(cot.getDetalleTarificador().getProducto().get(0).getIdProducto());
        cot.setCveFilialSubdependencia("");
        cot.setCveEmpresaDependencia("");
        cot.setIdActor(cotNegReq.getUsuario());
        cot.setJsonPintador(getJsonPintador(cotNeg, cotReq, descripciones, 
                cot.getDetalleVehiculo().getValorVehiculo()));
        
        detalleCotPadre.setDetalleCotizacion(cot);
        return detalleCotPadre;
    }

    /**
     * Gets the productos tarificador.
     *
     * @param cotNeg the cot neg
     * @return the productos tarificador
     */
    private List<Producto> getProductosTarificador(final CotizacionNegocio cotNeg) {
        return mapToList(cotNeg.getRespMuc().getPETICION().get(0).getDATOSPRODUCTOS(), this::getProducto);
    }

    /**
     * Gets the producto.
     *
     * @param prodDto the prod dto
     * @return the producto
     */
    private Producto getProducto(final ProductosDto prodDto) {
        CotizacionDto cot = prodDto.getDATOSCOTIZACION().get(0);
        FormaPagoReciboDto recibo = cot.getFORMAPAGORECIBO();
        
        Producto prod = new Producto();

        prod.setIdProducto(prodDto.getIDPRODUCTO());
        prod.setNombreProducto(prodDto.getNOMBREPRODUCTO());
        prod.setFchTarifa(prodDto.getFECHATARIFA().toString());
        prod.setCveTarifa(prodDto.getCLAVETARIFA().toString());
        prod.setCveZonificacion(prodDto.getCLAVEZONIFICACION());
        prod.setVersionZonificacion(prodDto.getVERSIONZONIFICACION());
        prod.setCoberturas(mapToList(cot.getCOBERTURAPRIMA(), this::getCobertura));
        prod.setTotales(getTotales(prodDto.getDATOSCOTIZACION().get(0).getTOTALPRIMA()));
        prod.setVersionRecargoFormaPago(recibo.getVERSIONRECARGOFORMAPAGO());
        
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setCveFormaPago(recibo.getCLAVEFORMAPAGO());
        cotizacion.setMtoPrimerRec(recibo.getMONTOPRIMERRECIBO().toString());
        cotizacion.setMtoRecSub(recibo.getMONTORECIBOSUBSECUENTE().toString());
        cotizacion.setMtoTotal(recibo.getMONTOTOTAL().toString());
        cotizacion.setNumPago(recibo.getNUMEROPAGOS().toString());
        prod.setCotizacion(cotizacion);
        
        prod.setDescuentosTarificador(getDescuentoReq(prodDto));
        
        return prod;
    }

    /**
     * Gets the totales.
     *
     * @param totalesDto the totales dto
     * @return the totales
     */
    private List<TotalPrima> getTotales(final TotalPrimaDto totalesDto) {
        TotalPrima tot = new TotalPrima();
        tot.setPrima(mapToList(totalesDto.getPRIMA(), this::getPrima));
        tot.setConceptoEconomico(mapToList(totalesDto.getCONCEPTOECONOMICO(), this::getConcepto));
        return Arrays.asList(tot);
    }

    /**
     * Gets the cobertura.
     *
     * @param cob the cob
     * @return the cobertura
     */
    private Cobertura getCobertura(final CoberturaPrimaDto cob) {
        Cobertura cobert = new Cobertura();
        cobert.setCveCobertura(cob.getCLAVECOBERTURA());
        cobert.setPrimas(mapToList(cob.getPRIMA(), this::getPrima));
        cobert.setConceptoEconomico(mapToList(cob.getCONCEPTOECONOMICO(), this::getConcepto));
        return cobert;
    }
    
    /**
     * Gets the prima.
     *
     * @param concepto the concepto
     * @return the prima
     */
    private Prima getPrima(final ConceptoDto concepto) {
        return new Prima(concepto.getNOMBRE(), concepto.getMONTO());
    }
    
    /**
     * Gets the concepto.
     *
     * @param concepto the concepto
     * @return the concepto
     */
    private ConceptoEconomico getConcepto(final ConceptoDto concepto) {
        return new ConceptoEconomico(concepto.getNOMBRE(), concepto.getMONTO());
    }

    /**
     * Gets the detalle tarificador.
     *
     * @param cotNeg the cot neg
     * @return the detalle tarificador
     */
    private DetalleTarificador getDetalleTarificador(final CotizacionNegocio cotNeg) {
        Tarifa tarifa = cotNeg.getUmoService().getDominios().getCombinaciones().get(0).getPaquetes().get(0)
                .getConfiguracion().getReglaTransformacion();
        
        DetalleTarificador tarificador = new DetalleTarificador();

        DatosTransformacion transformacion = new DatosTransformacion();
        transformacion.setFchTransformacion(tarifa.getFecha());
        transformacion.setVersionTransformacion(tarifa.getClave());
        tarificador.setDatosTransformacion(transformacion);

        tarificador.setProducto(getProductosTarificador(cotNeg));
        return tarificador;
    }
    
    /**
     * Gets the detalle etiquetas.
     *
     * @param cotNeg the cot neg
     * @return the detalle etiquetas
     */
    private DetalleEtiqueta getDetalleEtiquetas(final CotizacionNegocio cotNeg) {
        DetalleEtiqueta etiquetas = new DetalleEtiqueta();
        etiquetas.setCodigoPromocion(cotNeg.getCotizaNegReq().getCodigoPromocion());
        return etiquetas;
    }

    /**
     * Gets the detalle contratante.
     *
     * @param cotNeg the cot neg
     * @return the detalle contratante
     */
    private DetalleContratante getDetalleContratante(final CotizacionNegocio cotNeg) {
        PersonaNeg contratante = getPersona(cotNeg, Constantes.STR_CONTRATANTE);
        DetalleContratante detalleContratante = new DetalleContratante();
        detalleContratante.setTipoPersona(contratante.getTipoPersona());
        detalleContratante.setTelefono(getElemento(cotNeg, "NUM_TELEFONO").orElse(""));
        detalleContratante.setNombres(getElemento(cotNeg, "NOMBRES").orElse(""));
        detalleContratante.setCorreo(getElemento(cotNeg, "CORREO_ELECTRONICO").orElse(""));
        detalleContratante.setTipoTelefono(getElemento(cotNeg, "TIPO_TELEFONO").orElse(""));
        detalleContratante.setTelefonoContacto(getElemento(cotNeg, "TEL_CONTACTO").orElse(""));
        detalleContratante.setHoraInicio(getElemento(cotNeg, "HORA_INICIO").orElse(""));
        detalleContratante.setHoraFin(getElemento(cotNeg, "HORA_FIN").orElse(""));
        
        return detalleContratante;
    }

    /**
     * Gets the detalle conductor.
     *
     * @param cotNeg the cot neg
     * @return the detalle conductor
     */
    private DetalleConductor getDetalleConductor(final CotizacionNegocio cotNeg) {
        PersonaNeg conductor = getPersona(cotNeg, Constantes.STR_CONDUCTOR);
        DetalleConductor detalleConductor = new DetalleConductor();
        detalleConductor.setEdad(conductor.getEdad());
        detalleConductor.setGenero(MAPEO_SEXO.get(conductor.getSexo()));
        return detalleConductor;
    }

    /**
     * Gets the descuento detalle.
     *
     * @param cotNeg the cot neg
     * @return the descuento detalle
     */
    private DetalleDescuento getDescuentoDetalle(final CotizacionNegocio cotNeg) {
        DetalleDescuento detalleDesc = new DetalleDescuento();
        detalleDesc.setDescuentos(getDescuentosCot(cotNeg));
        return detalleDesc;
    }
    
    /**
     * Gets the descuentos cot.
     *
     * @param cotNeg the cot neg
     * @return the descuentos cot
     */
    private List<Descuento> getDescuentosCot(final CotizacionNegocio cotNeg) {
        List<DescuentoDto> descuentos = cotNeg.getReqMuc().getDATOSCOTIZACION().get(0).getDATOSPRODUCTO()
                .get(0).getDESCUENTO();
        return mapToList(descuentos, 
                d -> new Descuento(d.getCLAVEDESCUENTO(), d.getCOEFICIENTE().toString(), d.getUNIDADMEDIDA()));
    }

    /**
     * Gets the descuento req.
     *
     * @param prodDto the prod dto
     * @return the descuento req
     */
    private List<DescuentoTarificador> getDescuentoReq(final ProductosDto prodDto) {
        List<ConceptoDto> descuentos = prodDto.getDATOSCOTIZACION().get(0).getTOTALPRIMA().getDETALLEDESCUENTO();
        return mapToList(descuentos, d -> new DescuentoTarificador(d.getNOMBRE(), d.getMONTO()));
    }

    /**
     * Gets the detalle vehiculo.
     *
     * @param cotNeg the cot neg
     * @param descVehiculo the desc vehiculo
     * @return the detalle vehiculo
     */
    private DetalleVehiculo getDetalleVehiculo(final CotizacionNegocio cotNeg, final String descVehiculo) {
        VehiculoNeg vehiculo = cotNeg.getCotizaNegReq().getVehiculo();
        PersonaNeg conductor = getPersona(cotNeg, Constantes.STR_CONDUCTOR);
        
        DetalleVehiculo detalleVehiculo = new DetalleVehiculo();
        detalleVehiculo.setIdSubRamo(vehiculo.getSubRamo());
        detalleVehiculo.setIdUsoVehiculo(vehiculo.getUso());
        detalleVehiculo.setTipoVehiculo(vehiculo.getTipoVehiculo());
        detalleVehiculo.setArmadora(vehiculo.getArmadora());
        detalleVehiculo.setCarroceria(vehiculo.getCarroceria());
        detalleVehiculo.setMarca(vehiculo.getVersion());
        detalleVehiculo.setModelo(vehiculo.getModelo());
        detalleVehiculo.setDescripcionVehiculo(descVehiculo);
        detalleVehiculo.setDescripcionVehiculoFactura(vehiculo.getDescripcionFactura());
        
        Stream.of(vehiculo.getValorFactura(), vehiculo.getValorConvenido(), vehiculo.getValorVehiculo()).forEach(v -> {
            if (!Utils.isEmpty(v)) {
                detalleVehiculo.setValorVehiculo(v);
            }
        });

        detalleVehiculo.setCodigoPostal(conductor.getDomicilio().getCodigoPostal());
        detalleVehiculo.setIdEstadoCirculacion(cotNeg.getCatalogoResp().getEstadoCirculacion());
        detalleVehiculo.setIdValorVehiculo(vehiculo.getFormaIndemnizacion());
        detalleVehiculo.setTipoCarga(vehiculo.getTipoCarga());
        return detalleVehiculo;
    }
    
    /**
     * Gets the cobranza detalle.
     *
     * @param cotNeg the cot neg
     * @return the cobranza detalle
     */
    private DetalleCobranza getCobranzaDetalle(final CotizacionNegocio cotNeg) {
        CotizaNegReq cotNegReq = cotNeg.getCotizaNegReq();
        DetalleCobranza cobranza = new DetalleCobranza();
        cobranza.setPeriodicidad(getPeriodicidad(cotNeg.getTraductorResp().getPaquetes().getPaquetes().get(0)));
        cobranza.setViaPago(cotNegReq.getViaPago());
        cobranza.setTipoNomina(cotNegReq.getCveTipoNomina());
        return cobranza;
    }

    /**
     * Gets the json pintador.
     *
     * @param cotNeg the cot neg
     * @param cotReq the cot req
     * @param descripciones the descripciones
     * @param valorVehiculo the valor vehiculo
     * @return the json pintador
     */
    private String getJsonPintador(final CotizacionNegocio cotNeg, final CotCotizacionReq cotReq,
            final Map<String, String> descripciones, final String valorVehiculo) {
        CotizaNegReq cotNegReq = cotNeg.getCotizaNegReq();
        UmoServiceResp umoResp = cotNeg.getUmoService();
        Negocio neg = umoResp.getNegocio();
        VehiculoNeg vehiculo = cotNegReq.getVehiculo();
        PersonaNeg contratante = getPersona(cotNeg, Constantes.STR_CONTRATANTE);
        PersonaNeg conductor = getPersona(cotNeg, Constantes.STR_CONDUCTOR);
        
        JsonSimplificado json = new JsonSimplificado();
        
        ifNotEmpty(cotNegReq.getCodigoPromocion(), codProm -> {
            // Buscar la descripcion del codigo promocion
            UmoServiceResp nodosResp = sicaNegociosClient.getNodos(neg.getId(), neg.getVersion());
            json.setCodigoPromocion(
                    Utils.filterAndMapFirst(nodosResp.getHijos(), 
                            h -> Utils.filterTrim(h.getCodigoPromocion()).map(c -> c.equals(codProm)).orElse(false), 
                            h -> Utils.trim(h.getNombre())).orElse(null));
            
        });
        
        // Generales
        json.setNegocio(cotNeg.getUmoService().getNegocio().getNombre());
        json.setFechaInicio(DateUtils.formatDate(cotReq.getFchInicioCotizacion().getTime(), MX_DATE_FORMAT));
        json.setFechaFin(DateUtils.formatDate(cotReq.getFchFinCotizacion().getTime(), MX_DATE_FORMAT));
        
        // Vehiculo
        Vehiculo veh = new Vehiculo();
        veh.setProcedencia(new Item(vehiculo.getSubRamo(), descripciones.get(DESC_SUBRAMO)));
        veh.setTipo(descripciones.get(DESC_TIPO));
        veh.setUso(descripciones.get(DESC_USO));
        veh.setAnio(vehiculo.getModelo());
        veh.setDescripcion(descripciones.get(DESC_VEHICULO));
        veh.setDescripcionFactura(Utils.ifEmpty(vehiculo.getDescripcionFactura(), "-"));
        veh.setFormaIndemnizacion(new Item(vehiculo.getFormaIndemnizacion(),
                descripciones.get(DESC_FORMA_INDEMNIZACION)));
        veh.setNumSerie(vehiculo.getSerie());
        veh.setValor(Utileria.moneyWithoutCents(valorVehiculo));
        veh.setTipoCarga(descripciones.get(DESC_TIPO_CARGA_VEHICULO));
        
        JsonSimplificadoMapper.fillBlindaje(veh, cotNeg);
        
        json.setVehiculo(veh);
        
        // Contratante
        Contratante cont = new Contratante(contratante.getTipoPersona());
        json.setContratante(cont);
        
        // Conductor
        ConductorHabitual cond = new ConductorHabitual();
        cond.setCodigoPostal(conductor.getDomicilio().getCodigoPostal());
        if (Constantes.TIPO_PERSONA_FISICA.equals(contratante.getTipoPersona())) {
            cond.setSexo(MAPEO_SEXO.get(conductor.getSexo()));
            DateUtils.parseDate(conductor.getFecNacimiento(), "yyyyMMdd").ifPresent(f -> {
                cond.setFechaNacimiento(DateUtils.formatDate(f, MX_DATE_FORMAT));
            });
        }
        json.setConductorHabitual(cond);
        
        // Via Pago
        json.setViaPago(new Item(cotNegReq.getViaPago(), descripciones.get(DESC_VIA_PAGO)));
        
        // Catalogo de Coberturas
        json.setCatalogoCoberturas(createCatalogoCoberturas(cotNeg));
        
        // Productos
        JsonSimplificadoMapper.fillProductos(cotNeg, json);
        
        // Periodicidad y Descuentos
        List<PaqueteResp> paqs = cotNeg.getTraductorResp().getPaquetes().getPaquetes();
        Utils.filterFirst(paqs).ifPresent(p -> {
            json.setPeriodicidad(Utils.trim(p.getTotales().getTotales().get(0).getDescPeriodicidad()));
        });
        
        // Descuentos
        JsonSimplificadoMapper.fillDescuentos(paqs, json, umoResp);
        
        return stringify(json);
    }
    
    /**
     * Obtiene el catalogo de coberturas que se muestra en la parte izquierda del comparador.
     *
     * @param cotNeg the cot neg
     * @return the list
     */
    private List<CatalogoCobertura> createCatalogoCoberturas(final CotizacionNegocio cotNeg) {
        List<Paquete> paqsUmo = cotNeg.getUmoService().getDominios().getCombinaciones().get(0).getPaquetes();
        List<CatalogoCobertura> catalogo = new ArrayList<>();
        
        // Ordenar las coberturas de todos los paquetes quitando las coberturas elite y agregarlas al catalogo
        Map<Integer, CoberturaUmo> cobOrdenadasMap = new TreeMap<>();
        flatMapToList(paqsUmo, p -> p.getCoberturas().stream().filter(c -> !c.getElite())).forEach(c -> {
            cobOrdenadasMap.put(c.getCobertura().getOrden(), c);
        });
        addCoberturasCatalogo(catalogo, cobOrdenadasMap.values(), "");
        
        // Por cada paquete, ordenar solo las coberturas elite y agregarlas al catalogo
        Map<String, Set<CoberturaUmo>> cobsEliteMap = new LinkedHashMap<>();
        paqsUmo.forEach(p -> {
            if (!Utils.isEmpty(p.getTipoElite())) {
                p.getCoberturas().stream().filter(c -> c.getElite()).forEach(c -> {
                    Set<CoberturaUmo> cobs = cobsEliteMap.get(p.getTipoElite());
                    if (cobs == null) {
                        cobs = new HashSet<>();
                        cobsEliteMap.put(p.getTipoElite(), cobs);
                    }
                    cobs.add(c);
                });
            }
        });
        cobsEliteMap.forEach((tipoElite, cobsElite) -> {
            List<CoberturaUmo> cobs = cobsElite.stream().filter(c -> c.getElite()).sorted((a, b) -> 
                Integer.compare(a.getCobertura().getOrden(), b.getCobertura().getOrden())).collect(Collectors.toList());
            addCoberturasCatalogo(catalogo, cobs, Constantes.AGRUPADOR_ELITE_MAP.get(tipoElite));
        });
        
        return catalogo;
    }
    
    /**
     * Adds the coberturas catalogo.
     *
     * @param catalogo the catalogo
     * @param coberturas the coberturas
     * @param agrupador the agrupador
     */
    private void addCoberturasCatalogo(final List<CatalogoCobertura> catalogo, 
            final Collection<CoberturaUmo> coberturas, final String agrupador) {
        coberturas.forEach(c -> {
            Elemento e = c.getCobertura();
            catalogo.add(new CatalogoCobertura(e.getClave(), e.getNombre(), agrupador));
        });
    }
    
    /**
     * Gets the elemento.
     *
     * @param cotNeg the cot neg
     * @param nombre the nombre
     * @return the elemento
     */
    private Optional<String> getElemento(final CotizacionNegocio cotNeg, final String nombre) {
        return Utils.filterAndMapFirst(cotNeg.getCotizaNegReq().getElementos(), e -> e.getNombre().equals(nombre), 
                e -> e.getValor());
    }
    
    /**
     * Stringify.
     *
     * @param obj the obj
     * @return the string
     */
    private String stringify(final Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            Logger.getRootLogger().error(e);
        }
        
        return "";
    }
}