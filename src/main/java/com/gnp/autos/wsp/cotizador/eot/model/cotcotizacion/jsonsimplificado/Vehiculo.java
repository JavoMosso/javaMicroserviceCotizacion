package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado;

import lombok.Data;

/**
 * The Class Vehiculo.
 */
@Data
public class Vehiculo {
    /** The procedencia. */
    private Item procedencia;
    
    /** The uso. */
    private String uso;
    
    /** The tipo. */
    private String tipo;
    
    /** The anio. */
    private String anio;
    
    /** The descripcion. */
    private String descripcion;
    
    /** The descripcion factura. */
    private String descripcionFactura;
    
    /** The num serie. */
    private String numSerie;
    
    /** The forma indemnizacion. */
    private Item formaIndemnizacion;
    
    /** The valor. */
    private String valor;
    
    /** The tipo carga. */
    private String tipoCarga;
    
    /** The blindaje. */
    private Blindaje blindaje;
}