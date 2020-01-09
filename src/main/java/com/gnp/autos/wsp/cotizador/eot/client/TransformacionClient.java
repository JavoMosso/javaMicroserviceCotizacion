package com.gnp.autos.wsp.cotizador.eot.client;

import javax.ws.rs.core.MediaType;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gnp.autos.wsp.cotizador.eot.config.FeignConfig;
import com.gnp.autos.wsp.cotizador.eot.model.transformacion.TransformacionNeg;

/**
 * The Interface TransformacionClient.
 */
@FeignClient(name = "transformacionService", url = "${wsp_url_Transformacion}", configuration = FeignConfig.class)
public interface TransformacionClient {
    /**
     * Gets the catalogo WSP.
     *
     * @param transformacionNeg the transformacion neg
     * @return the catalogo WSP
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    TransformacionNeg getTransformacion(@RequestBody TransformacionNeg transformacionNeg);

    /**
     * Valida reglas.
     *
     * @param transformacionNeg the transformacion neg
     * @param tid               the tid
     * @return the resultado valida resp
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    TransformacionNeg getTransformacion(@RequestBody TransformacionNeg transformacionNeg,
            @RequestParam("tid") String tid);
}