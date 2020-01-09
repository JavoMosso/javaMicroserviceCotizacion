package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static com.gnp.autos.wsp.negocio.util.Utils.filterAndFlatMapToList;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gnp.autos.wsp.cotizador.eot.client.soap.MucClient;
import com.gnp.autos.wsp.cotizador.eot.domain.MucDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.transformacion.CoberturaT;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoRequest;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoResponse;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CoberturaDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.ConductorDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CotizacionDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.DatosCotizacionDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.DatosProductoDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.DatosSolicitudDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.DescuentoDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.FormaPagoDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.FormaPagoReciboDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.ModificadorCoberturaDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.ObjectFactory;
import com.gnp.autos.wsp.cotizador.eot.wsdl.ProductosDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.VehiculoDto;
import com.gnp.autos.wsp.negocio.neg.model.CoberturaNeg;
import com.gnp.autos.wsp.negocio.neg.model.ConceptoEconomicoNeg;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegResp;
import com.gnp.autos.wsp.negocio.neg.model.DescuentoNeg;
import com.gnp.autos.wsp.negocio.neg.model.PaqueteNeg;
import com.gnp.autos.wsp.negocio.neg.model.PersonaNeg;
import com.gnp.autos.wsp.negocio.neg.model.TotalPrimaNeg;
import com.gnp.autos.wsp.negocio.neg.model.VehiculoNeg;
import com.gnp.autos.wsp.negocio.umoservice.model.Agente;
import com.gnp.autos.wsp.negocio.umoservice.model.CobranzaUmo;
import com.gnp.autos.wsp.negocio.umoservice.model.Condiciones;
import com.gnp.autos.wsp.negocio.umoservice.model.DerechoPoliza;
import com.gnp.autos.wsp.negocio.umoservice.model.Descuento;
import com.gnp.autos.wsp.negocio.umoservice.model.Dominios;
import com.gnp.autos.wsp.negocio.umoservice.model.Paquete;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;

/**
 * The Class MucDomainImpl.
 */
@Service
public class MucDomainImpl implements MucDomain {

    /** The client. */
    @Autowired
    private MucClient client;

    /** The url T inter. */
    @Value("${wsp_url_TransaccionIntermedia}")
    private String urlTInter;

    /**
     * Gets the calculo muc.
     *
     * @param cotizacionNeg the cotizacion neg
     * @return the calculo muc
     */
    @Override
    public final CotizacionNegocio getCalculoMuc(final CotizacionNegocio cotizacionNeg) {
        Utileria.getRegistraLogTime("Inicia MUC");
        CalcularPrimaAutoRequest reqMuc = getRequestMuc(cotizacionNeg);
        cotizacionNeg.setReqMuc(reqMuc);
        CalcularPrimaAutoResponse respMuc = client.getCalcular(cotizacionNeg, urlTInter);
        cotizacionNeg.setRespMuc(respMuc);
        cotizacionNeg.setCotizaNegResp(parseSoapResponse(cotizacionNeg));
        return cotizacionNeg;
    }

    /**
     * Parses the soap response.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the cotiza neg resp
     */
    private CotizaNegResp parseSoapResponse(final CotizacionNegocio cotizacionNegocio) {
        CotizaNegResp resp = new CotizaNegResp();
        CotizaNegReq objReq = cotizacionNegocio.getCotizaNegReq();

        resp.setIniVig(objReq.getIniVig());
        resp.setFinVig(objReq.getFinVig());

        resp.setPaquetes(getProductosResp(cotizacionNegocio));

        return resp;
    }

    /**
     * Gets the productos resp.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the productos resp
     */
    public final List<PaqueteNeg> getProductosResp(final CotizacionNegocio cotizacionNegocio) {

        List<PaqueteNeg> paquetesNeg = cotizacionNegocio.getCotizaNegReq().getPaquetes();
        List<ProductosDto> paquetesDto = cotizacionNegocio.getRespMuc().getPETICION().get(0).getDATOSPRODUCTOS();

        CobranzaUmo cobranzaUmo = cotizacionNegocio.getUmoService().getDominios().getCobranzas().getCobranzas()
                .parallelStream()
                .filter(u -> u.getConductoCobro().getClave()
                        .equalsIgnoreCase(cotizacionNegocio.getCotizaNegReq().getViaPago()))
                .findFirst().orElseThrow(
                        () -> new ExecutionError(Constantes.ERROR_37, "No se encontraron datos de cobranza via pago"));

        paquetesNeg.removeIf(p -> paquetesDto.parallelStream()
                .noneMatch(pD -> pD.getIDPRODUCTO().equalsIgnoreCase(p.getCvePaquete())));

        paquetesNeg.forEach(p -> paquetesDto.parallelStream()
                .filter(pD -> pD.getIDPRODUCTO().trim().equalsIgnoreCase(p.getCvePaquete().trim()))
                .filter(pD -> pD.getNOMBREPRODUCTO().trim().equalsIgnoreCase(p.getDescPaquete().trim())).findFirst()
                .ifPresent(pP -> getPaquetesNeg(p, pP, cotizacionNegocio, cobranzaUmo)));

        return paquetesNeg;
    }

    /**
     * Gets the paquetes neg.
     *
     * @param p                 the p
     * @param pP                the p P
     * @param cotizacionNegocio the cotizacion negocio
     * @param cobranzaUmo       the cobranza umo
     */
    private static void getPaquetesNeg(final PaqueteNeg p, final ProductosDto pP,
            final CotizacionNegocio cotizacionNegocio, final CobranzaUmo cobranzaUmo) {
        p.getCoberturas().removeIf(c -> pP.getDATOSCOTIZACION().get(0).getCOBERTURAPRIMA().parallelStream()
                .noneMatch(cD -> cD.getCLAVECOBERTURA().equalsIgnoreCase(c.getCveCobertura())));

        p.setCoberturas(p.getCoberturas().stream().sorted((p1, p2) -> ((Long) Long.parseLong(p1.getImpresionOrden()))
                .compareTo(Long.parseLong(p2.getImpresionOrden()))).collect(Collectors.toList()));

        List<DescuentoNeg> descuentosNeg = getDescuentosNeg(pP, cotizacionNegocio.getCotizaNegReq().getDescuentos());
        p.setDescuentos(descuentosNeg);

        List<TotalPrimaNeg> totalesPrimaNeg = getTotalesPrimaNeg(cobranzaUmo, pP);
        p.setTotales(totalesPrimaNeg);
    }

    /**
     * Gets the descuentos neg.
     *
     * @param pP      the p P
     * @param listDes the list des
     * @return the descuentos neg
     */
    private static List<DescuentoNeg> getDescuentosNeg(final ProductosDto pP, final List<DescuentoNeg> listDes) {
        if (!Utileria.existeValor(pP.getDESCUENTO())) {
            return Collections.emptyList();
        }
        return pP.getDESCUENTO().parallelStream().map(d -> {
            DescuentoNeg dNeg = new DescuentoNeg();
            dNeg.setCveDescuento(d.getCLAVEDESCUENTO());
            if (Utileria.existeValor(listDes)) {
                listDes.parallelStream().filter(dU -> dU.getCveDescuento().equalsIgnoreCase(d.getCLAVEDESCUENTO()))
                        .findFirst().ifPresent(dU -> dNeg.setDescripcion(dU.getDescripcion()));
            }
            dNeg.setValor(d.getCOEFICIENTE().toString());
            dNeg.setUnidadMedida(d.getUNIDADMEDIDA());
            dNeg.setBanRecargo("0");
            return dNeg;
        }).collect(Collectors.toList());

    }

    /**
     * Gets the totales prima neg.
     *
     * @param cobranzaUmo the cobranza umo
     * @param pP          the p P
     * @return the totales prima neg
     */
    private static List<TotalPrimaNeg> getTotalesPrimaNeg(final CobranzaUmo cobranzaUmo, final ProductosDto pP) {
        return pP.getDATOSCOTIZACION().parallelStream().map(fpT -> getTotalPrimaNeg(fpT, cobranzaUmo))
                .collect(Collectors.toList());

    }

    /**
     * Gets the total prima neg.
     *
     * @param fpT         the fp T
     * @param cobranzaUmo the cobranza umo
     * @return the total prima neg
     */
    private static TotalPrimaNeg getTotalPrimaNeg(final CotizacionDto fpT, final CobranzaUmo cobranzaUmo) {
        TotalPrimaNeg tprima = new TotalPrimaNeg();
        FormaPagoReciboDto pagpRecDto = fpT.getFORMAPAGORECIBO();
        tprima.setCvePeriodicidad(pagpRecDto.getCLAVEFORMAPAGO());

        cobranzaUmo.getFormas().parallelStream()
                .filter(f -> f.getFormaPago().getClave().equalsIgnoreCase(tprima.getCvePeriodicidad())).findFirst()
                .ifPresent(cc -> tprima.setDescPeriodicidad(cc.getFormaPago().getNombre()));

        tprima.setConceptosEconomicos(new ArrayList<ConceptoEconomicoNeg>());

        List<ConceptoEconomicoNeg> conceptosNeg = fpT.getTOTALPRIMA().getPRIMA().parallelStream()
                .filter(pr -> pr.getNOMBRE().equals(Constantes.STR_PRIMA_TECNICA)
                        || pr.getNOMBRE().equals(Constantes.STR_PRIMA_NETA)
                        || pr.getNOMBRE().equals(Constantes.STR_PRIMA_COMERCIAL))
                .map(pr -> new ConceptoEconomicoNeg(pr.getNOMBRE(), pr.getMONTO())).collect(Collectors.toList());

        tprima.getConceptosEconomicos().addAll(conceptosNeg);

        List<ConceptoEconomicoNeg> conceptosCENeg = fpT.getTOTALPRIMA().getCONCEPTOECONOMICO().parallelStream()
                .map(dd -> new ConceptoEconomicoNeg(dd.getNOMBRE(), dd.getMONTO())).collect(Collectors.toList());

        tprima.getConceptosEconomicos().addAll(conceptosCENeg);

        Double sumDesc = fpT.getTOTALPRIMA().getDETALLEDESCUENTO().parallelStream()
                .mapToDouble(dd -> Double.parseDouble(dd.getMONTO())).sum();
        tprima.getConceptosEconomicos().add(new ConceptoEconomicoNeg(Constantes.STR_DESCUENTO, sumDesc.toString()));

        List<ConceptoEconomicoNeg> conceptosDDNeg = fpT.getTOTALPRIMA().getDETALLEDESCUENTO().parallelStream()
                .map(dd -> new ConceptoEconomicoNeg(Constantes.STR_DESCUENTO + "_" + dd.getNOMBRE(), dd.getMONTO()))
                .collect(Collectors.toList());

        tprima.getConceptosEconomicos().addAll(conceptosDDNeg);

        tprima.getConceptosEconomicos()
                .add(new ConceptoEconomicoNeg(Constantes.STR_TOTAL_PAGAR, pagpRecDto.getMONTOTOTAL().toString()));
        tprima.getConceptosEconomicos().add(
                new ConceptoEconomicoNeg(Constantes.STR_PRIMER_RECIBO, pagpRecDto.getMONTOPRIMERRECIBO().toString()));
        tprima.getConceptosEconomicos().add(new ConceptoEconomicoNeg(Constantes.STR_RECIBO_SUBSECUENTE,
                pagpRecDto.getMONTORECIBOSUBSECUENTE().toString()));
        tprima.getConceptosEconomicos().add(
                new ConceptoEconomicoNeg(Constantes.STR_NUM_RECIBOS_SUBS, pagpRecDto.getNUMRECIBOSSUB().toString()));

        tprima.getConceptosEconomicos()
                .add(new ConceptoEconomicoNeg(Constantes.STR_NUM_PAGOS, pagpRecDto.getNUMEROPAGOS().toString()));

        return tprima;
    }

    /**
     * Gets the request muc.
     *
     * @param cotizacionNeg the cotizacion neg
     * @return the request muc
     */
    private CalcularPrimaAutoRequest getRequestMuc(final CotizacionNegocio cotizacionNeg) {
        CalcularPrimaAutoRequest reqMuc = new CalcularPrimaAutoRequest();
        DatosCotizacionDto datoCotizacion = new DatosCotizacionDto();
        datoCotizacion.setDATOSSOLICITUD(getDataSolicitudDto(cotizacionNeg));
        datoCotizacion.setVEHICULO(getDataVehiculoDto(cotizacionNeg));

        datoCotizacion.getFORMAPAGO().addAll(getFormaPagoDto(cotizacionNeg));
        datoCotizacion.getDATOSPRODUCTO().addAll(addProductos(cotizacionNeg));

        reqMuc.getDATOSCOTIZACION().add(datoCotizacion);
        return reqMuc;
    }

    /**
     * Gets the data vehiculo dto.
     *
     * @param cotizacionNeg the cotizacion neg
     * @return the data vehiculo dto
     */
    private static VehiculoDto getDataVehiculoDto(final CotizacionNegocio cotizacionNeg) {
        VehiculoDto vehMuc = new VehiculoDto();

        VehiculoNeg vh = cotizacionNeg.getCotizaNegReq().getVehiculo();
        vehMuc.setSUBRAMO(vh.getSubRamo());
        vehMuc.setTIPOVEHICULO(vh.getTipoVehiculo());
        vehMuc.setARMADORA(vh.getArmadora());
        vehMuc.setCARROCERIA(vh.getCarroceria());
        vehMuc.setMARCA(vh.getVersion());
        vehMuc.setMODELO(Integer.parseInt(vh.getModelo()));

        ObjectFactory fac = new ObjectFactory();

        JAXBElement<String> tipoUso = fac.createVehiculoDtoTIPOUSO(vh.getUso());
        vehMuc.setTIPOUSO(tipoUso);

        if (Utileria.existeValor(vh.getTipoCarga())) {
            JAXBElement<String> tipoCarga = fac.createVehiculoDtoCVETIPOCARGA(vh.getTipoCarga());
            vehMuc.setCVETIPOCARGA(tipoCarga);
        }

        return vehMuc;
    }

    /**
     * Gets the data solicitud dto.
     *
     * @param cotizacionNeg the cotizacion neg
     * @return the data solicitud dto
     */
    private DatosSolicitudDto getDataSolicitudDto(final CotizacionNegocio cotizacionNeg) {
        DatosSolicitudDto dSol = new DatosSolicitudDto();

        CotizaNegReq objReq = cotizacionNeg.getCotizaNegReq();

        UmoServiceResp umoResp = cotizacionNeg.getUmoService();

        PersonaNeg conductor = objReq.getPersonas().parallelStream()
                .filter(e -> Constantes.STR_CONDUCTOR.equalsIgnoreCase(e.getTipo())).findFirst()
                .orElseThrow(() -> new ExecutionError(Constantes.ERROR_34));

        validaConfigDom(cotizacionNeg.getUmoService().getDominios());

        Condiciones conDerechoPoliza = cotizacionNeg.getUmoService().getDominios().getCobranzas().getCondiciones();

        Double porcComision = 0.0;
        Double porcCesionComision = 0.0;
        if (Utileria.existeValor(cotizacionNeg.getUmoService().getDominios().getIntermediarios())
                && Utileria.existeValor(
                        cotizacionNeg.getUmoService().getDominios().getIntermediarios().getComisionGeneral())
                && Utileria.existeValor(cotizacionNeg.getUmoService().getDominios().getIntermediarios().getAgentes())) {
            Agente agentePoliza = cotizacionNeg.getUmoService().getDominios().getIntermediarios().getAgentes().get(0);
            porcComision = cotizacionNeg.getUmoService().getDominios().getIntermediarios().getComisionGeneral()
                    .getValor();
            porcCesionComision = agentePoliza.getPorcentajeCesion();
        }
        String banRenov = Utileria.getValorElementoNeg(cotizacionNeg.getCotizaNegReq().getElementos(),
                Constantes.STR_BAN_RENOVACION);

        String tipoFiltroDerPol = Utileria.stringToBoolean(banRenov) ? Constantes.FILTRO_TX_DESC_RENOV
                : Constantes.FILTRO_TX_DESCUENTO;

        Double derPol = getDerPolUmo(conDerechoPoliza, false, tipoFiltroDerPol);

        String derPolTmp = Utileria.getValorElementoNeg(cotizacionNeg.getCotizaNegReq().getElementos(),
                Constantes.STR_DERECHO_POLIZA);
        boolean isAltoRiesgo = Utileria.stringToBoolean(cotizacionNeg.getCotizaNegReq().getVehiculo().getAltoRiesgo());
        String politica = conDerechoPoliza.getClaveCobroDerechoPoliza().getClave();
        politica = Optional.ofNullable(politica).isPresent() ? politica : Constantes.RT;
        // Restricciones si es alto riesgo
        if (isAltoRiesgo && !Utileria.stringToBoolean(banRenov)) {
            Double derPolRestr = getDerPolUmo(conDerechoPoliza, true, Constantes.FILTRO_TX_DESCUENTO);
            if (derPolRestr > 0) {
                derPol = derPolRestr;
            }
        }
        dSol.setMONTODERECHOPOLIZA(derPolTmp.isEmpty() ? derPol : Double.parseDouble(derPolTmp));

        dSol.setCODIGOPOSTALCONTRATANTE(conductor.getDomicilio().getCodigoPostal());
        dSol.setPOLITICADERECHOPOLIZA(politica);

        String porcComisionTmp = Utileria.getValorElementoNeg(cotizacionNeg.getCotizaNegReq().getElementos(),
                Constantes.STR_PORC_COMISION);
        String porcCesionComTmp = Utileria.getValorElementoNeg(cotizacionNeg.getCotizaNegReq().getElementos(),
                Constantes.STR_PORC_CESION_COMISION);

        dSol.setPORCENTAJECOMISION(porcComisionTmp.isEmpty() ? porcComision : Double.parseDouble(porcComisionTmp));
        dSol.setPORCENTAJECESIONCOMISION(
                porcCesionComTmp.isEmpty() ? porcCesionComision : Double.parseDouble(porcCesionComTmp));
        dSol.setFORMAINDEMNIZACION(objReq.getVehiculo().getFormaIndemnizacion());
        dSol.setFORMAAJUSTEIRREGULAR(conDerechoPoliza.getClaveAjusteIrregular().getClave());
        dSol.setIDNEGOCIOOPERABLE(objReq.getIdUMO());

        dSol.setFECHAVIGENCIAINICIAL(Utileria.getXMLDate(objReq.getIniVig()));
        dSol.setFECHAVIGENCIAFINAL(Utileria.getXMLDate(objReq.getFinVig()));

        dSol.setCODIGOPROMOCION(Utileria.getValorString(cotizacionNeg.getCotizaNegReq().getCodigoPromocion()));
        dSol.setIDTRANSACCION(objReq.getIdCotizacion());
        dSol.setVERSIONNEGOCIO(umoResp.getNegocio().getVersion());

        return dSol;
    }

    /**
     * Valida config dom.
     *
     * @param dominios the dominios
     */
    private static void validaConfigDom(final Dominios dominios) {
        if (!Utileria.existeValor(dominios.getCobranzas())
                || !Utileria.existeValor(dominios.getCobranzas().getCobranzas())) {
            throw new ExecutionError(Constantes.ERROR_37, "No se encontro la configuracion de cobranza");
        }
    }

    /**
     * Gets the der pol umo.
     *
     * @param conDerechoPoliza  the con derecho poliza
     * @param isRestriccion     the is restriccion
     * @param filtroTxDescuento the filtro tx descuento
     * @return the der pol umo
     */
    private Double getDerPolUmo(final Condiciones conDerechoPoliza, final boolean isRestriccion,
            final String filtroTxDescuento) {
        if (!Utileria.existeValor(conDerechoPoliza.getAgrupadorDerechoPoliza().getDerechosPoliza())) {
            throw new ExecutionError(Constantes.ERROR_37, "No se encontro el derecho de poliza");
        }
        if (isRestriccion) {
            if (Utileria.existeValor(conDerechoPoliza.getAgrupadorDerechoPoliza().getDerechosPolizaRestriccion())) {
                return conDerechoPoliza.getAgrupadorDerechoPoliza().getDerechosPolizaRestriccion().parallelStream()
                        .filter(dp -> Optional.ofNullable(dp.getTransaccion()).isPresent())
                        .filter(dp -> dp.getTransaccion().getNombre().trim().equalsIgnoreCase(filtroTxDescuento))
                        .map(DerechoPoliza::getMonto).findFirst().orElse(0.0);
            } else {
                return 0.0;
            }
        } else {
            return conDerechoPoliza.getAgrupadorDerechoPoliza().getDerechosPoliza().parallelStream()
                    .filter(dp -> Optional.ofNullable(dp.getTransaccion()).isPresent())
                    .filter(dp -> dp.getTransaccion().getNombre().trim().equalsIgnoreCase(filtroTxDescuento))
                    .map(DerechoPoliza::getMonto).findFirst()
                    .orElseThrow(() -> new ExecutionError(Constantes.ERROR_37, "No se encontro el derecho de poliza"));
        }
    }

    /**
     * Gets the forma pago dto.
     *
     * @param cotizacionNeg the cotizacion neg
     * @return the forma pago dto
     */
    private List<FormaPagoDto> getFormaPagoDto(final CotizacionNegocio cotizacionNeg) {
        CotizaNegReq objReq = cotizacionNeg.getCotizaNegReq();

        List<FormaPagoDto> list = new ArrayList<>();

        CobranzaUmo cobUmo = cotizacionNeg.getUmoService().getDominios().getCobranzas().getCobranzas().stream()
                .filter(p -> p.getConductoCobro().getClave().equalsIgnoreCase(objReq.getViaPago())).findFirst()
                .orElseThrow(() -> new ExecutionError(1));

        if (!Utileria.existeValor(objReq.getPeriodicidad())) {
            list = cobUmo.getFormas().parallelStream().map(p -> addFormaPagoDto(p.getFormaPago().getClave()))
                    .collect(Collectors.toList());
            return list;
        } else {
            if (cobUmo.getFormas().parallelStream()
                    .noneMatch(p -> p.getFormaPago().getClave().equalsIgnoreCase(objReq.getPeriodicidad()))) {
                throw new ExecutionError(Constantes.ERROR_24, cobUmo.getFormas().parallelStream()
                        .map(f -> f.getFormaPago().getClave()).collect(Collectors.joining()));
            }

            list.add(addFormaPagoDto(objReq.getPeriodicidad()));
            return list;
        }

    }

    /**
     * Adds the forma pago dto.
     *
     * @param periodicidad the periodicidad
     * @return the forma pago dto
     */
    private static FormaPagoDto addFormaPagoDto(final String periodicidad) {
        FormaPagoDto dFp = new FormaPagoDto();
        dFp.setCLAVEFORMAPAGO(periodicidad);
        dFp.setPERIODICIDADPAGO(periodicidad);
        dFp.setVERSIONRECARGOFORMAPAGO(null);
        return dFp;
    }

    /**
     * Adds the productos.
     *
     * @param cotizacionNeg the cotizacion neg
     * @return the list
     */
    private List<DatosProductoDto> addProductos(final CotizacionNegocio cotizacionNeg) {

        List<PaqueteNeg> paquetesNeg = cotizacionNeg.getCotizaNegReq().getPaquetes();

        return paquetesNeg.parallelStream().map(p -> getDatosProductoDto(p, cotizacionNeg))
                .collect(Collectors.toList());

    }

    /**
     * Gets the datos producto dto.
     *
     * @param p             the p
     * @param cotizacionNeg the cotizacion neg
     * @return the datos producto dto
     */
    private DatosProductoDto getDatosProductoDto(final PaqueteNeg p, final CotizacionNegocio cotizacionNeg) {

        DatosProductoDto dProd = new DatosProductoDto();
        Paquete paqUmo = cotizacionNeg.getUmoService().getDominios().getCombinaciones().get(0).getPaquetes()
                .parallelStream().filter(u -> u.getProductoPersonalizado().equalsIgnoreCase(p.getCvePaquete()))
                .findFirst().orElseThrow(() -> new ExecutionError(Constantes.ERROR_37,
                        String.format("No se encontro paquete en umo service %s", p.getCvePaquete())));

        dProd.setIDPRODUCTO(p.getCvePaquete());
        dProd.setNOMBREPRODUCTO(p.getDescPaquete());
        String cveTarifaTmp = Utileria.getValorElementoNeg(cotizacionNeg.getCotizaNegReq().getElementos(),
                Constantes.STR_CVE_TARIFA);
        String fchTarifaTmp = Utileria.getValorElementoNeg(cotizacionNeg.getCotizaNegReq().getElementos(),
                Constantes.STR_FCH_TARIFA);

        dProd.setCLAVETARIFA(cveTarifaTmp.isEmpty() ? new BigInteger(paqUmo.getConfiguracion().getTarifa().getClave())
                : new BigInteger(cveTarifaTmp));

        actualizaValoresVehiculo(dProd, cotizacionNeg.getCotizaNegReq().getVehiculo());

        ObjectFactory fac = new ObjectFactory();
        JAXBElement<XMLGregorianCalendar> fechatarifa = fac
                .createDatosProductoBigDecimalDtoFECHATARIFA(Utileria.getXMLDate(
                        fchTarifaTmp.isEmpty() ? paqUmo.getConfiguracion().getTarifa().getFecha().replace("-", "")
                                : fchTarifaTmp));
        dProd.setFECHATARIFA(fechatarifa);

        dProd.getCOBERTURA().addAll(getCoberturasDto(p, cotizacionNeg));
        dProd.getDESCUENTO().addAll(getDescuentoDto(cotizacionNeg));
        return dProd;
    }

    /**
     * Actualiza valores vehiculo.
     *
     * @param dProd    the d prod
     * @param vehiculo the vehiculo
     */
    private static void actualizaValoresVehiculo(final DatosProductoDto dProd, final VehiculoNeg vehiculo) {
        actualizaValorVehiculo(dProd, vehiculo.getValorFactura());
        actualizaValorVehiculo(dProd, vehiculo.getValorConvenido());
        actualizaValorVehiculo(dProd, vehiculo.getValorVehiculo());
    }

    /**
     * Actualiza valor vehiculo.
     *
     * @param dProd        the d prod
     * @param valorFactura the valor factura
     */
    private static void actualizaValorVehiculo(final DatosProductoDto dProd, final String valorFactura) {
        if (Utileria.existeValor(valorFactura)) {
            dProd.setVALORCOMERCIAL(valorFactura);
        }
    }

    /**
     * Gets the descuento dto.
     *
     * @param cotizacionNeg the cotizacion neg
     * @return the descuento dto
     */
    private List<DescuentoDto> getDescuentoDto(final CotizacionNegocio cotizacionNeg) {

        List<DescuentoDto> descuentosDto = new ArrayList<>();
        List<DescuentoNeg> descuentosNeg = cotizacionNeg.getCotizaNegReq().getDescuentos();

        List<Descuento> descuentosUmo = cotizacionNeg.getUmoService().getDominios().getCobranzas().getDescuentos();

        if (Utileria.existeValor(descuentosNeg)) {
            List<String> arrDesc = Arrays.asList(Constantes.STR_PODESCAM, Constantes.STR_VADESVOL,
                    Constantes.STR_VADESREN);
            cotizacionNeg.getCotizaNegReq().getDescuentos().parallelStream().forEach(dU -> {
                if (!arrDesc.contains(dU.getCveDescuento()) && descuentosUmo.parallelStream()
                        .noneMatch(t -> t.getDescuento().getCodigo().equalsIgnoreCase(dU.getCveDescuento()))) {
                    throw new ExecutionError(Constantes.ERROR_11, dU.getCveDescuento());
                }
            });

            descuentosDto = cotizacionNeg.getCotizaNegReq().getDescuentos().parallelStream().map(des -> {
                DescuentoDto descDto = new DescuentoDto();
                descDto.setBANRECARGO(false);
                descDto.setCLAVEDESCUENTO(des.getCveDescuento());
                descDto.setCOEFICIENTE(Utileria.getValorDoubleZero(des.getValor()));
                descDto.setUNIDADMEDIDA(des.getUnidadMedida());
                return descDto;
            }).collect(Collectors.toList());

        }
        return descuentosDto;
    }

    /**
     * Gets the coberturas dto.
     *
     * @param p             the p
     * @param cotizacionNeg the cotizacion neg
     * @return the coberturas dto
     */
    private List<CoberturaDto> getCoberturasDto(final PaqueteNeg p, final CotizacionNegocio cotizacionNeg) {
        // La cantidad de veces que se envia un mismo paquete, sera la misma cantidad de
        // paquetes transformados, colocar
        // en un list todas las coberturas del paquete(s) transformado(s), esto se hace
        // para que cuando se envie un
        // mismo paquete dos o mas veces (copia) se encuentre la informacion de
        // transformacion de las coberturas
        List<CoberturaT> cobsTrans = filterAndFlatMapToList(cotizacionNeg.getTransformacionNegocio().getPaquetes(),
                t -> t.getCvePaquete().equalsIgnoreCase(p.getCvePaquete()), t -> t.getCoberturas().stream());

        if (cobsTrans.isEmpty()) {
            throw new ExecutionError(Constantes.ERROR_37,
                    String.format("Error en paquete transformacion, no existen coberturas para paquete %s - %s",
                            p.getCvePaquete(), p.getDescPaquete()));
        }

        PersonaNeg condNeg = cotizacionNeg.getCotizaNegReq().getPersonas().parallelStream()
                .filter(t -> t.getTipo().equalsIgnoreCase(Constantes.STR_CONTRATANTE)).findFirst()
                .orElseThrow(() -> new ExecutionError(Constantes.ERROR_37, "Error Contratante"));

        String cp = condNeg.getDomicilio().getCodigoPostal();

        List<CoberturaDto> coberturasDto = p.getCoberturas().parallelStream().map(cob -> {
            CoberturaT cobTrans = cobsTrans.parallelStream()
                    .filter(ct -> ct.getCveCobertura().equalsIgnoreCase(cob.getCveCobertura())).findFirst()
                    .orElseThrow(() -> new ExecutionError(Constantes.ERROR_37,
                            String.format("Error en paquete %s, cobertura transformacion %s", p.getCvePaquete(),
                                    cob.getCveCobertura())));

            return addCoberturaProd(cob, cobTrans, cp);
        }).collect(Collectors.toList());

        return coberturasDto;
    }

    /**
     * Adds the cobertura prod.
     *
     * @param coberturaNeg the cobertura neg
     * @param cobT         the cob T
     * @param cp           the cp
     * @return the cobertura dto
     */
    public final CoberturaDto addCoberturaProd(final CoberturaNeg coberturaNeg, final CoberturaT cobT,
            final String cp) {

        CoberturaDto cobDto = new CoberturaDto();
        cobDto.setCLAVECOBERTURA(coberturaNeg.getCveCobertura());
        cobDto.setTIPOCOBERTURA(coberturaNeg.getTipoCobertura());

        ConductorDto condDto = new ConductorDto();
        ObjectFactory objF = new ObjectFactory();

        condDto.setEDAD(Integer.parseInt(cobT.getConductor().getEdad()));
        condDto.setGENERO(cobT.getConductor().getGenero());
        JAXBElement<String> rtar = objF
                .createConductorDtoREGIONTARIFICACION(Utileria.rellenarCero(cobT.getConductor().getRegion(), "3"));
        condDto.setREGIONTARIFICACION(rtar);

        JAXBElement<String> jcp = objF.createConductorDtoCODIGOPOSTAL(cp);
        condDto.setCODIGOPOSTAL(jcp);
        cobDto.setCONDUCTOR(condDto);

        if (Utileria.existeValor(coberturaNeg.getSa())) {
            ModificadorCoberturaDto mcd = new ModificadorCoberturaDto();
            mcd.setCLAVEMODIFICADOR(Constantes.MOD_CVE_SA);
            mcd.setUNIDADMEDIDA(coberturaNeg.getUdSA());
            mcd.setVALORREQUERIDO(Utileria.getValorDouble(coberturaNeg.getSa()));
            cobDto.getMODIFICADOR().add(mcd);
        }
        if (Utileria.existeValor(coberturaNeg.getDeducible())) {
            ModificadorCoberturaDto mcd = new ModificadorCoberturaDto();
            mcd.setCLAVEMODIFICADOR(Constantes.MOD_CVE_DEDUCIBLE);
            mcd.setUNIDADMEDIDA(coberturaNeg.getUdDed());
            mcd.setVALORREQUERIDO(Utileria.getValorDouble(coberturaNeg.getDeducible()));
            cobDto.getMODIFICADOR().add(mcd);
        }

        return cobDto;
    }

}
