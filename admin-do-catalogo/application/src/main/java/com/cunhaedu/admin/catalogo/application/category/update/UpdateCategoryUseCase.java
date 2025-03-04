package com.cunhaedu.admin.catalogo.application.category.update;

import com.cunhaedu.admin.catalogo.application.UseCase;
import com.cunhaedu.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class UpdateCategoryUseCase
        extends UseCase<UpdateCategoryCommand, Either<Notification, UpdateCategoryOutput>> {
}
