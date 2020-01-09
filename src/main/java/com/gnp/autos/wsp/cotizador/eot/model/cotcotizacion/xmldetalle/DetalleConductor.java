package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class DetalleConductor.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "detalleConductor")
public class DetalleConductor {
    /** The fch nacimiento. */
    @XmlElement(name = "fechaNacimiento")
    private String fchNacimiento;

    /** The genero. */
    @XmlElement(name = "genero")
    private String genero;

    /** The edad. */
    @XmlElement(name = "edad")
    private String edad;
}