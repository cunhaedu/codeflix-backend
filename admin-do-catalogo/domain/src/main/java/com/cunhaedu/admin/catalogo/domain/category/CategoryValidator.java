package com.cunhaedu.admin.catalogo.domain.category;

import com.cunhaedu.admin.catalogo.domain.validation.Error;
import com.cunhaedu.admin.catalogo.domain.validation.ValidationHandler;
import com.cunhaedu.admin.catalogo.domain.validation.Validator;

public class CategoryValidator extends Validator {
    private final Category category;

    public static final int NAME_MIN_LENGTH = 3;
    public static final int NAME_MAX_LENGTH = 255;

    public CategoryValidator(final Category category, final ValidationHandler handler) {
        super(handler);
        this.category = category;
    }

    @Override
    public void validate() {
        this.checkNameConstraints();
    }

    private void checkNameConstraints() {
        var name = this.category.getName();

        if(name == null) {
            this.validationHandler().append(new Error("'name' should not be null"));
            return;
        }

        if(name.isBlank()) {
            this.validationHandler().append(new Error("'name' should not be empty"));
            return;
        }

        if(name.trim().length() < NAME_MIN_LENGTH || name.trim().length() > NAME_MAX_LENGTH) {
            this.validationHandler().append(new Error("'name' must be between 3 and 255 characters"));
        }
    }
}
