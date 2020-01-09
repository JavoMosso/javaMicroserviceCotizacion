package com.gnp.autos.wsp.cotizador.eot.model.transformacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instantiates a new cobertura T.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoberturaT {

    /** The cve cobertura. */
    private String cveCobertura;

    /** The conductor. */
    private ConductorT conductor;

    /**
     * Instantiates a new cobertura T.
     *
     * @param cveCobertura the cve cobertura
     */
    public CoberturaT(final String cveCobertura) {
        super();
        this.cveCobertura = cveCobertura;
    }

}
