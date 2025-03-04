package com.cunhaedu.admin.catalogo.domain.category;

import com.cunhaedu.admin.catalogo.domain.pagination.Pagination;

import java.util.Optional;

public interface CategoryGateway {

    Category create(Category category);

    Category update(Category category);

    void deleteByID(CategoryId id);

    Optional<Category> findByID(CategoryId id);

    Pagination<Category> findAll(CategorySearchQuery searchQuery);
}
