package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class ElementosCot.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ELEMENTOS")
public class ElementosCot {
    /** The elementos. */
    @XmlElement(name = "ELEMENTO")
    private List<ElementoCot> elementos;
}