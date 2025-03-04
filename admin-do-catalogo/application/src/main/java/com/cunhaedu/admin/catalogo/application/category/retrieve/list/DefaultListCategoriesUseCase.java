package com.cunhaedu.admin.catalogo.application.category.retrieve.list;

import com.cunhaedu.admin.catalogo.domain.category.CategoryGateway;
import com.cunhaedu.admin.catalogo.domain.category.CategorySearchQuery;
import com.cunhaedu.admin.catalogo.domain.pagination.Pagination;

import java.util.Objects;

public class DefaultListCategoriesUseCase extends ListCategoriesUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultListCategoriesUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Pagination<CategoryListOutput> execute(final CategorySearchQuery query) {
        return categoryGateway.findAll(query).map(CategoryListOutput::from);
    }
}
