package com.gnp.autos.wsp.cotizador.eot.domain;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.campain.CampaniaResp;

/**
 * The Interface CampaniasDomain.
 */
@FunctionalInterface
public interface CampaniasDomain {

    /**
     * Gets the campana.
     *
     * @param cotizacionNeg the cotizacion neg
     * @return the campana
     */
    CampaniaResp getCampana(CotizacionNegocio cotizacionNeg);
}
