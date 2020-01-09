package com.gnp.autos.wsp.cotizador.eot.client;

import javax.ws.rs.core.MediaType;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gnp.autos.wsp.cotizador.eot.config.FeignConfig;
import com.gnp.autos.wsp.cotizador.eot.model.banderas.BanderasAutosDecisionService;

/**
 * The Interface BanderasClient.
 */
@FeignClient(name = "bandera", url = "${wsp_url_BanderasRest}", configuration = FeignConfig.class)
public interface BanderasClient {
    /**
     * Gets the catalogo WSP.
     *
     * @param banderas the obj req
     * @return the catalogo WSP
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    BanderasAutosDecisionService getBanderas(@RequestBody BanderasAutosDecisionService banderas);

    /**
     * Valida reglas.
     *
     * @param banderas the banderas
     * @param tid      the tid
     * @return the resultado valida resp
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    BanderasAutosDecisionService getBanderas(@RequestBody BanderasAutosDecisionService banderas,
            @RequestParam("tid") String tid);

}