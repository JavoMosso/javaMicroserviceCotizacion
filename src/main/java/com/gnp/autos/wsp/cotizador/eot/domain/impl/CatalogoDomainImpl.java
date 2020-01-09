package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gnp.autos.wsp.cotizador.eot.client.CatalogoClient;
import com.gnp.autos.wsp.cotizador.eot.domain.CatalogoDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.gnp.autos.wsp.errors.xml.ErrorXML;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoReq;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoWSPReq;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoWSPResp;
import com.gnp.autos.wsp.negocio.catalogo.model.MultiCatalogosReq;
import com.gnp.autos.wsp.negocio.catalogo.model.MultiCatalogosResp;
import com.gnp.autos.wsp.negocio.catalogo.model.Vehiculo;
import com.gnp.autos.wsp.negocio.log.TransaccionIntermedia;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.ElementoReq;
import com.gnp.autos.wsp.negocio.neg.model.PersonaNeg;
import com.gnp.autos.wsp.negocio.util.Utils;

/**
 * The Class CatalogoDomainImpl.
 */
@Component
public class CatalogoDomainImpl implements CatalogoDomain {
    /** The catalogo client. */
    @Autowired
    private CatalogoClient catalogoClient;

    /** The url transaccion intermedia. */
    @Value("${wsp_url_TransaccionIntermedia}")
    private String urlTransaccionIntermedia;

    /** The url catalogo. */
    @Value("${wsp_url_Catalogos}")
    private String urlCatalogo;
    
    /**
     * Gets the catalogos.
     *
     * @param catalogos the catalogos
     * @return the catalogos
     */
    @Override
    public final Map<String, List<ElementoReq>> getCatalogos(final Map<String, Map<String, String>> catalogos) {
        MultiCatalogosResp resp = getCatalogosResp(catalogos);
        return Utils.toMap(resp.getCatalogos(), k -> k.getTipoCatalogo(), v -> v.getElementos());
    }
    
    /**
     * Gets the catalogos resp.
     *
     * @param catalogos the catalogos
     * @return the catalogos resp
     */
    private MultiCatalogosResp getCatalogosResp(final Map<String, Map<String, String>> catalogos) {
        MultiCatalogosReq req = new MultiCatalogosReq();
        req.setCatalogos(Utils.mapToList(catalogos, (k, v) -> createReq(k, v)));
        return catalogoClient.getCatalogos(req);
    }
    
    /**
     * Creates the req.
     *
     * @param tipoCatalogo the tipo catalogo
     * @param elementos the elementos
     * @return the catalogo req
     */
    private static CatalogoReq createReq(final String tipoCatalogo, final Map<String, String> elementos) {
        CatalogoReq req = new CatalogoReq();
        req.setTipoCatalogo(tipoCatalogo);
        if (!Utils.isEmpty(elementos)) {
            req.setElementos(new ArrayList<>());
            elementos.forEach((nombre, valor) -> {
                req.getElementos().add(new ElementoReq(nombre, valor, valor));
            });
        }
        return req;
    }

    /**
     * Gets the catalogo WSP.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the catalogo WSP
     */
    @Override
    public final CatalogoWSPResp getCatalogoWSP(final CotizacionNegocio cotizacionNegocio) {
        Utileria.getRegistraLogTime("Inicia Catalogo");
        CotizaNegReq cotizaNeg = cotizacionNegocio.getCotizaNegReq();
        PersonaNeg conductor = cotizaNeg.getPersonas().parallelStream()
                .filter(p -> p.getTipo().equalsIgnoreCase(Constantes.STR_CONDUCTOR)).findFirst()
                .orElseThrow(() -> new ExecutionError(Constantes.ERROR_34));

        String cp;
        cp = conductor.getDomicilio().getCodigoPostal();
        CatalogoWSPReq objReq = new CatalogoWSPReq();
        objReq.setIsValida(false);
        Vehiculo vehCat = new Vehiculo();
        vehCat.setCp(cp);
        vehCat.setArmadora(cotizaNeg.getVehiculo().getArmadora());
        vehCat.setCarroceria(cotizaNeg.getVehiculo().getCarroceria());
        vehCat.setModelo(cotizaNeg.getVehiculo().getModelo());
        vehCat.setSubRamo(cotizaNeg.getVehiculo().getSubRamo());
        vehCat.setTipoVehiculo(cotizaNeg.getVehiculo().getTipoVehiculo());
        vehCat.setUso(cotizaNeg.getVehiculo().getUso());
        vehCat.setVersion(cotizaNeg.getVehiculo().getVersion());
        objReq.setVehiculo(vehCat);

        Utileria.pintaObjToXml(objReq, Constantes.PRINT_CAT_REQ);
        Timestamp tsRequest = new Timestamp(new Date().getTime());
        CatalogoWSPResp objResp = catalogoClient.getCatalogoWSP(objReq);
        TransaccionIntermedia.guardarTxJson(urlTransaccionIntermedia, cotizacionNegocio.getTid(),
                Constantes.CVE_TX_CATALOGO, objReq, objResp, urlCatalogo + "/WSP", tsRequest);

        Utileria.pintaObjToXml(objResp, Constantes.PRINT_CAT_RESP);
        if (Utileria.existeValor(objResp.getStrError())) {
            ErrorXML ex = new ErrorXML();
            ex.setClave(1);
            ex.setError(objResp.getStrError());
            ex.setOrigen("CATALOGOS");
            ex.setNow(new Date());
            throw new WSPXmlExceptionWrapper(ex);
        }
        return objResp;
    }
}