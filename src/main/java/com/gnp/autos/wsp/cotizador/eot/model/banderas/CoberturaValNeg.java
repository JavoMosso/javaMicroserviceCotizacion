package com.gnp.autos.wsp.cotizador.eot.model.banderas;

import lombok.Data;

/**
 * Instantiates a new cobertura val neg.
 */
@Data
public class CoberturaValNeg {

    /** The clavecobertura. */
    private String clavecobertura;

    /** The mto suma asegurada. */
    private String mtoSumaAsegurada;

    /** The mto deducible. */
    private String mtoDeducible;

    /** The mto deducible alto riesgo. */
    private String mtoDeducibleAltoRiesgo;
}
