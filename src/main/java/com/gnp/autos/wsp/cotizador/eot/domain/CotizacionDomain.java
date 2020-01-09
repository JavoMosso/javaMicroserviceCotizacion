package com.gnp.autos.wsp.cotizador.eot.domain;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;

/**
 * The Interface CotizacionDomain.
 */
@FunctionalInterface
public interface CotizacionDomain {

    /**
     * Gets the cotizacion.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the cotizacion
     */
    CotizacionNegocio getCotizacion(CotizacionNegocio cotizacionNegocio);
}
