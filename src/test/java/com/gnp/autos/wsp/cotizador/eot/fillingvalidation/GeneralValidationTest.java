package com.gnp.autos.wsp.cotizador.eot.fillingvalidation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.errors.exceptions.WSPMultipleErrorException;

/**
 * The Class CatalogoImplTest.
 */
@RunWith(SpringRunner.class)
public class GeneralValidationTest {

    /** The catalogo domain. */
    @InjectMocks
    private GeneralValidation generalValidation;

    /**
     * Inits the mocks.
     */
    @Before
    public void initMocks() {
        generalValidation = new GeneralValidation();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaWSPReqTest() {
        GeneralValidation.instantValidate("algo", "nulo");
        GeneralValidation.instantValidate(null, "nulo");
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValida2WSPReqTest() {
        GeneralValidation.instantValidate(35, "nulo");
        GeneralValidation.instantValidate("", "nulo");

    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = WSPMultipleErrorException.class)
    public void getValidateTest() {
        generalValidation.validate(null, "nulo");
        generalValidation.throwIfErros();
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = WSPMultipleErrorException.class)
    public void getValidateVacioTest() {
        generalValidation.validate("algo", "nulo");
        generalValidation.validate("", "nulo");
        generalValidation.throwIfErros();
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = WSPMultipleErrorException.class)
    public void getValidateSizeTest() {
        generalValidation.validate("algo", "nulo", 4);
        generalValidation.validate(3455, "nulo", 4);
        generalValidation.validate("3455556", "nulo", 4);
        generalValidation.throwIfErros();
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = WSPMultipleErrorException.class)
    public void getValidateMaxTest() {
        generalValidation.validateMax("algo", "nulo", 4);
        generalValidation.validateMax(3455, "nulo", 4);
        generalValidation.validateMax("3455556", "nulo", 4);
        generalValidation.throwIfErros();
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaInternalWSPReqTest() {
        GeneralValidation.internalValidate("eee");
        GeneralValidation.internalValidate("");
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaInternal2WSPReqTest() {
        GeneralValidation.internalValidate(null);
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidaInternal3WSPReqTest() {
        List<String> lstStr = new ArrayList<>();
        lstStr.add("algo");
        GeneralValidation.internalValidate(lstStr);
        GeneralValidation.internalValidate(new ArrayList<>());
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test
    public void getValidateSinErrorTest() {
        generalValidation.validateMax("algo", "nulo", 4);
        generalValidation.throwIfErros();
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidate2Test() {
        GeneralValidation.instantValidateNumeric("", "val");
        GeneralValidation.instantValidateNumeric("22", "val");
        GeneralValidation.instantValidateNumeric("22dd", "val");
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidate3Test() {
        GeneralValidation.instantValidateFecha("");
        GeneralValidation.instantValidateFecha("20190101");
        GeneralValidation.instantValidateFecha("kjhksdfsdf");
    }

    /**
     * Gets the catalogo WSP req test.
     *
     */
    @Test(expected = ExecutionError.class)
    public void getValidate4Test() {
        GeneralValidation.instantValidateFecha("20190101");
        GeneralValidation.instantValidateFecha("kjhkasas");
    }

}
