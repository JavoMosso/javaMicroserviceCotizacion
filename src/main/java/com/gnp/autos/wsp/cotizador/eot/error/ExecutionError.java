package com.gnp.autos.wsp.cotizador.eot.error;

import com.gnp.autos.wsp.errors.exceptions.WSPSimpleException;

/**
 * The Class ExecutionError.
 */
public class ExecutionError extends WSPSimpleException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6051218318109989549L;

    /** The Constant TIPO. */
    private static final String TIPO = "muc";

    /**
     * Instantiates a new execution error.
     *
     * @param id the id
     */
    public ExecutionError(final int id) {
        super(TIPO, id);
    }

    /**
     * Instantiates a new execution error.
     *
     * @param id the id
     * @param ex the ex
     */
    public ExecutionError(final int id, final Throwable ex) {
        super(TIPO, id);
        initCause(ex);
    }

    /**
     * Instantiates a new execution error.
     *
     * @param id   the id
     * @param args the args
     */
    public ExecutionError(final int id, final String... args) {
        super(TIPO, id, args);
    }
}
