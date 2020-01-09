package com.gnp.autos.wsp.cotizador.eot.model.transformacion;

import java.util.List;

import com.gnp.autos.wsp.negocio.neg.model.ElementoNeg;

import lombok.Data;

/**
 * Instantiates a new transformacion neg.
 */
@Data
public class TransformacionNeg {

    /** The variables trans. */
    private List<ElementoNeg> variablesTrans;

    /** The paquetes. */
    private List<PaqueteT> paquetes;

}
