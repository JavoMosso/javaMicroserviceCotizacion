package com.gnp.autos.wsp.cotizador.eot.model.campain;

import java.util.List;

import lombok.Data;

/**
 * Instantiates a new campania resp.
 */
@Data
public class CampaniaResp {

    /** The salida. */
    private String salida;

    /** The reglas. */
    private String reglas;

    /** The id decision. */
    private String idDecision;

    /** The beneficios. */
    private List<Beneficio> beneficios;

    /** The descuento total. */
    private String descuentoTotal;

    /** The resultado. */
    private String resultado;
}
