package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class Campania.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "campanias")
public class Campania {
    /** The id. */
    @XmlElement(name = "id")
    private String id;

    /** The nombre. */
    @XmlElement(name = "nombre")
    private String nombre;

    /** The valor. */
    @XmlElement(name = "valor")
    private String valor;

    /** The unidad. */
    @XmlElement(name = "unidad")
    private String unidad;
}