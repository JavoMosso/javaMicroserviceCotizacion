package com.gnp.autos.wsp.cotizador.eot.client;

import javax.ws.rs.core.MediaType;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gnp.autos.wsp.cotizador.eot.config.FeignConfig;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoReq;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoResp;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoWSPReq;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoWSPResp;
import com.gnp.autos.wsp.negocio.catalogo.model.MultiCatalogosReq;
import com.gnp.autos.wsp.negocio.catalogo.model.MultiCatalogosResp;

/**
 * The Interface CatalogoClient.
 */
@FeignClient(name = "catalogo", url = "${wsp_url_Catalogos}", configuration = FeignConfig.class)
public interface CatalogoClient {
    /**
     * Gets the catalogo WSP.
     *
     * @param objReq the obj req
     * @return the catalogo WSP
     */
    @RequestMapping(method = RequestMethod.POST, value = "/wsp",
            consumes = MediaType.APPLICATION_XML, produces = MediaType.APPLICATION_XML)
    CatalogoWSPResp getCatalogoWSP(@RequestBody CatalogoWSPReq objReq);

    /**
     * Gets the catalogo.
     *
     * @param objReq the obj req
     * @return the catalogo
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_XML, produces = MediaType.APPLICATION_XML)
    CatalogoResp getCatalogo(@RequestBody CatalogoReq objReq);
    
    /**
     * Gets the catalogos.
     *
     * @param request the request
     * @return the catalogos
     */
    @RequestMapping(method = RequestMethod.POST, value = "/multi", consumes = MediaType.APPLICATION_XML, 
            produces = MediaType.APPLICATION_XML)
    MultiCatalogosResp getCatalogos(@RequestBody MultiCatalogosReq request);
}