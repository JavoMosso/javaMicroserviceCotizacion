package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class DatosTransformacion.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "datosTransformacion")
public class DatosTransformacion {
    /** The version transformacion. */
    @XmlElement(name = "versionTrasformacion")
    private String versionTransformacion;

    /** The fch transformacion. */
    @XmlElement(name = "fechaTransformacion")
    private String fchTransformacion;
}