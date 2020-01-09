package com.gnp.autos.wsp.cotizador.eot.client;

import javax.ws.rs.core.MediaType;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gnp.autos.wsp.cotizador.eot.config.FeignConfig;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;

/**
 * The Interface SicaNegociosClient.
 */
@FeignClient(name = "sica-negocios", url = "${wsp_url_UmoService}", configuration = FeignConfig.class)
public interface SicaNegociosClient {
    /**
     * Gets the nodos.
     *
     * @param idNegocio the id negocio
     * @param versionNegocio the version negocio
     * @return the nodos
     */
    @RequestMapping(method = RequestMethod.GET, value = "/negocios/{idNegocio}/versiones/{versionNegocio}/nodos",
            consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    UmoServiceResp getNodos(@RequestParam("idNegocio") Integer idNegocio,
            @RequestParam("versionNegocio") Integer versionNegocio);
}