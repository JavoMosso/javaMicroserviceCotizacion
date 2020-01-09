package com.gnp.autos.wsp.cotizador.eot.domain;

import java.util.List;
import java.util.Map;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoWSPResp;
import com.gnp.autos.wsp.negocio.neg.model.ElementoReq;

/**
 * The Interface CatalogoDomain.
 */
public interface CatalogoDomain {
    /**
     * Gets the catalogos.
     *
     * @param catalogos the catalogos
     * @return the catalogos
     */
    Map<String, List<ElementoReq>> getCatalogos(Map<String, Map<String, String>> catalogos);

    /**
     * Gets the catalogo WSP.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the catalogo WSP
     */
    CatalogoWSPResp getCatalogoWSP(CotizacionNegocio cotizacionNegocio);
}
