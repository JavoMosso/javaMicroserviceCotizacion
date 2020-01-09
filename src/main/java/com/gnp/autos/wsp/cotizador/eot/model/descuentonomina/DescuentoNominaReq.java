package com.gnp.autos.wsp.cotizador.eot.model.descuentonomina;

import java.math.BigDecimal;

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
 * Instantiates a new descuento nomina req.
 */
@Data
@XmlRootElement(name = "ConsultarCalendarioReq")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultarCalendarioReq")
@ApiModel(value = "ConsultarCalendarioReq")
public class DescuentoNominaReq {

    /** The id paquete. */
    @XmlElement(name = "idPaquete")
    @JsonProperty(value = "idPaquete")
    @ApiModelProperty(value = "Id del producto", position = 0, example = "PRP0000131")
    private String idPaquete;

    /** The periodicidad. */
    @XmlElement(name = "periodicidad")
    @JsonProperty(value = "periodicidad")
    @ApiModelProperty(value = "Id de la parcialidad", position = 1, example = "A")
    private String periodicidad;

    /** The num contrato SADE. */
    @XmlElement(name = "numContratoSADE")
    @JsonProperty(value = "numContratoSADE")
    @ApiModelProperty(value = "Numero de contrato SADE", position = 2, example = "20020089")
    private String numContratoSADE;

    /** The codigo calendario info. */
    @XmlElement(name = "codigoCalendarioInfo")
    @JsonProperty(value = "codigoCalendarioInfo")
    @ApiModelProperty(value = "Codigo Calendario Info", position = 3, example = "20020089")
    private String codigoCalendarioInfo;

    /** The fec ini vig. */
    @XmlElement(name = "fecIniVig")
    @JsonProperty(value = "fecIniVig")
    @ApiModelProperty(value = "Fecha inicio de vigencia", position = 4, example = "20170701")
    private String fecIniVig;

    /** The fec fin vig. */
    @XmlElement(name = "fecFinVig")
    @JsonProperty(value = "fecFinVig")
    @ApiModelProperty(value = "Fecha inicio de vigencia", position = 5, example = "20180701")
    private String fecFinVig;

    /** The prima total. */
    @XmlElement(name = "primaTotal")
    @JsonProperty(value = "primaTotal")
    @ApiModelProperty(value = "Importe de la prima total actual", position = 6, example = "5468.88")
    private BigDecimal primaTotal;
}
