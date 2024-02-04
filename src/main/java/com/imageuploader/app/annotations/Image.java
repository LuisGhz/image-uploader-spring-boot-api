package com.imageuploader.app.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { Image.ImageValidator.class })
public @interface Image {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    public class ImageValidator implements ConstraintValidator<Image, MultipartFile> {

        String imageEmptyMessage = "File is empty";
        String imageTypeMessage = "File type is not supported";
        String imageSizeMessage = "File size exceeds the limit of 2MB";

        @Override
        public boolean isValid(MultipartFile image, ConstraintValidatorContext context) {
            if (image == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("File is required").addConstraintViolation();
                return false;
            }

            boolean isNotEmpty = !isEmpty(image);
            boolean isValidImageType = isValidImageType(image);
            boolean isValidImageSize = isValidImageSize(image);
            boolean isValid = isNotEmpty && isValidImageType && isValidImageSize;

            if (!isNotEmpty) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(imageEmptyMessage).addConstraintViolation();
            }
            if (!isValidImageType) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(imageTypeMessage).addConstraintViolation();
            }
            if (!isValidImageSize) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(imageSizeMessage).addConstraintViolation();
            }
            return isValid;
        }

        private boolean isEmpty(MultipartFile file) {
            return file.isEmpty();
        }

        private boolean isValidImageType(MultipartFile file) {
            return file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")
                    || file.getContentType().equals("image/gif");
        }

        private boolean isValidImageSize(MultipartFile file) {
            int kb = 1024;
            int mb = kb * kb;
            int maxSize = 2 * mb;
            return file.getSize() <= maxSize;
        }

    }
}