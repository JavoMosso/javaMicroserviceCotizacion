package com.gnp.autos.wsp.cotizador.eot.services;

import java.util.Optional;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gnp.autos.wsp.cotizador.eot.domain.CotizacionDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.gnp.autos.wsp.errors.xml.ErrorXML;
import com.gnp.autos.wsp.negocio.cotizacion.model.req.TraductorReq;
import com.gnp.autos.wsp.negocio.cotizacion.model.resp.TraductorResp;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegResp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Servicio de Catalogo.
 */

@Api(tags = "Cotizador EOT", value = "/cotizar", produces = MediaType.APPLICATION_XML, consumes = MediaType.APPLICATION_XML)
@RestController
@RequestMapping("/cotizar")
public class CotizaService {

    /** The cotiza domain. */
    @Autowired
    private CotizacionDomain cotizaDomain;

    /**
     * Catalogo.
     *
     * @param traReq the tra req
     * @param tid    the tid
     * @return the traductor resp
     */
    @Produces({ MediaType.APPLICATION_XML })
    @ApiOperation(code = Constantes.HTTP_CODE_OK, value = Constantes.DESC_SERVICE)
    @ApiResponses({ @ApiResponse(code = Constantes.HTTP_CODE_OK, message = "OK", response = TraductorResp.class),
            @ApiResponse(code = Constantes.HTTP_BAD_REQUEST, message = "Bad Request", response = ErrorXML.class) })
    @RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_XML }, consumes = {
            MediaType.APPLICATION_XML })
    public final TraductorResp cotizar(@RequestBody final TraductorReq traReq,
            @RequestParam(value = "tid", required = false) final Integer tid) {

        CotizacionNegocio cotizacionNegocio = new CotizacionNegocio();
        cotizacionNegocio.setCotizaNegReq(traReq.getCotizaNeg());
        cotizacionNegocio.setTid(tid);
        try {
            TraductorResp resp = cotizaDomain.getCotizacion(cotizacionNegocio).getTraductorResp();
            return resp;
        } catch (WSPXmlExceptionWrapper ex) {
            ErrorXML err = ex.getEx();
            if (Optional.ofNullable(tid).isPresent()) {
                err.setError(String.format("%s (%s)", err.getError(), tid.toString()));
            }
            throw new WSPXmlExceptionWrapper(err);
        } catch (ExecutionError ex) {
            String[] args = ex.getArgs();
            if (Optional.ofNullable(tid).isPresent() && args != null && args.length > 0) {
                args[0] = String.format("%s (%s)", args[0], tid.toString());
            }
            throw ex;
        } catch (Exception ex) {
            throw ex;
        }

    }

    /**
     * Cotiza muc.
     *
     * @param cotReq the cot req
     * @param tid    the tid
     * @return the cotiza neg resp
     */
    @Produces({ MediaType.APPLICATION_JSON })
    @ApiOperation(code = Constantes.HTTP_CODE_OK, value = Constantes.DESC_SERVICE_NEG)
    @ApiResponses({ @ApiResponse(code = Constantes.HTTP_CODE_OK, message = "OK", response = TraductorResp.class),
            @ApiResponse(code = Constantes.HTTP_BAD_REQUEST, message = "Bad Request", response = ErrorXML.class) })
    @RequestMapping(value = "/neg", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON }, consumes = {
            MediaType.APPLICATION_JSON })
    public final CotizaNegResp cotizaMuc(@RequestBody final CotizaNegReq cotReq,
            @RequestParam(value = "tid", required = false) final Integer tid) {

        CotizacionNegocio cotizacionNegocio = new CotizacionNegocio();
        cotizacionNegocio.setCotizaNegReq(cotReq);
        cotizacionNegocio.setTid(tid);
        CotizacionNegocio resp = cotizaDomain.getCotizacion(cotizacionNegocio);
        return resp.getCotizaNegResp();

    }

}