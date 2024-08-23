
package com.fs.fsapi.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorFactoryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class VideoIdValidatorUnitTest {

  private static Validator validator;
  private static VideoIdValidator videoIdValidator;

  private final String videoId = "JM7bKM9uV2I";

  @BeforeAll
  public static void setUpTest() {
    ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
      .constraintValidatorFactory(new CustomConstraintValidatorFactory())
      .buildValidatorFactory();

    validator = validatorFactory.usingContext().getValidator();
    videoIdValidator = new VideoIdValidator();
  }

  @Test
  public void nullPropertyIsValidTestTest() {
    TestObject obj = new TestObject(null);

    Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void charsAndNumbersAreValidTest() {
    TestObject obj = new TestObject(videoId);

    Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void tooShortIsInvalidTest() {
    TestObject obj = new TestObject(videoId.substring(1));

    Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);
    expectVideoIdViolation(violations, "The video id must be 11 characters long");
  }

  @Test
  public void tooLongIsInvalidTest() {
    TestObject obj = new TestObject(videoId + videoId.charAt(0));

    Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);
    expectVideoIdViolation(violations, "The video id must be 11 characters long");
  }

  @ParameterizedTest
  @ValueSource(chars = { '-', '_' })
  public void certainSpecialCharactersAreValidTest(char character) {
    StringBuilder newVideoId = new StringBuilder(videoId);
    newVideoId.setCharAt(4, character);

    TestObject obj = new TestObject(newVideoId.toString());

    Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);
    assertTrue(violations.isEmpty());
  }

  @ParameterizedTest
  @ValueSource(chars = { ',', ' ', '!'})
  public void certainSpecialCharactersAreInValidTest(char character) {
    StringBuilder newVideoId = new StringBuilder(videoId);
    newVideoId.setCharAt(4, character);

    TestObject obj = new TestObject(newVideoId.toString());

    Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);
    expectVideoIdViolation(
      violations, 
      "The video id must consist of characters 'a-z', 'A-Z', '0-9', '-'' and '_'"
    );
  }

  private static void expectVideoIdViolation(
    Set<ConstraintViolation<TestObject>> violations,
    String message
  ) {
    // only a single validation error
    assertEquals(1, violations.size());
    ConstraintViolation<TestObject> violation = violations.iterator().next();

    // error is on video id
    assertEquals(
      VideoId.class,
      violation.getConstraintDescriptor()
        .getAnnotation()
        .annotationType()
    );

    assertEquals(message, violation.getMessage());
  }

  @Getter @Setter
  @AllArgsConstructor @NoArgsConstructor
  public class TestObject {
    
    @VideoId
    private String videoId;
  }

  public static class CustomConstraintValidatorFactory
    implements ConstraintValidatorFactory
  {
    private final ConstraintValidatorFactory hibernateConstraintValidatorFactory
      = new ConstraintValidatorFactoryImpl();

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
      if (VideoIdValidator.class.equals(key)) {
        //noinspection unchecked
        return (T) videoIdValidator;
      }
      return hibernateConstraintValidatorFactory.getInstance(key);
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> instance) {
      hibernateConstraintValidatorFactory.releaseInstance(instance);
    }
  }
}
