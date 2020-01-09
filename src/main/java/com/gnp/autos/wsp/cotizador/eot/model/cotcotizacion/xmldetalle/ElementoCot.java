package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class ElementoCot.
 */
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ELEMENTO")
@SuppressWarnings("all")
public class ElementoCot {
    /**
     * Instantiates a new elemento cot.
     *
     * @param nombre the nombre
     * @param valor the valor
     */
    public ElementoCot(final String nombre, final String valor) {
        super();
        this.nombre = nombre;
        this.valor = valor;
    }
    
    /**
     * Instantiates a new elemento cot.
     *
     * @param nombre the nombre
     * @param detalleCotizacion the detalle cotizacion
     */
    public ElementoCot(final String nombre, final DetalleCotizacionPadre detalleCotizacion) {
        super();
        this.nombre = nombre;
        this.detalleCotizacion = detalleCotizacion;
    }

    /** The nombre. */
    @XmlElement(name = "NOMBRE_ELEMENTO")
    private String nombre;

    /** The valor. */
    @XmlElement(name = "VALOR_ELEMENTO")
    private String valor;

    /** The detalle cotizacion. */
    @XmlElement(name = "VALOR_ELEMENTO")
    private DetalleCotizacionPadre detalleCotizacion;
}