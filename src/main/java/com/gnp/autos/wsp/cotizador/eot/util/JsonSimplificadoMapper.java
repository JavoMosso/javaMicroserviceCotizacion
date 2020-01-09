package com.gnp.autos.wsp.cotizador.eot.util;

import static com.gnp.autos.wsp.cotizador.eot.util.Constantes.ERROR_6;
import static com.gnp.autos.wsp.cotizador.eot.util.Constantes.STR_UNO;
import static com.gnp.autos.wsp.cotizador.eot.util.Utileria.money;
import static com.gnp.autos.wsp.negocio.util.Utils.filterAndMapFirst;
import static com.gnp.autos.wsp.negocio.util.Utils.filterFirst;
import static com.gnp.autos.wsp.negocio.util.Utils.parseDouble;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.Blindaje;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.Cobertura;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.Item;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.JsonSimplificado;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.Producto;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.Tarificacion;
import com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado.Vehiculo;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CoberturaDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.DatosProductoDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.ModificadorCoberturaDto;
import com.gnp.autos.wsp.negocio.cotizacion.model.resp.CoberturaResp;
import com.gnp.autos.wsp.negocio.cotizacion.model.resp.ConceptoEconomicoResp;
import com.gnp.autos.wsp.negocio.cotizacion.model.resp.PaqueteResp;
import com.gnp.autos.wsp.negocio.cotizacion.model.resp.TotalPrimaResp;
import com.gnp.autos.wsp.negocio.neg.model.AdaptacionVehNeg;
import com.gnp.autos.wsp.negocio.umoservice.model.CoberturaUmo;
import com.gnp.autos.wsp.negocio.umoservice.model.DescuentoUmo;
import com.gnp.autos.wsp.negocio.umoservice.model.Paquete;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;
import com.gnp.autos.wsp.negocio.util.DateUtils;
import com.gnp.autos.wsp.negocio.util.Utils;

/**
 * The Class CotCotizacionMapper.
 */
public final class JsonSimplificadoMapper {
    /**
     * Constructor privado para evitar creacion de objetos.
     */
    private JsonSimplificadoMapper() { }
    
    /** The Constant COBERTURA_ADAPTACIONES. */
    private static final String COBERTURA_ADAPTACIONES = "0000000894";
    
    /** The Constant DESCUENTO_CAMPANIAS. */
    private static final String DESCUENTO_CAMPANIAS = "PODESCAM";
    
    /** The Constant ID_TIPO_DESCUENTO_COMERCIAL. */
    private static final int ID_TIPO_DESCUENTO_COMERCIAL = 1;
    
    /** The Constant DECIMAL_FORMAT. */
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.##");
    
    /**
     * Fill productos.
     *
     * @param cotNeg the cot neg
     * @param json the json
     */
    public static void fillProductos(final CotizacionNegocio cotNeg, final JsonSimplificado json) {
        List<PaqueteResp> paquetesResp = cotNeg.getTraductorResp().getPaquetes().getPaquetes();
        List<DatosProductoDto> productosMuc = cotNeg.getReqMuc().getDATOSCOTIZACION().get(0).getDATOSPRODUCTO();
        List<Paquete> paquetesUmo = cotNeg.getUmoService().getDominios().getCombinaciones().get(0).getPaquetes();
        json.setProductos(Utils.mapToList(paquetesResp, p -> {
            Producto prod = new Producto();
            
            prod.setId(p.getCvePaquete());
            prod.setDescripcion(Utils.trim(p.getDescPaquete()));
            
            DatosProductoDto prodMuc = filterFirst(productosMuc, d -> d.getIDPRODUCTO().equals(p.getCvePaquete()) 
                    && d.getNOMBREPRODUCTO().equals(p.getDescPaquete())).orElseThrow(
                    () -> new ExecutionError(ERROR_6, "CotCotizacion: no se encontro el paquete en MUC"));
            
            Paquete paqUmo = filterFirst(paquetesUmo, e -> e.getProductoPersonalizado().equals(p.getCvePaquete()))
                    .orElseThrow(() -> new ExecutionError(ERROR_6, "CotCotizacion: no se encontro el paquete Umo"));

            // El id (valor base del modificador) de suma y de deducible se toman del request hacia el MUC
            // La descripcion (valor formateado) se toma de la respuesta del cotizador (PaqueteResp)
            prod.setCoberturas(Utils.mapToList(p.getCoberturas().getCoberturas(), c -> {
                CoberturaDto cobMuc = filterFirst(prodMuc.getCOBERTURA(), 
                        cm -> cm.getCLAVECOBERTURA().equals(c.getCveCobertura())).orElseThrow(
                        () -> new ExecutionError(ERROR_6, "CotCotizacion: no se encontro cobertura en MUC"));
                CoberturaUmo cobUmo = filterFirst(paqUmo.getCoberturas(), 
                        cu -> cu.getCobertura().getClave().equals(c.getCveCobertura())).orElseThrow(
                        () -> new ExecutionError(ERROR_6, "CotCotizacion: no se encontro cobertura Umo"));
                String agrupador = cobUmo.getElite() ? Constantes.AGRUPADOR_ELITE_MAP.get(paqUmo.getTipoElite()) : "";
                Cobertura cob = mapCobertura(c, cobMuc, agrupador);
                if (COBERTURA_ADAPTACIONES.equals(c.getCveCobertura())) {
                    ajustarCoberturaAdaptaciones(cob, cotNeg);
                }
                return cob;
            }));

            List<TotalPrimaResp> totPrimas = p.getTotales().getTotales();
            List<ConceptoEconomicoResp> conceptos = totPrimas.get(0).getConceptosEconomicos();

            prod.setAnualidad(moneyConcepto(conceptos, "PRIMER_RECIBO"));
            prod.setParcialidad(moneyConcepto(conceptos, "RECIBO_SUBSECUENTE"));
            prod.setPrimaTotal(moneyConcepto(conceptos, "TOTAL_PAGAR"));
            prod.setDescuento(getDescuento(conceptos));

            Tarificacion tarificacion = new Tarificacion();
            tarificacion.setMtoPrimerRecibo(doubleConcepto(conceptos, "PRIMER_RECIBO"));
            tarificacion.setMtoReciboSubsecuente(doubleConcepto(conceptos, "RECIBO_SUBSECUENTE"));
            tarificacion.setNumPagos(intConcepto(conceptos, "NUM_PAGOS"));
            tarificacion.setNumRecibosSubsecuentes(intConcepto(conceptos, "NUM_RECIBOS_SUBS"));
            Utils.ifPresent(totPrimas.get(0).getResultadoParcialidad(), q -> q.getNumParcialidades(), np -> {
                tarificacion.setNumParcialidades(np.toString());
            });
            prod.setTarificacion(tarificacion);
            
            return prod;
        }));
    }
    
    /**
     * Gets the descuento.
     *
     * @param conceptos the conceptos
     * @return the descuento
     */
    private static String getDescuento(final List<ConceptoEconomicoResp> conceptos) {
        return filterAndMapFirst(conceptos, c -> c.getNombre().contains(DESCUENTO_CAMPANIAS),
                c -> Utileria.moneyWithoutCents(Math.abs(Double.parseDouble(c.getMonto())))).orElse(null);
    }
    
    /**
     * Money concepto.
     *
     * @param conceptos the conceptos
     * @param clave the clave
     * @return the string
     */
    private static String moneyConcepto(final List<ConceptoEconomicoResp> conceptos, final String clave) {
        return Utileria.moneyWithoutCents(getMontoConcepto(conceptos, clave));
    }
    
    /**
     * Double concepto.
     *
     * @param conceptos the conceptos
     * @param clave the clave
     * @return the double
     */
    private static double doubleConcepto(final List<ConceptoEconomicoResp> conceptos, final String clave) {
        return Double.parseDouble(getMontoConcepto(conceptos, clave));
    }
    
    /**
     * Int concepto.
     *
     * @param conceptos the conceptos
     * @param clave the clave
     * @return the int
     */
    private static int intConcepto(final List<ConceptoEconomicoResp> conceptos, final String clave) {
        return Integer.parseInt(getMontoConcepto(conceptos, clave));
    }
    
    /**
     * Map cobertura.
     *
     * @param cobResp the cob resp
     * @param cobMuc the cob muc
     * @param agrupador the agrupador
     * @return the cobertura
     */
    private static Cobertura mapCobertura(final CoberturaResp cobResp, final CoberturaDto cobMuc,
            final String agrupador) {
        Cobertura cob = new Cobertura();
        cob.setId(cobResp.getCveCobertura());
        cob.setAgrupador(agrupador);
        // El id de suma asegurada por defecto es 999999
        cob.setSuma(new Item("999999", cobResp.getSa()));
        getModificador(cobMuc, "CPASEGUR").ifPresent(m -> {
            cob.getSuma().setId(DECIMAL_FORMAT.format(m.getVALORREQUERIDO()));
        });
        getModificador(cobMuc, "CDDEDUCI").ifPresent(m -> {
            cob.setDeducible(new Item(DECIMAL_FORMAT.format(m.getVALORREQUERIDO()), cobResp.getDeducible()));
        });
        return cob;
    }
    
    /**
     * Gets the blindaje.
     *
     * @param cotNeg the cot neg
     * @return the blindaje
     */
    private static Optional<AdaptacionVehNeg> getBlindaje(final CotizacionNegocio cotNeg) {
        List<AdaptacionVehNeg> adaptaciones = cotNeg.getCotizaNegReq().getVehiculo().getAdaptaciones();
        return filterFirst(adaptaciones, a -> STR_UNO.equals(a.getBanEquip()));
    }
    
    /**
     * Restarle a la suma asegurada el blindaje si es que se especifico.
     *
     * @param cob the cob
     * @param cotNeg the cot ne
     */
    private static void ajustarCoberturaAdaptaciones(final Cobertura cob, final CotizacionNegocio cotNeg) {
        getBlindaje(cotNeg).flatMap(a -> parseDouble(a.getMontoSA())).ifPresent(monto -> {
            cob.getSuma().setDescripcion(money(parseDouble(cob.getSuma().getId()).orElse(0.0) - monto));
        });
    }
    
    /**
     * Gets the modificador.
     *
     * @param cobMuc the cob muc
     * @param nombre the nombre
     * @return the modificador
     */
    private static Optional<ModificadorCoberturaDto> getModificador(final CoberturaDto cobMuc, final String nombre) {
        return filterFirst(cobMuc.getMODIFICADOR(), m -> m.getCLAVEMODIFICADOR().equals(nombre));
    }
    
    /**
     * Gets the monto concepto.
     *
     * @param conceptos the conceptos
     * @param clave the clave
     * @return the monto concepto
     */
    private static String getMontoConcepto(final List<ConceptoEconomicoResp> conceptos, final String clave) {
        return filterAndMapFirst(conceptos, c -> c.getNombre().equals(clave), c -> c.getMonto()).orElse("0");
    }
    
    /**
     * Solo llenar los descuentos que esten en umo service y que no sean comerciales.
     *
     * @param paquetesResp the paquetes resp
     * @param json the json
     * @param umoResp the umo resp
     */
    public static void fillDescuentos(final List<PaqueteResp> paquetesResp, final JsonSimplificado json, 
            final UmoServiceResp umoResp) {
        filterFirst(paquetesResp).ifPresent(p -> {
            json.setDescuentos(Utils.filterAndMapToList(p.getDescuentos().getDescuentos(), d -> {
                return filterFirst(umoResp.getDominios().getCobranzas().getDescuentos(), descUmo -> {
                    DescuentoUmo dUmo = descUmo.getDescuento();
                    return dUmo.getCodigo().equals(d.getCveDescuento()) 
                        && Utils.filter(dUmo.getTipo(), u -> u.getId() != ID_TIPO_DESCUENTO_COMERCIAL).isPresent();
                }).isPresent();
            }, d -> new Item(d.getCveDescuento(), Utils.trim(d.getDescripcion()))));
        });
    }
    
    /**
     * Fill blindaje.
     *
     * @param vehiculo the vehiculo
     * @param cotNeg the cot neg
     */
    public static void fillBlindaje(final Vehiculo vehiculo, final CotizacionNegocio cotNeg) {
        getBlindaje(cotNeg).ifPresent(a -> {
            Blindaje blindaje = new Blindaje();
            blindaje.setValor(money(parseDouble(a.getMontoFacturacion()).orElse(0.0)));
            blindaje.setValorDepreciado(money(parseDouble(a.getMontoSA()).orElse(0.0)));
            DateUtils.parseDate(a.getFechaFactura(), "yyyyMMdd").ifPresent(d -> {
                blindaje.setFecha(DateUtils.formatDate(d, "dd/MM/yyyy"));
            });
            vehiculo.setBlindaje(blindaje);
        });
    }
}