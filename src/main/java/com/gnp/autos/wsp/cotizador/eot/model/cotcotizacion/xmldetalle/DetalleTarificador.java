package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class DetalleTarificador.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "detalleCotizacion")
public class DetalleTarificador {
    /** The datos transformacion. */
    @XmlElement(name = "datosTransformacion")
    private DatosTransformacion datosTransformacion;

    /** The producto. */
    @XmlElement(name = "Productos")
    private List<Producto> producto;
}