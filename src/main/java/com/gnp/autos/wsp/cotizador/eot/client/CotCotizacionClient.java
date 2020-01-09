/*
 * 
 */
package com.gnp.autos.wsp.cotizador.eot.client;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gnp.autos.wsp.cotizador.eot.config.FeignConfig;
import com.gnp.autos.wsp.negocio.cotcotizacion.model.req.CotCotizacionReq;

/**
 * The Interface CotCotizacionClient.
 */
@FeignClient(name = "wsp-cot-cotizacion", url = "${wsp_url_cot_cotizacion}", configuration = FeignConfig.class)
public interface CotCotizacionClient {
    /**
     * Guardar.
     *
     * @param cotReq the cot req
     * @return the response entity
     */
    @RequestMapping(method = RequestMethod.POST, value = "/cotizacion",
            consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    ResponseEntity<Void> guardar(@Valid @RequestBody CotCotizacionReq cotReq);
}