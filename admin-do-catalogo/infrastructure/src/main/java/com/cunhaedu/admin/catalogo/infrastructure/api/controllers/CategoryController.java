package com.cunhaedu.admin.catalogo.infrastructure.api.controllers;

import com.cunhaedu.admin.catalogo.application.category.create.CreateCategoryCommand;
import com.cunhaedu.admin.catalogo.application.category.create.CreateCategoryOutput;
import com.cunhaedu.admin.catalogo.application.category.create.CreateCategoryUseCase;
import com.cunhaedu.admin.catalogo.domain.pagination.Pagination;
import com.cunhaedu.admin.catalogo.domain.validation.handler.Notification;
import com.cunhaedu.admin.catalogo.infrastructure.api.CategoryAPI;
import com.cunhaedu.admin.catalogo.infrastructure.category.models.CreateCategoryApiInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;
import java.util.function.Function;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;

    public CategoryController(final CreateCategoryUseCase createCategoryUseCase) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
    }

    @Override
    public ResponseEntity<?> createCategory(CreateCategoryApiInput input) {
        final var command = CreateCategoryCommand.with(
                input.name(),
                input.description(),
                input.active() != null ? input.active() : true
        );

        final Function<Notification, ResponseEntity<?>> onError = notification ->
                ResponseEntity.unprocessableEntity().body(notification);

        final Function<CreateCategoryOutput, ResponseEntity<?>> onSuccess = output ->
                ResponseEntity.created(URI.create("/categories/" + output.id())).body(output);

        return this.createCategoryUseCase.execute(command)
                .fold(onError, onSuccess);
    }

    @Override
    public Pagination<?> listCategories(
            String search,
            int page,
            int perPage,
            String sort,
            String direction
    ) {
        return null;
    }
}
