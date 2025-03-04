package com.cunhaedu.admin.catalogo.domain.validation.handler;

import com.cunhaedu.admin.catalogo.domain.exceptions.DomainException;
import com.cunhaedu.admin.catalogo.domain.validation.Error;
import com.cunhaedu.admin.catalogo.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class Notification implements ValidationHandler {
    private final List<Error> errors;

    private Notification(List<Error> errors) {
        this.errors = errors;
    }

    public static Notification create() {
        return new Notification(new ArrayList<>());
    }

    public static Notification create(final Error anError) {
        return new Notification(new ArrayList<>()).append(anError);
    }

    public static Notification create(final Throwable t) {
        return Notification.create(new Error(t.getMessage()));
    }

    @Override
    public Notification append(final Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public Notification append(ValidationHandler handler) {
        this.errors.addAll(handler.getErrors());
        return this;
    }

    @Override
    public Notification validate(final Validation validation) {
        try {
            validation.validate();
        } catch (final DomainException domainException) {
            this.errors.addAll(domainException.getErrors());
        } catch (final RuntimeException exception) {
            this.errors.add(new Error(exception.getMessage()));
        }

        return this;
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }
}
