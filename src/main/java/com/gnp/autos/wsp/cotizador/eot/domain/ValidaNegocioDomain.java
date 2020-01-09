package com.gnp.autos.wsp.cotizador.eot.domain;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;

/**
 * The Interface ValidaNegocioDomain.
 */
@FunctionalInterface
public interface ValidaNegocioDomain {

    /**
     * Valida.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the cotizacion negocio
     */
    CotizacionNegocio valida(CotizacionNegocio cotizacionNegocio);
}
