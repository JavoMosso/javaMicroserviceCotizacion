package com.gnp.autos.wsp.cotizador.eot.model.campain;

import java.util.List;

import com.gnp.autos.wsp.negocio.neg.model.CoberturaNeg;
import com.gnp.autos.wsp.negocio.neg.model.PersonaNeg;
import com.gnp.autos.wsp.negocio.neg.model.VehiculoNeg;

import lombok.Data;

/**
 * Instantiates a new campania req.
 */
@Data
public class CampaniaReq {

    /** The id negocio. */
    private String idNegocio;

    /** The tipo tramite. */
    private String tipoTramite;

    /** The fecha tramite. */
    private String fechaTramite;

    /** The coberturas. */
    private List<CoberturaNeg> coberturas;

    /** The personas. */
    private List<PersonaNeg> personas;

    /** The vehiculo. */
    private VehiculoNeg vehiculo;
}
