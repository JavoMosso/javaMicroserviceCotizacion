package com.gnp.autos.wsp.cotizador.eot.client;

import javax.ws.rs.core.MediaType;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gnp.autos.wsp.cotizador.eot.config.FeignConfig;
import com.gnp.autos.wsp.cotizador.eot.model.descuentonomina.DescuentoNominaReq;
import com.gnp.autos.wsp.cotizador.eot.model.descuentonomina.DescuentoNominaResp;

/**
 * The Interface DescuentoNominaClient.
 */
@FeignClient(name = "descuentoService", url = "${wsp_url_DescuentoNomina}", configuration = FeignConfig.class)
public interface DescuentoNominaClient {
    /**
     * Gets the catalogo WSP.
     *
     * @param descuentoNom the descuento nom
     * @return the catalogo WSP
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    DescuentoNominaResp getDescuentoNomina(@RequestBody DescuentoNominaReq descuentoNom);

    /**
     * Valida reglas.
     *
     * @param descuentoNom the descuento nom
     * @param tid          the tid
     * @return the resultado valida resp
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    DescuentoNominaResp getDescuentoNomina(@RequestBody DescuentoNominaReq descuentoNom,
            @RequestParam("tid") String tid);
}