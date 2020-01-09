package com.gnp.autos.wsp.cotizador.eot.client;

import javax.ws.rs.core.MediaType;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gnp.autos.wsp.cotizador.eot.config.FeignConfig;
import com.gnp.autos.wsp.negocio.formatoimpresion.model.FormatoImpReq;

/**
 * The Interface FormatoImpresionClient.
 */
@FeignClient(name = "formatoImpresionService", url = "${wsp_url_FormatoImpresion}", configuration = FeignConfig.class)
public interface FormatoImpresionClient {
    /**
     * Gets the catalogo WSP.
     *
     * @param formatoImp the formato imp
     * @return the catalogo WSP
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    FormatoImpReq getFormatoImp(@RequestBody FormatoImpReq formatoImp);

    /**
     * Gets Formato Imp.
     *
     * @param formatoImp the formato imp
     * @param tid        the tid
     * @return the resultado valida resp
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    FormatoImpReq getFormatoImp(@RequestBody FormatoImpReq formatoImp, @RequestParam("tid") String tid);
}