package com.fs.fsapi.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VideoIdValidator implements ConstraintValidator<VideoId, String> {

  private final int VIDEO_ID_LENGTH = 11;

  // https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
  private final Pattern VIDEO_ID_PATTERN = Pattern.compile("^[-\\w]+$");

  @Override
  public boolean isValid(String object, ConstraintValidatorContext context) {
    // Jakarta Bean Validation specification recommends to consider null
    // values as being valid
    if (object == null) {
      return true;
    }

    boolean isCorrectLength = object.length() == VIDEO_ID_LENGTH;
    boolean matchesPattern = VIDEO_ID_PATTERN.matcher(object).matches();

    boolean isValid = isCorrectLength && matchesPattern;

    if (!isValid) {
      // replace the default constraint violation message
      context.disableDefaultConstraintViolation();

      if (!isCorrectLength) {
        String message = String.format(
          "The video id must be %d characters long", VIDEO_ID_LENGTH
        );

        context.buildConstraintViolationWithTemplate(message)
          .addConstraintViolation();

      } else if (!matchesPattern) {
        String message = "The video id must consist of characters "
          + "'a-z', 'A-Z', '0-9', '-'' and '_'";

        context.buildConstraintViolationWithTemplate(message)
          .addConstraintViolation();
      }

      return false;
    }

    return true;
  }
}
