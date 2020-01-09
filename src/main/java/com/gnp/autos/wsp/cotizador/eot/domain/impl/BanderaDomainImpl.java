package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gnp.autos.wsp.cotizador.eot.client.BanderasClient;
import com.gnp.autos.wsp.cotizador.eot.domain.BanderaDomain;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.banderas.BanderasAutosDecisionService;
import com.gnp.autos.wsp.cotizador.eot.model.banderas.CoberturaValNeg;
import com.gnp.autos.wsp.cotizador.eot.model.banderas.NegocioModeloNeg;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.negocio.neg.model.CoberturaNeg;

/**
 * The Class BanderaDomainImpl.
 */
@Component
public class BanderaDomainImpl implements BanderaDomain {

    /** The banderas client. */
    @Autowired
    private BanderasClient banderasClient;

    /**
     * Actualiza bandera.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the banderas autos decision service
     */
    @Override
    public final BanderasAutosDecisionService actualizaBandera(final CotizacionNegocio cotizacionNegocio) {
        Utileria.getRegistraLogTime("Inicia Banderas");
        BanderasAutosDecisionService banderas = getRequest(cotizacionNegocio);
        BanderasAutosDecisionService respuesta;
        Utileria.pintaObjToJson(banderas, Constantes.PRINT_BAN_REQ);
        if (Optional.ofNullable(cotizacionNegocio.getTid()).isPresent()) {
            respuesta = banderasClient.getBanderas(banderas, cotizacionNegocio.getTid().toString());
        } else {
            respuesta = banderasClient.getBanderas(banderas);
        }
        Utileria.pintaObjToJson(respuesta, Constantes.PRINT_BAN_RESP);
        return respuesta;

    }

    /**
     * Gets the request.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the request
     */
    private static BanderasAutosDecisionService getRequest(final CotizacionNegocio cotizacionNegocio) {
        BanderasAutosDecisionService banderas = new BanderasAutosDecisionService();
        NegocioModeloNeg negocioModelo = new NegocioModeloNeg();
        negocioModelo.setIdNegocioOperable(cotizacionNegocio.getCotizaNegReq().getIdUMO());
        banderas.setNegocioModelo(negocioModelo);
        banderas.setVehiculo(cotizacionNegocio.getCotizaNegReq().getVehiculo());

        List<CoberturaValNeg> listCoberturas = cotizacionNegocio.getCotizaNegReq().getPaquetes().parallelStream()
                .map(p -> getCoberturaRoboTotal(p.getCoberturas())).flatMap(List::stream).collect(Collectors.toList());

        banderas.setListCoberturas(listCoberturas);

        return banderas;

    }

    /**
     * Gets the cobertura robo total.
     *
     * @param coberturas the coberturas
     * @return the cobertura robo total
     */
    private static List<CoberturaValNeg> getCoberturaRoboTotal(final List<CoberturaNeg> coberturas) {
        return coberturas.parallelStream()
                .filter(p -> p.getCveCobertura().equalsIgnoreCase(Constantes.COBERTURA_ROBO_TOTAL)).map(p -> {
                    CoberturaValNeg cobertura = new CoberturaValNeg();
                    cobertura.setClavecobertura(p.getCveCobertura());
                    cobertura.setMtoDeducible(p.getDeducible());
                    cobertura.setMtoSumaAsegurada(p.getSa());
                    return cobertura;
                }).collect(Collectors.toList());
    }

}
