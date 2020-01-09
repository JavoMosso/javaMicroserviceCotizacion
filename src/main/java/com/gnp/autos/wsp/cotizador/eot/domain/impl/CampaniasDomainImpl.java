package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gnp.autos.wsp.cotizador.eot.client.CampaniasClient;
import com.gnp.autos.wsp.cotizador.eot.domain.CampaniasDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.campain.CampaniaReq;
import com.gnp.autos.wsp.cotizador.eot.model.campain.CampaniaResp;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.negocio.neg.model.CoberturaNeg;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.umoservice.model.CoberturaUmo;
import com.gnp.autos.wsp.negocio.umoservice.model.Paquete;

/**
 * The Class CampaniasDomainImpl.
 */
@Component
public class CampaniasDomainImpl implements CampaniasDomain {

    /** The campanias client. */
    @Autowired
    private CampaniasClient campaniasClient;

    /**
     * Gets the campana.
     *
     * @param cotizacionNeg the cotizacion neg
     * @return the campana
     */
    @Override
    public final CampaniaResp getCampana(final CotizacionNegocio cotizacionNeg) {
        Utileria.getRegistraLogTime("Inicia Campania");
        CotizaNegReq cotizaNeg = cotizacionNeg.getCotizaNegReq();
        List<Paquete> paquetesUmo = cotizacionNeg.getUmoService().getDominios().getCombinaciones().get(0).getPaquetes();
        if (!Utileria.existeValor(paquetesUmo)) {
            throw new ExecutionError(Constantes.ERROR_37, "No se encontraron paquetes en el umo service");
        }
        CampaniaReq camReq = new CampaniaReq();
        List<CoberturaNeg> coberturasNeg = paquetesUmo.get(0).getCoberturas().parallelStream().map(this::getCoberturaN)
                .collect(Collectors.toList());
        camReq.setCoberturas(coberturasNeg);
        camReq.setIdNegocio(cotizaNeg.getIdUMO());
        Date fchHoy = new Date();
        camReq.setFechaTramite(Utileria.getStrFecha(fchHoy));
        camReq.setTipoTramite("COTIZACION");
        camReq.setPersonas(cotizaNeg.getPersonas());
        camReq.setVehiculo(cotizaNeg.getVehiculo());
        CampaniaResp respCam;
        Utileria.pintaObjToJson(camReq, Constantes.PRINT_CAMP_REQ);
        if (Optional.ofNullable(cotizacionNeg.getTid()).isPresent()) {
            respCam = campaniasClient.getCampanias(camReq, cotizacionNeg.getTid().toString());
        } else {
            respCam = campaniasClient.getCampanias(camReq);
        }
        Utileria.pintaObjToJson(respCam, Constantes.PRINT_CAMP_RESP);
        return respCam;

    }

    /**
     * Gets the cobertura N.
     *
     * @param cobR the cob R
     * @return the cobertura N
     */
    public final CoberturaNeg getCoberturaN(final CoberturaUmo cobR) {
        CoberturaNeg cobNeg = new CoberturaNeg();
        cobNeg.setCveCobertura(cobR.getCobertura().getClave());
        cobNeg.setTipoCobertura(String.valueOf(cobR.getTipoCobertura().getId()));
        return cobNeg;
    }

}
