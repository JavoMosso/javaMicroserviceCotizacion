package com.gnp.autos.wsp.cotizador.eot.util;

import java.util.List;

import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.wsdl.ConceptoDto;
import com.gnp.autos.wsp.cotizador.eot.wsdl.ProductosDto;
import com.gnp.autos.wsp.negocio.cotcotizacion.model.req.CotCotizacionReq;
import com.gnp.autos.wsp.negocio.umoservice.model.Paquete;
import com.gnp.autos.wsp.negocio.util.Utils;

/**
 * The Class CotCotizacionMapper.
 */
public final class CotCotizacionMapper {
    /**
     * Constructor privado para evitar creacion de objetos.
     */
    private CotCotizacionMapper() { }
    
    /**
     * Fill primas.
     *
     * @param cotReq the cot req
     * @param cotNeg the cot neg
     */
    public static void fillPrimas(final CotCotizacionReq cotReq, final CotizacionNegocio cotNeg) {
        ProductosDto prodMuc = getProductoMuc(cotNeg);
        cotReq.setMtoPrimaNeta(getPrimaMuc(prodMuc, "PRIMA_NETA"));
        cotReq.setMtoDerechoPoliza(getConceptoMuc(prodMuc, "DERECHOS_POLIZA"));
    }
    
    /**
     * Gets the mto prima total.
     *
     * @param cotNeg the cot neg
     * @return the mto prima total
     */
    public static Double getMtoPrimaTotal(final CotizacionNegocio cotNeg) {
        ProductosDto prodMuc = getProductoMuc(cotNeg);
        return prodMuc.getDATOSCOTIZACION().get(0).getFORMAPAGORECIBO().getMONTOTOTAL();
    }
    
    /**
     * Gets the producto muc.
     *
     * @param cotNeg the cot neg
     * @return the producto muc
     */
    private static ProductosDto getProductoMuc(final CotizacionNegocio cotNeg) {
        List<ProductosDto> prodsMuc = cotNeg.getRespMuc().getPETICION().get(0).getDATOSPRODUCTOS();
        if (prodsMuc.size() == 1) {
            return prodsMuc.get(0);
        }
        List<Paquete> paqsUmo = cotNeg.getUmoService().getDominios().getCombinaciones().get(0).getPaquetes();
        return Utils.filterAndMapFirst(paqsUmo, p -> p.isPreferente(), p -> p.getProductoPersonalizado()).flatMap(
                prodUmo -> Utils.filterFirst(prodsMuc, p -> prodUmo.equals(p.getIDPRODUCTO()))).orElse(prodsMuc.get(0));
    }
    
    /**
     * Gets the concepto muc.
     *
     * @param prodMuc the prod muc
     * @param concepto the concepto
     * @return the concepto muc
     */
    private static Double getConceptoMuc(final ProductosDto prodMuc, final String concepto) {
        return getDatoMuc(concepto, 
                prodMuc.getDATOSCOTIZACION().get(0).getTOTALPRIMA().getCONCEPTOECONOMICO());
    }
    
    /**
     * Gets the prima muc.
     *
     * @param prodMuc the prod muc
     * @param concepto the concepto
     * @return the prima muc
     */
    private static Double getPrimaMuc(final ProductosDto prodMuc, final String concepto) {
        return getDatoMuc(concepto, 
                prodMuc.getDATOSCOTIZACION().get(0).getTOTALPRIMA().getPRIMA());
    }
    
    /**
     * Gets the dato muc.
     *
     * @param concepto the concepto
     * @param conceptos the conceptos
     * @return the dato muc
     */
    private static Double getDatoMuc(final String concepto, final List<ConceptoDto> conceptos) {
        return Utils.parseDouble(Utils.filterFirst(conceptos, 
                c -> concepto.equals(c.getNOMBRE())).map(c -> c.getMONTO()).orElse(null)).orElse(0.0);
    }
    
}