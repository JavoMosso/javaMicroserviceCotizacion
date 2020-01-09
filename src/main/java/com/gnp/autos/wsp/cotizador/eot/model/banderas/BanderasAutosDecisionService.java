package com.gnp.autos.wsp.cotizador.eot.model.banderas;

import java.util.List;

import com.gnp.autos.wsp.negocio.neg.model.VehiculoNeg;

import lombok.Data;

/**
 * Instantiates a new banderas autos decision service.
 */
@Data
public class BanderasAutosDecisionService {

    /** The ind afecta bono. */
    private String indAfectaBono;

    /** The vehiculo. */
    private VehiculoNeg vehiculo;

    /** The agentes. */
    private List<AgenteValNeg> agentes;

    /** The negocio modelo. */
    private NegocioModeloNeg negocioModelo;

    /** The list coberturas. */
    private List<CoberturaValNeg> listCoberturas;
}
