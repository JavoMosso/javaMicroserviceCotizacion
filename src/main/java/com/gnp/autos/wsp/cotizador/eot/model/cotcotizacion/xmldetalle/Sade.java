package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class Sade.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SADE")
public class Sade {
    /** The num parcialidades. */
    @XmlElement(name = "NUM_PARCIALIDADES")
    private String numParcialidades;

    /** The mto parcialidad. */
    @XmlElement(name = "MTO_PARCIALIDAD")
    private String mtoParcialidad;
}