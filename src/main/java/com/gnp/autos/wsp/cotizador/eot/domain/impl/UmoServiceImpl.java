package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gnp.autos.wsp.cotizador.eot.client.UmoServiceClient;
import com.gnp.autos.wsp.cotizador.eot.domain.UmoServiceDomain;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.negocio.log.TransaccionIntermedia;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.PersonaNeg;
import com.gnp.autos.wsp.negocio.neg.model.VehiculoNeg;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;

/**
 * The Class UmoServiceImpl.
 */
@Component
public class UmoServiceImpl implements UmoServiceDomain {

    /** The umo service client. */
    @Autowired
    private UmoServiceClient umoServiceClient;

    /** The url transaccion intermedia. */
    @Value("${wsp_url_TransaccionIntermedia}")
    private String urlTransaccionIntermedia;

    /** The url transformacion service. */
    @Value("${wsp_url_UmoService}")
    private String urlUmoService;

    /**
     * Gets the datos negocio.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the datos negocio
     */
    @Override
    public final UmoServiceResp getDatosNegocio(final CotizacionNegocio cotizacionNegocio) {
        Utileria.getRegistraLogTime("Inicia Umo");
        CotizaNegReq objReq = cotizacionNegocio.getCotizaNegReq();

        PersonaNeg cont = objReq.getPersonas().parallelStream()
                .filter(p -> p.getTipo().equalsIgnoreCase(Constantes.STR_CONTRATANTE)).findFirst()
                .orElse(new PersonaNeg());
        return consumeUmoService(objReq, cont, cotizacionNegocio.getTid());

    }

    /**
     * Consume umo service.
     *
     * @param objReq the obj req
     * @param cont   the cont
     * @param tId    the t id
     * @return the umo service resp
     */
    private UmoServiceResp consumeUmoService(final CotizaNegReq objReq, final PersonaNeg cont, final Integer tId) {
        UmoServiceResp resUmo;
        String tipoPersona = cont.getTipoPersona();
        String nop = objReq.getIdUMO();
        VehiculoNeg vehNeg = objReq.getVehiculo();
        if (tipoPersona.equalsIgnoreCase(Constantes.TIPO_PERSONA_MORAL)) {
            tipoPersona = "J";
        }
        String versionNegocioReq = Utileria.getValorElementoNeg(objReq.getElementos(), Constantes.STR_VERSION_NEGOCIO);
        Timestamp tsRequest = new Timestamp(new Date().getTime());
        String urlService = urlUmoService + "/negocios-operables/" + nop;
        if (!versionNegocioReq.isEmpty()) {
            if (!objReq.getCodigoPromocion().isEmpty()) {
                urlService += "/versiones/" + versionNegocioReq + "/codigos-promocion/" + objReq.getCodigoPromocion();
                resUmo = umoServiceClient.getUmoServiceVersionCodPromo(nop, versionNegocioReq,
                        objReq.getCodigoPromocion(), vehNeg.getUso(), vehNeg.getTipoVehiculo(), tipoPersona,
                        vehNeg.getSubRamo());

            } else {
                urlService += "/versiones/" + versionNegocioReq;
                resUmo = umoServiceClient.getUmoServiceVersion(nop, versionNegocioReq, vehNeg.getUso(),
                        vehNeg.getTipoVehiculo(), tipoPersona, vehNeg.getSubRamo());
            }
        } else {
            if (!objReq.getCodigoPromocion().isEmpty()) {
                urlService += "/codigos-promocion/" + objReq.getCodigoPromocion();
                resUmo = umoServiceClient.getUmoService(nop, objReq.getCodigoPromocion(), vehNeg.getUso(),
                        vehNeg.getTipoVehiculo(), tipoPersona, vehNeg.getSubRamo());
            } else {
                resUmo = umoServiceClient.getUmoService(nop, vehNeg.getUso(), vehNeg.getTipoVehiculo(), tipoPersona,
                        vehNeg.getSubRamo());

            }
        }
        urlService += "/detalles?tipoUso=" + vehNeg.getUso() + "&tipoVehiculo=" + vehNeg.getTipoVehiculo()
                + "&tipoPersona=" + tipoPersona + "&procedencia=" + vehNeg.getSubRamo();

        TransaccionIntermedia.guardarTxJson(urlTransaccionIntermedia, tId, Constantes.CVE_TX_UMO_SERVICE, null, resUmo,
                urlService, tsRequest);

        Utileria.pintaObjToJson(resUmo, Constantes.PRINT_UMO_RESP);
        return resUmo;
    }

}
