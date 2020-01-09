package com.gnp.autos.wsp.cotizador.eot.domain;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;

/**
 * The Interface CotizacionDomain.
 */
@FunctionalInterface
public interface FormatoImpresionDomain {

    /**
     * Gets the cotizacion.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the cotizacion
     */
    CotizacionNegocio getFormatoImp(CotizacionNegocio cotizacionNegocio);

}
