package com.cunhaedu.admin.catalogo.application.category.retrieve.list;

import com.cunhaedu.admin.catalogo.application.UseCase;
import com.cunhaedu.admin.catalogo.domain.category.CategorySearchQuery;
import com.cunhaedu.admin.catalogo.domain.pagination.Pagination;

public abstract class ListCategoriesUseCase
        extends UseCase<CategorySearchQuery, Pagination<CategoryListOutput>> {}
