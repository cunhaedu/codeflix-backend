package com.cunhaedu.admin.catalogo.application.category.retrieve.get;

import com.cunhaedu.admin.catalogo.application.category.update.UpdateCategoryCommand;
import com.cunhaedu.admin.catalogo.application.category.update.UpdateCategoryOutput;
import com.cunhaedu.admin.catalogo.application.category.update.UpdateCategoryUseCase;
import com.cunhaedu.admin.catalogo.domain.category.Category;
import com.cunhaedu.admin.catalogo.domain.category.CategoryGateway;
import com.cunhaedu.admin.catalogo.domain.category.CategoryId;
import com.cunhaedu.admin.catalogo.domain.exceptions.DomainException;
import com.cunhaedu.admin.catalogo.domain.validation.Error;
import com.cunhaedu.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

import java.util.Objects;
import java.util.function.Supplier;

import static io.vavr.API.Left;
import static io.vavr.API.Try;

public class DefaultGetCategoryByIdUseCase extends GetCategoryByIdUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultGetCategoryByIdUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public CategoryOutput execute(final String anId) {
        final var categoryId = CategoryId.from(anId);
        return this.categoryGateway.findByID(categoryId)
                .map(CategoryOutput::from)
                .orElseThrow(notFound(categoryId));
    }

    private Supplier<DomainException> notFound(final CategoryId anId) {
        return () -> DomainException.with(
                new Error("Category with id %s was not found".formatted(anId.getValue()))
        );
    }
}
