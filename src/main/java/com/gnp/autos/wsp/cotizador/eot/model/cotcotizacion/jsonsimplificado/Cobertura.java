package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado;

import lombok.Data;

/**
 * The Class Cobertura.
 */
@Data
public class Cobertura {
    /** The id. */
    private String id;
    
    /** The agrupador. */
    private String agrupador;

    /** The suma. */
    private Item suma;

    /** The deducible. */
    private Item deducible;

    /** The blindaje. */
    private String blindaje;
}