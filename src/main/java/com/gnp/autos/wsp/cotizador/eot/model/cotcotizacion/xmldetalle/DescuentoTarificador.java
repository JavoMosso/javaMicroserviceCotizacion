package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class DescuentoTarificador.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Descuentos")
public class DescuentoTarificador {
    /** The nombre. */
    @XmlElement(name = "NOMBRE")
    private String nombre;

    /** The monto. */
    @XmlElement(name = "MONTO")
    private String monto;
}