package com.gnp.autos.wsp.cotizador.eot.client;

import javax.ws.rs.core.MediaType;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gnp.autos.wsp.cotizador.eot.config.FeignConfig;
import com.gnp.autos.wsp.cotizador.eot.model.campain.CampaniaReq;
import com.gnp.autos.wsp.cotizador.eot.model.campain.CampaniaResp;

/**
 * The Interface CampaniasClient.
 */
@FeignClient(name = "campaniaService", url = "${wsp_url_Campania}", configuration = FeignConfig.class)
public interface CampaniasClient {
    /**
     * Gets the catalogo WSP.
     *
     * @param banderas the obj req
     * @return the catalogo WSP
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    CampaniaResp getCampanias(@RequestBody CampaniaReq banderas);

    /**
     * Valida reglas.
     *
     * @param campaniaReq the campania req
     * @param tid         the tid
     * @return the resultado valida resp
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    CampaniaResp getCampanias(@RequestBody CampaniaReq campaniaReq, @RequestParam("tid") String tid);
}