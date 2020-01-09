package com.gnp.autos.wsp.cotizador.eot.model;

import com.gnp.autos.wsp.cotizador.eot.model.transformacion.TransformacionNeg;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoRequest;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoResponse;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoWSPResp;
import com.gnp.autos.wsp.negocio.cotizacion.model.resp.TraductorResp;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegResp;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;

import lombok.Data;

/**
 * Instantiates a new cotizacion negocio.
 */
@Data
public class CotizacionNegocio {

    /** The emite neg req. */
    private CotizaNegReq cotizaNegReq;

    /** The cotiza neg resp. */
    private CotizaNegResp cotizaNegResp;

    /** The catalogo resp. */
    private CatalogoWSPResp catalogoResp;

    /** The umo service. */
    private UmoServiceResp umoService;

    /** The tid. */
    private Integer tid;

    /** The emite neg resp. */
    private CotizaNegResp emiteNegResp;

    /** The emite neg resp. */
    private TraductorResp traductorResp;

    /** The req muc. */
    private CalcularPrimaAutoRequest reqMuc;

    /** The resp muc. */
    private CalcularPrimaAutoResponse respMuc;

    /** The transformacion negocio. */
    private TransformacionNeg transformacionNegocio;

}
