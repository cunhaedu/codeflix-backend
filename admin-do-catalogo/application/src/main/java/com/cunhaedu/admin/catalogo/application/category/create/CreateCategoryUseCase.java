package com.cunhaedu.admin.catalogo.application.category.create;

import com.cunhaedu.admin.catalogo.application.UseCase;
import com.cunhaedu.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class CreateCategoryUseCase
        extends UseCase<CreateCategoryCommand, Either<Notification, CreateCategoryOutput>> {
}
