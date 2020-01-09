package com.gnp.autos.wsp.cotizador.eot.domain;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.transformacion.TransformacionNeg;

/**
 * The Interface TransformacionDomain.
 */
@FunctionalInterface
public interface TransformacionDomain {

    /**
     * Gets the variables transformacion.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the variables transformacion
     */
    TransformacionNeg getVariablesTransformacion(CotizacionNegocio cotizacionNegocio);
}
