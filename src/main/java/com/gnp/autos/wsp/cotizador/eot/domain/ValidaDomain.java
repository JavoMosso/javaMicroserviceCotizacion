package com.gnp.autos.wsp.cotizador.eot.domain;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;

/**
 * The Interface ValidaDomain.
 */
@FunctionalInterface
public interface ValidaDomain {

    /**
     * Valida.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the cotizacion negocio
     */
    CotizacionNegocio valida(CotizacionNegocio cotizacionNegocio);
}
