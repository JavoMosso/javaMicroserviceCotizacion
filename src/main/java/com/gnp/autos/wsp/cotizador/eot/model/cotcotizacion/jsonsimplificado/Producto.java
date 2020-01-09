package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado;

import java.util.List;

import lombok.Data;

/**
 * The Class Producto.
 */
@Data
public class Producto {
    /** The id. */
    private String id;
    
    /** The preferente. */
    private boolean preferente;
    
    /** The descripcion. */
    private String descripcion;
    
    /** The anualidad. */
    private String anualidad;
    
    /** The parcialidad. */
    private String parcialidad;
    
    /** The prima total. */
    private String primaTotal;
    
    /** The descuento. */
    private String descuento;
    
    /** The coberturas. */
    private List<Cobertura> coberturas;
    
    /** The tarificacion. */
    private Tarificacion tarificacion;
}