package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static com.gnp.autos.wsp.negocio.util.Utils.filterFirst;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gnp.autos.wsp.cotizador.eot.client.DescuentoNominaClient;
import com.gnp.autos.wsp.cotizador.eot.client.FormatoImpresionClient;
import com.gnp.autos.wsp.cotizador.eot.domain.FormatoImpresionDomain;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.descuentonomina.DescuentoNominaReq;
import com.gnp.autos.wsp.cotizador.eot.model.descuentonomina.DescuentoNominaResp;
import com.gnp.autos.wsp.cotizador.eot.model.descuentonomina.ResultadoParcialidad;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.negocio.cotizacion.model.resp.TraductorResp;
import com.gnp.autos.wsp.negocio.formatoimpresion.model.CoberturaFormato;
import com.gnp.autos.wsp.negocio.formatoimpresion.model.ElementoFormato;
import com.gnp.autos.wsp.negocio.formatoimpresion.model.FormatoImpReq;
import com.gnp.autos.wsp.negocio.formatoimpresion.model.PaqueteFormato;
import com.gnp.autos.wsp.negocio.log.TransaccionIntermedia;
import com.gnp.autos.wsp.negocio.neg.model.CoberturaNeg;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegResp;
import com.gnp.autos.wsp.negocio.neg.model.PaqueteNeg;
import com.gnp.autos.wsp.negocio.neg.model.PersonaNeg;
import com.gnp.autos.wsp.negocio.neg.model.ResultadoParcialidadNeg;
import com.gnp.autos.wsp.negocio.neg.model.TotalPrimaNeg;
import com.gnp.autos.wsp.negocio.neg.model.VehiculoNeg;
import com.gnp.autos.wsp.negocio.util.Utils;

/**
 * The Class FormatoRestDomainImpl.
 */
@Component
public class FormatoRestDomainImpl implements FormatoImpresionDomain {

    /** The formato imp client. */
    @Autowired
    private FormatoImpresionClient fFormatoImpClient;

    /** The descuento nomina client. */
    @Autowired
    private DescuentoNominaClient descuentoNominaClient;

    /** The url transaccion intermedia. */
    @Value("${wsp_url_TransaccionIntermedia}")
    private String urlTransaccionIntermedia;

    /** The urlformato imp service. */
    @Value("${wsp_url_FormatoImpresion}")
    private String urlformatoImpService;

    /**
     * Gets the formato imp.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the formato imp
     */
    @Override
    public final CotizacionNegocio getFormatoImp(final CotizacionNegocio cotizacionNegocio) {
        Utileria.getRegistraLogTime("Entra a formatoIMP");
        CotizaNegResp cotNeg = cotizacionNegocio.getCotizaNegResp();
        VehiculoNeg vehNeg = cotizacionNegocio.getCotizaNegReq().getVehiculo();
        String tipoPersona = "F";
        Optional<PersonaNeg> contMoral = cotizacionNegocio.getCotizaNegReq().getPersonas().parallelStream()
                .filter(p -> p.getTipo().equalsIgnoreCase(Constantes.STR_CONTRATANTE)
                        && !p.getTipoPersona().equalsIgnoreCase(Constantes.TIPO_PERSONA_FISICA))
                .findFirst();
        if (contMoral.isPresent()) {
            tipoPersona = "J";
        }

        updDescuentoNominaSADE(cotNeg, cotizacionNegocio);

        FormatoImpReq req = new FormatoImpReq();
        req.setIdNegocio(cotizacionNegocio.getCotizaNegReq().getIdUMO());
        req.setCodigoPromocion(Utileria.getValorString(cotizacionNegocio.getCotizaNegReq().getCodigoPromocion()));
        req.setFormaIndemnizacion(vehNeg.getFormaIndemnizacion());
        req.setTipoPersona(tipoPersona);
        req.setSubRamo(vehNeg.getSubRamo());
        req.setTipoUso(vehNeg.getUso());
        req.setTipoVehiculo(vehNeg.getTipoVehiculo());
        req.setValorVehiculo(Utileria.getValorDouble(vehNeg.getValorVehiculo()));
        req.setVersionNegocio(cotizacionNegocio.getUmoService().getNegocio().getVersion());
        req.setPaquetes(cotNeg.getPaquetes().stream().map(p -> getPaqueteNeg(p, vehNeg)).collect(Collectors.toList()));

        Utileria.pintaObjToJson(req, Constantes.PRINT_FORM_REQ);
        Timestamp tsRequest = new Timestamp(new Date().getTime());
        FormatoImpReq resp = fFormatoImpClient.getFormatoImp(req);
        Utileria.pintaObjToJson(resp, Constantes.PRINT_FORM_RESP);
        TransaccionIntermedia.guardarTxJson(urlTransaccionIntermedia, cotizacionNegocio.getTid(),
                Constantes.CVE_TX_FORMATO_IMP, req, resp, urlformatoImpService, tsRequest);

        cotNeg.getPaquetes().stream().forEach(p -> updPaqueteNeg(p, resp.getPaquetes()));

        cotNeg.setPaquetes(getPaqueteOrder(cotNeg.getPaquetes()));

        cotNeg.setIdCotizacion(cotizacionNegocio.getCotizaNegReq().getIdCotizacion());
        cotizacionNegocio.setCotizaNegResp(cotNeg);

        TraductorResp rsp = new TraductorResp();
        rsp.getTraductor(cotizacionNegocio.getCotizaNegResp());
        cotizacionNegocio.setTraductorResp(rsp);

        return cotizacionNegocio;

    }

    /**
     * Gets the paquete order.
     *
     * @param paquetes the paquetes
     * @return the paquete order
     */
    public List<PaqueteNeg> getPaqueteOrder(final List<PaqueteNeg> paquetes) {

        return paquetes.stream().sorted(
                Comparator.comparing(PaqueteNeg::isPreferente).thenComparing(PaqueteNeg::getMtoTotal).reversed())
                .collect(Collectors.toList());

    }

    /**
     * Gets the paquete neg.
     *
     * @param paqNeg the paq neg
     * @param vehNeg the veh neg
     * @return the paquete neg
     */
    public final PaqueteFormato getPaqueteNeg(final PaqueteNeg paqNeg, final VehiculoNeg vehNeg) {
        PaqueteFormato paquete = new PaqueteFormato();
        paquete.setCvePaquete(paqNeg.getCvePaquete());
        paquete.setDescPaquete(paqNeg.getDescPaquete());

        if (Utileria.stringToBoolean(vehNeg.getAltoRiesgo())) {
            paqNeg.getCoberturas().parallelStream()
                    .filter(p -> p.getCveCobertura().equals(Constantes.CVE_COBERTURA_ROBO)).findFirst()
                    .ifPresent(c -> c.setDeducible(
                            Utileria.getDecimalFormat(Double.parseDouble(vehNeg.getPctDedAltoRiesgo()), 2)));
        }

        List<CoberturaFormato> coberturasFormato = paqNeg.getCoberturas().parallelStream().map(p -> {
            CoberturaFormato cobF = new CoberturaFormato();
            cobF.setCveCobertura(p.getCveCobertura());
            cobF.setSas(getElemFormato(p.getSa()));
            cobF.setDeducibles(getElemFormato(p.getDeducible()));
            return cobF;
        }).collect(Collectors.toList());

        paquete.setCoberturas(coberturasFormato);

        return paquete;

    }

    /**
     * Gets the elem formato.
     *
     * @param valor the valor
     * @return the elem formato
     */
    private static List<ElementoFormato> getElemFormato(final String valor) {
        if (!Utileria.existeValor(valor)) {
            return Collections.emptyList();
        }
        List<ElementoFormato> elemFormat = new ArrayList<>();
        ElementoFormato e = new ElementoFormato();
        e.setValor(valor.trim());
        elemFormat.add(e);
        return elemFormat;
    }

    /**
     * Upd paquete neg.
     *
     * @param paquete   the paquete
     * @param paquetesF the paquetes F
     * @return the paquete neg
     */
    public final PaqueteNeg updPaqueteNeg(final PaqueteNeg paquete, final List<PaqueteFormato> paquetesF) {
        paquetesF.parallelStream()
                .filter(p -> p.getCvePaquete().equalsIgnoreCase(paquete.getCvePaquete())
                        && p.getDescPaquete().trim().equalsIgnoreCase(paquete.getDescPaquete().trim()))
                .findFirst().ifPresent(paqF -> {
                    paquete.setCvePaquete(paqF.getCvePaquete());

                    List<CoberturaNeg> coberturas = paquete.getCoberturas();
                    coberturas.parallelStream().forEach(p -> updCoberturaOrdenNeg(p, paqF.getCoberturas()));
                    paquete.setCoberturas(coberturas);
                    paquete.setTotales(getTotalesOrder(paquete.getTotales()));
                    paquete.getTotales().stream().findFirst().ifPresent(t -> paquete.setMtoTotal(getTotalPrimaSort(t)));

                });
        return paquete;
    }

    /**
     * Gets the totales order.
     *
     * @param totales the totales
     * @return the totales order
     */
    private List<TotalPrimaNeg> getTotalesOrder(final List<TotalPrimaNeg> totales) {
        return totales.stream().sorted(Comparator.comparing(this::getTotalPrimaSort)).collect(Collectors.toList());

    }

    /**
     * Gets the total prima sort.
     *
     * @param totPrimaNeg the tot prima neg
     * @return the total prima sort
     */
    private Double getTotalPrimaSort(final TotalPrimaNeg totPrimaNeg) {

        return totPrimaNeg.getConceptosEconomicos().parallelStream()
                .filter(p -> p.getNombre().equalsIgnoreCase(Constantes.STR_TOTAL_PAGAR))
                .map(cE -> new BigDecimal(cE.getMonto())).findFirst().orElse(new BigDecimal("0")).doubleValue();

    }

    /**
     * Upd cobertura orden neg.
     *
     * @param cobertura   the cobertura
     * @param coberturasF the coberturas F
     * @return the cobertura neg
     */
    public final CoberturaNeg updCoberturaOrdenNeg(final CoberturaNeg cobertura,
            final List<CoberturaFormato> coberturasF) {
        coberturasF.parallelStream().filter(p -> p.getCveCobertura().equalsIgnoreCase(cobertura.getCveCobertura()))
                .findFirst().ifPresent(cobF -> {
                    cobertura.setDeducible(
                            Utileria.existeValor(cobF.getDeducibles()) ? cobF.getDeducibles().get(0).getValorFormato()
                                    : "");
                    cobertura.setSa(Utileria.existeValor(cobF.getSas()) ? cobF.getSas().get(0).getValorFormato() : "");
                    cobertura.setNombre(cobF.getDescCobertura());
                });
        return cobertura;
    }

    /**
     * Upd descuento nomina SADE.
     *
     * @param cotResp           the cot resp
     * @param cotizacionNegocio the cotizacion negocio
     */
    private void updDescuentoNominaSADE(final CotizaNegResp cotResp, final CotizacionNegocio cotizacionNegocio) {
        CotizaNegReq cotReq = cotizacionNegocio.getCotizaNegReq();
        if (!(cotReq.getViaPago().equalsIgnoreCase(Constantes.CVE_DESC_NOMINA)
                || cotReq.getViaPago().equalsIgnoreCase(Constantes.CVE_BANCARIO_NOMINA))) {
            return;
        }
        filterFirst(cotReq.getElementos(), 
            p -> p.getNombre().equalsIgnoreCase(Constantes.CVE_TIPO_NOMINA)).flatMap(
                el -> filterFirst(cotizacionNegocio.getUmoService().getDominios().getCobranzas().getCobranzas(),
                    cob -> cob.getConductoCobro().getClave().equalsIgnoreCase(cotReq.getViaPago())).flatMap(
                        cUm -> filterFirst(cUm.getNominas(),
                            cU -> cU.getTipoNominaNegocio().getId() == Integer.parseInt(el.getClave().trim())))).map(
                                n -> n.getTipoNominaNegocio().getIdCobranza()).ifPresent(
                                    id -> cotReq.setContratoSADE(Utils.trim(id)));
        
        if (Utils.isEmpty(cotReq.getContratoSADE())) {
            return;
        }
        
        boolean isDescuentoNomina = cotReq.getViaPago().equalsIgnoreCase(Constantes.CVE_DESC_NOMINA);

        cotResp.getPaquetes().parallelStream().forEach(
                p -> p.getTotales().stream().forEach(q -> updTotalDN(p.getCvePaquete(), cotReq.getContratoSADE(),
                        isDescuentoNomina, cotResp.getIniVig(), cotResp.getFinVig(), q, cotizacionNegocio.getTid())));
    }

    /**
     * Upd total DN.
     *
     * @param cvePaquete the cve paquete
     * @param idCobranza the id cobranza
     * @param isDescuentoNomina the is descuento nomina
     * @param fecIni the fec ini
     * @param fecFin the fec fin
     * @param totPrimaNeg the tot prima neg
     * @param tid the tid
     */
    public final void updTotalDN(final String cvePaquete, final String idCobranza, final boolean isDescuentoNomina,
            final String fecIni, final String fecFin, final TotalPrimaNeg totPrimaNeg, final Integer tid) {

        DescuentoNominaReq dNReq = getRequestDN(cvePaquete, idCobranza, isDescuentoNomina, fecIni, fecFin, totPrimaNeg);
        DescuentoNominaResp dNResp;

        if (tid != null) {
            dNResp = descuentoNominaClient.getDescuentoNomina(dNReq, tid.toString());
        } else {
            dNResp = descuentoNominaClient.getDescuentoNomina(dNReq);
        }

        totPrimaNeg.setResultadoParcialidad(getResultParcial(dNResp));

    }

    /**
     * Gets the request DN.
     *
     * @param cvePaquete the cve paquete
     * @param idCobranza the id cobranza
     * @param isDescuentoNomina the is descuento nomina
     * @param fecIni the fec ini
     * @param fecFin the fec fin
     * @param totPrimaNeg the tot prima neg
     * @return the request DN
     */
    private DescuentoNominaReq getRequestDN(final String cvePaquete, final String idCobranza, 
            final boolean isDescuentoNomina, final String fecIni, final String fecFin,
            final TotalPrimaNeg totPrimaNeg) {
        DescuentoNominaReq dNReq = new DescuentoNominaReq();
        dNReq.setIdPaquete(cvePaquete);
        if (isDescuentoNomina) {
            dNReq.setNumContratoSADE(idCobranza);
        } else {
            dNReq.setCodigoCalendarioInfo(idCobranza);
        }
        dNReq.setFecFinVig(fecFin);
        dNReq.setFecIniVig(fecIni);
        dNReq.setPeriodicidad(totPrimaNeg.getCvePeriodicidad());

        BigDecimal primaTotal = totPrimaNeg.getConceptosEconomicos().parallelStream()
                .filter(p -> p.getNombre().equalsIgnoreCase(Constantes.STR_TOTAL_PAGAR))
                .map(cE -> new BigDecimal(cE.getMonto())).findFirst().orElse(new BigDecimal("0"));

        dNReq.setPrimaTotal(primaTotal);
        return dNReq;
    }

    /**
     * Gets the result parcial.
     *
     * @param dNResp the d N resp
     * @return the result parcial
     */
    private static ResultadoParcialidadNeg getResultParcial(final DescuentoNominaResp dNResp) {
        ResultadoParcialidad resParcialidad = dNResp.getResultadoParcialidad();
        ResultadoParcialidadNeg resultadoParcialidad = new ResultadoParcialidadNeg();
        resultadoParcialidad.setFecProximoPago(resParcialidad.getFecProximoPago());
        resultadoParcialidad.setMontoParcialidad(resParcialidad.getMontoParcialidad());
        resultadoParcialidad.setNumParcialidades(resParcialidad.getNumParcialidades());
        return resultadoParcialidad;
    }
}
