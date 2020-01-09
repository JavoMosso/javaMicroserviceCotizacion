package com.gnp.autos.wsp.cotizador.eot.domain;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;

/**
 * The Interface RulesDomain.
 */
@FunctionalInterface
public interface RulesDomain {

    /**
     * Gets the rules.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the rules
     */
    CotizacionNegocio getRules(CotizacionNegocio cotizacionNegocio);

}
