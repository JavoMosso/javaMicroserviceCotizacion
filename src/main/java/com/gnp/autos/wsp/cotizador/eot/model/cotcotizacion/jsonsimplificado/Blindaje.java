package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class Blindaje.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blindaje {
    /** The valor. */
    private String valor;
    
    /** The valor depreciado. */
    private String valorDepreciado;
    
    /** The fecha. */
    private String fecha;
}