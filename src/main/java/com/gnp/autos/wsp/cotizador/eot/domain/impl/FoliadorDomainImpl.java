package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gnp.autos.wsp.cotizador.eot.client.soap.FoliadorClient;
import com.gnp.autos.wsp.cotizador.eot.domain.FoliadorDomain;
import com.gnp.autos.wsp.cotizador.eot.foliador.wsdl.Foliador;
import com.gnp.autos.wsp.cotizador.eot.foliador.wsdl.FoliadorResponse;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;

/**
 * The Class FoliadorDomainImpl.
 */
@Service
public class FoliadorDomainImpl implements FoliadorDomain {

    /** The client. */
    @Autowired
    private FoliadorClient client;

    /** The url T inter. */
    @Value("${wsp_url_TransaccionIntermedia}")
    private String urlTInter;

    /**
     * Gets the folio.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the folio
     */
    @Override
    public final String getFolio(final CotizacionNegocio cotizacionNegocio) {
        Utileria.getRegistraLogTime("Inicia Foliador");
        if (Utileria.existeValor(cotizacionNegocio.getCotizaNegReq().getIdCotizacion())) {
            return cotizacionNegocio.getCotizaNegReq().getIdCotizacion();
        }
        Foliador requestDatos = obtenRequestDatos("5");
        FoliadorResponse resSOAP;
        resSOAP = client.getFolio(requestDatos, cotizacionNegocio.getTid(), urlTInter);
        return resSOAP.getIDTRANSACCION();
    }

    /**
     * Obten request datos.
     *
     * @param idtransaccion the idtransaccion
     * @return the foliador
     */
    private static Foliador obtenRequestDatos(final String idtransaccion) {
        Foliador reqFoliador = new Foliador();
        reqFoliador.setCVETIPOTRANSACCION(idtransaccion);
        return reqFoliador;
    }
}
