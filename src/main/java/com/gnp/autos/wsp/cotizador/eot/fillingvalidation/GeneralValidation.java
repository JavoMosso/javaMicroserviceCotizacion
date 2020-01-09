package com.gnp.autos.wsp.cotizador.eot.fillingvalidation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.gnp.autos.wsp.cotizador.eot.error.ErrorInstanceMUC;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.errors.ErrorInstance;
import com.gnp.autos.wsp.errors.exceptions.WSPMultipleErrorException;

/**
 * The Class GeneralValidation.
 */
public class GeneralValidation {

    /** The errors. */
    private ArrayList<ErrorInstance> errors;

    /**
     * Instantiates a new general validation.
     */
    public GeneralValidation() {
        errors = new ArrayList<>();
    }

    /**
     * Validate.
     *
     * @param instance the instance
     * @param name     the name
     */
    public final void validate(final Object instance, final String name) {
        if (!Optional.ofNullable(instance).isPresent()) {
            errors.add(new ErrorInstanceMUC(1, name));
        }
        if (instance instanceof String) {
            String i = (String) instance;
            if (i.isEmpty()) {
                errors.add(new ErrorInstanceMUC(Constantes.ERROR_2, name));
            }
        }
    }

    /**
     * Instant validate.
     *
     * @param instance the instance
     * @param name     the name
     */
    public static void instantValidate(final Object instance, final String name) {
        if (!Optional.ofNullable(instance).isPresent()) {
            throw new ExecutionError(1, name);
        }
        if (instance instanceof String) {
            String i = (String) instance;
            if (i.isEmpty()) {
                throw new ExecutionError(Constantes.ERROR_2, name);
            }
        }
    }

    /**
     * Instant validate.
     *
     * @param instance the instance
     * @param name     the name
     */
    public static void instantValidateNumeric(final String instance, final String name) {
        if (!instance.isEmpty() && !Utileria.isNumeric(instance)) {
            throw new ExecutionError(Constantes.ERROR_9, name);
        }
    }

    /**
     * Instant validate.
     *
     * @param instance the instance
     */
    public static void instantValidateFecha(final String instance) {
        if (!instance.isEmpty()) {
            if (instance.length() > Constantes.NUM_8) {
                throw new ExecutionError(Constantes.ERROR_6);
            }

            SimpleDateFormat d = new SimpleDateFormat("yyyyMMdd");

            try {
                d.parse(instance);
            } catch (ParseException e) {
                throw new ExecutionError(Constantes.ERROR_6);
            }
        }

    }

    /**
     * Validate.
     *
     * @param instance the instance
     * @param name     the name
     * @param size     the size
     */
    public final void validate(final Object instance, final String name, final int size) {
        validate(instance, name);
        if (instance instanceof String) {
            String i = (String) instance;
            if (i.length() > size) {
                errors.add(new ErrorInstanceMUC(Constantes.ERROR_3, name, Integer.toString(size)));
            }
        }
    }

    /**
     * Validate max.
     *
     * @param instance the instance
     * @param name     the name
     * @param size     the size
     */
    public final void validateMax(final Object instance, final String name, final int size) {
        validate(instance, name);
        if (instance instanceof String) {
            String i = (String) instance;
            if (i.length() > size) {
                errors.add(new ErrorInstanceMUC(Constantes.ERROR_4, name, Integer.toString(size)));
            }
        }
    }

    /**
     * Internal validate.
     *
     * @param instance the instance
     */
    public static void internalValidate(final Object instance) {
        if (!Optional.ofNullable(instance).isPresent()) {
            throw new ExecutionError(0);
        }

        if ((instance instanceof String && ((String) instance).isEmpty())
                || (instance instanceof List && ((List<?>) instance).isEmpty())) {
            throw new ExecutionError(Constantes.ERROR_0);
        }

    }

    /**
     * Throw if erros.
     */
    public final void throwIfErros() {
        if (!errors.isEmpty()) {
            throw new WSPMultipleErrorException(errors);
        }
    }

}
