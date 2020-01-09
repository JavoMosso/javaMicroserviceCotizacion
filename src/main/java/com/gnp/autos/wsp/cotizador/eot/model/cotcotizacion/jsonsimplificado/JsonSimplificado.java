package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado;

import java.util.List;

import lombok.Data;

/**
 * The Class JsonSimplificado.
 */
@Data
public class JsonSimplificado {
    /** The negocio. */
    private String negocio;
    
    /** The codigo promocion. */
    private String codigoPromocion;
    
    /** The fecha inicio. */
    private String fechaInicio;
    
    /** The fecha fin. */
    private String fechaFin;
    
    /** The vehiculo. */
    private Vehiculo vehiculo;
    
    /** The contratante. */
    private Contratante contratante;
    
    /** The conductor habitual. */
    private ConductorHabitual conductorHabitual;
    
    /** The via pago. */
    private Item viaPago;
    
    /** The periodicidad. */
    private String periodicidad;
    
    /** The catalogo coberturas. */
    private List<CatalogoCobertura> catalogoCoberturas;
    
    /** The productos. */
    private List<Producto> productos;
    
    /** The descuentos. */
    private List<Item> descuentos;
}