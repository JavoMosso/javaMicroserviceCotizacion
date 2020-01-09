package com.gnp.autos.wsp.cotizador.eot.model.transformacion;

import java.util.List;

import lombok.Data;

/**
 * Instantiates a new paquete T.
 */
@Data
public class PaqueteT {

    /** The cve paquete. */
    private String cvePaquete;

    /** The version transf. */
    private String versionTransf;

    /** The fecha transf. */
    private String fechaTransf;

    /** The coberturas. */
    private List<CoberturaT> coberturas;
}
