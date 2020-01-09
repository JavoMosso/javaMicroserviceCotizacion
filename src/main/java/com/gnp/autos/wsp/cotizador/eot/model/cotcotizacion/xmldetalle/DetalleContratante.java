package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class DetalleContratante.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "detalleContratante")
public class DetalleContratante {
    /** The tipo persona. */
    @XmlElement(name = "tipoPersona")
    private String tipoPersona;
    
    /** The telefono. */
    @XmlElement(name = "NUM_TELEFONO")
    private String telefono;
    
    /** The nombres. */
    @XmlElement(name = "NOMBRES")
    private String nombres;
    
    /** The correo. */
    @XmlElement(name = "CORREO_ELECTRONICO")
    private String correo;
    
    /** The tipo telefono. */
    @XmlElement(name = "TIPO_TELEFONO")
    private String tipoTelefono;
    
    /** The telefono contacto. */
    @XmlElement(name = "TEL_CONTACTO")
    private String telefonoContacto;
    
    /** The hora inicio. */
    @XmlElement(name = "HORA_INICIO")
    private String horaInicio;
    
    /** The hora fin. */
    @XmlElement(name = "HORA_FIN")
    private String horaFin;
}