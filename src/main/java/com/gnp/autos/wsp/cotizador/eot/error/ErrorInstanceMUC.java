package com.gnp.autos.wsp.cotizador.eot.error;

/**
 * The Class ErrorInstanceMUC.
 */
public class ErrorInstanceMUC extends com.gnp.autos.wsp.errors.ErrorInstance {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant TIPO. */
    private static final String TIPO = "muc";

    /**
     * Instantiates a new error instance MUC.
     *
     * @param id the id
     */
    public ErrorInstanceMUC(final int id) {
        super(TIPO, id);
    }

    /**
     * Instantiates a new error instance MUC.
     *
     * @param id   the id
     * @param args the args
     */
    public ErrorInstanceMUC(final int id, final String... args) {
        super(TIPO, id, args);
    }
}
