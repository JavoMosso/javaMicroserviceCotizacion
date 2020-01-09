package com.gnp.autos.wsp.cotizador.eot.domain;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;

/**
 * The Interface UmoServiceDomain.
 */
@FunctionalInterface
public interface UmoServiceDomain {

    /**
     * Gets the datos negocio.
     *
     * @param cotizacionNegocio the obj req
     * @return the datos negocio
     */
    UmoServiceResp getDatosNegocio(CotizacionNegocio cotizacionNegocio);

}
