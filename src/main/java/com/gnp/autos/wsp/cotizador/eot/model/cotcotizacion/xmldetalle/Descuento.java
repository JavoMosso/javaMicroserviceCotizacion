package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class Descuento.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "descuentos")
public class Descuento {
    /** The clave. */
    @XmlElement(name = "clave")
    private String clave;

    /** The valor. */
    @XmlElement(name = "valor")
    private String valor;

    /** The unidad. */
    @XmlElement(name = "unidad")
    private String unidad;
}