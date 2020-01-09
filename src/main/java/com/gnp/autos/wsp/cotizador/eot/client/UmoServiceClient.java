package com.gnp.autos.wsp.cotizador.eot.client;

import javax.ws.rs.core.MediaType;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gnp.autos.wsp.cotizador.eot.config.FeignConfig;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;

/**
 * The Interface UmoServiceClient.
 */
@FeignClient(name = "umoService", url = "${wsp_url_UmoService}", configuration = FeignConfig.class)
public interface UmoServiceClient {
    /**
     * Gets the umo service.
     *
     * @param nop          the nop
     * @param tipoUso      the tipo uso
     * @param tipoVehiculo the tipo vehiculo
     * @param tipoPersona  the tipo persona
     * @param procedencia  the procedencia
     * @return the umo service
     */
    @RequestMapping(method = RequestMethod.GET, value = "/negocios-operables/{nop}/detalles",
            produces = MediaType.APPLICATION_JSON)
    UmoServiceResp getUmoService(@PathVariable("nop") String nop, @RequestParam("tipoUso") String tipoUso,
            @RequestParam("tipoVehiculo") String tipoVehiculo, @RequestParam("tipoPersona") String tipoPersona,
            @RequestParam("procedencia") String procedencia);

    /**
     * Gets the umo service.
     *
     * @param nop          the nop
     * @param codPromo     the codPromo
     * @param tipoUso      the tipo uso
     * @param tipoVehiculo the tipo vehiculo
     * @param tipoPersona  the tipo persona
     * @param procedencia  the procedencia
     * @return the umo service
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/negocios-operables/{nop}/codigos-promocion/{codPromo}/detalles",
            produces = MediaType.APPLICATION_JSON)
    UmoServiceResp getUmoService(@PathVariable("nop") String nop, @PathVariable("codPromo") String codPromo,
            @RequestParam("tipoUso") String tipoUso, @RequestParam("tipoVehiculo") String tipoVehiculo,
            @RequestParam("tipoPersona") String tipoPersona, @RequestParam("procedencia") String procedencia);

    /**
     * Gets the umo service version.
     *
     * @param nop          the nop
     * @param versionNeg   the version neg
     * @param tipoVehiculo the tipo vehiculo
     * @param tipoUso      the tipo uso
     * @param tipoPersona  the tipo persona
     * @param procedencia  the procedencia
     * @return the umo service version
     */
    @RequestMapping(method = RequestMethod.GET, value = "/negocios-operables/{nop}/versiones/{versionNeg}/detalles",
            produces = MediaType.APPLICATION_JSON)
    UmoServiceResp getUmoServiceVersion(@PathVariable("nop") String nop, @PathVariable("versionNeg") String versionNeg,
            @RequestParam("tipoUso") String tipoUso, @RequestParam("tipoVehiculo") String tipoVehiculo,
            @RequestParam("tipoPersona") String tipoPersona, @RequestParam("procedencia") String procedencia);

    /**
     * Gets the umo service version.
     *
     * @param nop          the nop
     * @param versionNeg   the version neg
     * @param codPromo     the cod promo
     * @param tipoVehiculo the tipo vehiculo
     * @param tipoUso      the tipo uso
     * @param tipoPersona  the tipo persona
     * @param procedencia  the procedencia
     * @return the umo service version
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/negocios-operables/{nop}/versiones/{versionNeg}/codigos-promocion/{codPromo}/detalles",
            produces = MediaType.APPLICATION_JSON)
    UmoServiceResp getUmoServiceVersionCodPromo(@PathVariable("nop") String nop,
            @PathVariable("versionNeg") String versionNeg, @PathVariable("codPromo") String codPromo,
            @RequestParam("tipoUso") String tipoUso, @RequestParam("tipoVehiculo") String tipoVehiculo,
            @RequestParam("tipoPersona") String tipoPersona, @RequestParam("procedencia") String procedencia);

}