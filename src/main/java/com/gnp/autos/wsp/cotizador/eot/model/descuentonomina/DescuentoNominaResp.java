package com.gnp.autos.wsp.cotizador.eot.model.descuentonomina;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Instantiates a new descuento nomina resp.
 */
@Data
@XmlRootElement(name = "ConsultarCalendarioResp")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultarCalendarioResp")
@ApiModel(value = "ConsultarCalendarioResp")
public class DescuentoNominaResp {

    /** The resultado parcialidad. */
    @XmlElement(name = "RESULTADO_MONTO_PARCIALIDAD")
    @JsonProperty(value = "ResultadoParcialidad")
    @ApiModelProperty(value = "ResultadoParcialidad", position = 0)
    private ResultadoParcialidad resultadoParcialidad;
}
