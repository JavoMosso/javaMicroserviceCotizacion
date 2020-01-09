package com.gnp.autos.wsp.cotizador.eot.domain;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;

/**
 * The Interface FoliadorDomain.
 */
@FunctionalInterface
public interface FoliadorDomain {

    /**
     * Gets the folio.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the folio
     */
    String getFolio(CotizacionNegocio cotizacionNegocio);

}
