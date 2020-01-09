package com.gnp.autos.wsp.cotizador.eot.domain;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;

/**
 * The Interface MucDomain.
 */
@FunctionalInterface
public interface MucDomain {

    /**
     * Gets the calculo muc.
     *
     * @param cotizacionNeg the cotizacion neg
     * @return the calculo muc
     */
    CotizacionNegocio getCalculoMuc(CotizacionNegocio cotizacionNeg);
}
