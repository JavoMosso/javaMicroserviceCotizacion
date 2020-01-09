package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gnp.autos.wsp.cotizador.eot.client.TransformacionClient;
import com.gnp.autos.wsp.cotizador.eot.domain.TransformacionDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.transformacion.CoberturaT;
import com.gnp.autos.wsp.cotizador.eot.model.transformacion.PaqueteT;
import com.gnp.autos.wsp.cotizador.eot.model.transformacion.TransformacionNeg;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.negocio.neg.model.ElementoNeg;
import com.gnp.autos.wsp.negocio.neg.model.PaqueteNeg;
import com.gnp.autos.wsp.negocio.neg.model.PersonaNeg;
import com.gnp.autos.wsp.negocio.umoservice.model.Paquete;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;

/**
 * The Class TransformacionDomainImpl.
 */
@Component
public class TransformacionDomainImpl implements TransformacionDomain {

    /** The transformacion client. */
    @Autowired
    private TransformacionClient transformacionClient;

    /**
     * Gets the variables transformacion.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the variables transformacion
     */
    @Override
    public final TransformacionNeg getVariablesTransformacion(final CotizacionNegocio cotizacionNegocio) {
        Utileria.getRegistraLogTime("Inicia Variables Trans");
        return getReqTransformacion(cotizacionNegocio);
    }

    /**
     * Gets the req transformacion.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the req transformacion
     */
    private TransformacionNeg getReqTransformacion(final CotizacionNegocio cotizacionNegocio) {
        TransformacionNeg req = getRequest(cotizacionNegocio);
        TransformacionNeg respT;
        Utileria.pintaObjToJson(req, Constantes.PRINT_TRANS_REQ);
        if (Optional.ofNullable(cotizacionNegocio.getTid()).isPresent()) {
            respT = transformacionClient.getTransformacion(req, cotizacionNegocio.getTid().toString());
        } else {
            respT = transformacionClient.getTransformacion(req);
        }
        Utileria.pintaObjToJson(respT, Constantes.PRINT_TRANS_RESP);
        return respT;
    }

    /**
     * Gets the request.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the request
     */
    private TransformacionNeg getRequest(final CotizacionNegocio cotizacionNegocio) {

        TransformacionNeg req = new TransformacionNeg();
        List<PaqueteT> paquetes = cotizacionNegocio.getCotizaNegReq().getPaquetes().parallelStream()
                .map(p -> addPaquete(p, cotizacionNegocio.getUmoService())).collect(Collectors.toList());

        req.setPaquetes(paquetes);
        req.setVariablesTrans(getVarTransformacion(cotizacionNegocio));
        return req;
    }

    /**
     * Gets the var transformacion.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the var transformacion
     */
    public static List<ElementoNeg> getVarTransformacion(final CotizacionNegocio cotizacionNegocio) {
        List<ElementoNeg> variablesTrans = new ArrayList<>();

        PersonaNeg cond = cotizacionNegocio.getCotizaNegReq().getPersonas().parallelStream()
                .filter(p -> p.getTipo().equalsIgnoreCase(Constantes.STR_CONDUCTOR)).findFirst()
                .orElseThrow(() -> new ExecutionError(Constantes.ERROR_34));

        variablesTrans.add(new ElementoNeg("Edad", String.valueOf(cond.getEdad()), String.valueOf(cond.getEdad())));
        variablesTrans.add(new ElementoNeg("Genero", cond.getSexo(), cond.getSexo()));
        variablesTrans.add(new ElementoNeg("Region", cotizacionNegocio.getCatalogoResp().getRegionTarificacion(),
                cotizacionNegocio.getCatalogoResp().getRegionTarificacion()));
        return variablesTrans;
    }

    /**
     * Adds the paquete.
     *
     * @param paqNeg  the paq neg
     * @param umoResp the umo resp
     * @return the paquete T
     */
    private PaqueteT addPaquete(final PaqueteNeg paqNeg, final UmoServiceResp umoResp) {
        PaqueteT paquete = new PaqueteT();
        paquete.setCvePaquete(paqNeg.getCvePaquete());
        Paquete paqueteUmo = umoResp.getDominios().getCombinaciones().get(0).getPaquetes().parallelStream()
                .filter(p -> p.getProductoPersonalizado().equalsIgnoreCase(paqNeg.getCvePaquete())).findFirst()
                .orElseThrow(() -> new ExecutionError(Constantes.ERROR_37, "No se encontro configuracion paquete"));

        paquete.setVersionTransf(paqueteUmo.getConfiguracion().getReglaTransformacion().getClave());

        paquete.setFechaTransf(Utileria.getF1DateF2(paqueteUmo.getConfiguracion().getReglaTransformacion().getFecha()));

        List<CoberturaT> coberturas = paqNeg.getCoberturas().parallelStream()
                .map(cT -> new CoberturaT(cT.getCveCobertura())).collect(Collectors.toList());
        paquete.setCoberturas(coberturas);
        return paquete;
    }

}
