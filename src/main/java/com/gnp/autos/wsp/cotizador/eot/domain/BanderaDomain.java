package com.gnp.autos.wsp.cotizador.eot.domain;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.banderas.BanderasAutosDecisionService;

/**
 * The Interface CotizacionDomain.
 */
@FunctionalInterface
public interface BanderaDomain {

    /**
     * Actualiza bandera.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the banderas autos decision service
     */
    BanderasAutosDecisionService actualizaBandera(CotizacionNegocio cotizacionNegocio);

}
