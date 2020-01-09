package com.gnp.autos.wsp.cotizador.eot.model.descuentonomina;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Instantiates a new resultado parcialidad.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultadoParcialidad {

    /** The num parcialidades. */
    @XmlElement(name = "NUM_PARCIALIDADES")
    @JsonProperty(value = "numParcialidades")
    @ApiModelProperty(value = "Numero de parcialidades", position = 0)
    private Integer numParcialidades;

    /** The monto parcialidad. */
    @XmlElement(name = "MTO_PARCIALIDAD")
    @JsonProperty(value = "montoParcialidad")
    @ApiModelProperty(value = "Monto de la parcialidad", position = 1)
    private BigDecimal montoParcialidad;

    /** The fec proximo pago. */
    @XmlElement(name = "FCH_PROXIMO_PAGO")
    @JsonProperty(value = "fecProximoPago")
    @ApiModelProperty(value = "Fecha proximo pago", position = 2)
    private String fecProximoPago;
}
