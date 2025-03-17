package com.cunhaedu.admin.catalogo.infrastructure.category;

import com.cunhaedu.admin.catalogo.domain.category.Category;
import com.cunhaedu.admin.catalogo.domain.category.CategoryId;
import com.cunhaedu.admin.catalogo.domain.category.CategorySearchQuery;
import com.cunhaedu.admin.catalogo.MySQLGatewayTest;
import com.cunhaedu.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.cunhaedu.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@MySQLGatewayTest
class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void givenAValidCategory_whenCallsCreate_shouldReturnANewCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        Assertions.assertEquals(0, categoryRepository.count());

        final var actualCategory = categoryGateway.create(aCategory);

        Assertions.assertEquals(1, categoryRepository.count());
        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

        final var actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        Assertions.assertEquals(aCategory.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualEntity.getUpdatedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
    }

    @Test
    void givenAValidCategory_whenCallsUpdate_shouldReturnACategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(
                "Filme",
                null,
                expectedIsActive
        );

        Assertions.assertEquals(0, categoryRepository.count());

        final var invalidCategory = categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertEquals("Filme", invalidCategory.getName());
        Assertions.assertNull(invalidCategory.getDescription());
        Assertions.assertTrue(invalidCategory.isActive());

        Assertions.assertEquals(1, categoryRepository.count());

        final var aUpdatedCategory = aCategory
                .copy()
                .update(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = categoryGateway.update(aUpdatedCategory);

        Assertions.assertEquals(1, categoryRepository.count());
        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertNull(actualCategory.getDeletedAt());

        final var actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        Assertions.assertEquals(aCategory.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertTrue(aCategory.getUpdatedAt().isBefore(actualEntity.getUpdatedAt()));
        Assertions.assertNull(actualEntity.getDeletedAt());
    }

    @Test
    void givenAPrePersistedCategory_whenCallsDeleteById_thenShouldDeleteCategory() {
        final var aCategory = Category.newCategory("Filmes", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertEquals(1, categoryRepository.count());

        categoryGateway.deleteByID(aCategory.getId());

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    void givenAInvalidCategoryId_whenCallsDeleteById_thenShouldDeleteCategory() {
        Assertions.assertEquals(0, categoryRepository.count());

        categoryGateway.deleteByID(CategoryId.from("invalid"));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    void givenAPrePersistedCategory_whenCallsFindById_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertEquals(1, categoryRepository.count());

        final var actualCategory = categoryGateway.findByID(aCategory.getId()).get();

        Assertions.assertEquals(1, categoryRepository.count());
        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    void givenANotPrePersistedCategory_whenCallsFindById_shouldReturnEmpty() {
        Assertions.assertEquals(0, categoryRepository.count());

        final var actualCategory = categoryGateway.findByID(CategoryId.from("empty"));

        Assertions.assertTrue(actualCategory.isEmpty());
    }

    @Test
    void givenPrePersistedCategories_whenCallsFindAll_thenShouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        Assertions.assertEquals(3, categoryRepository.count());

        final var searchQuery = new CategorySearchQuery(0, 1, "", "name", "asc");
        final var actualResult = this.categoryGateway.findAll(searchQuery);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());

        Assertions.assertEquals(documentarios.getId(), actualResult.items().get(0).getId());
    }

    @Test
    void givenNoPrePersistedCategories_whenCallsFindAll_thenShouldReturnEmptyPaged() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        Assertions.assertEquals(0, categoryRepository.count());

        final var searchQuery = new CategorySearchQuery(0, 1, "", "name", "asc");
        final var actualResult = this.categoryGateway.findAll(searchQuery);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(0, actualResult.items().size());
    }

    @Test
    void givenFollowPagination_whenCallsFindAllWithPage1_thenShouldReturnPaged() {
        var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        Assertions.assertEquals(3, categoryRepository.count());

        var searchQuery = new CategorySearchQuery(0, 1, "", "name", "asc");
        var actualResult = this.categoryGateway.findAll(searchQuery);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());

        Assertions.assertEquals(documentarios.getId(), actualResult.items().get(0).getId());

        // page 1
        expectedPage = 1;

        searchQuery = new CategorySearchQuery(1, 1, "", "name", "asc");
        actualResult = this.categoryGateway.findAll(searchQuery);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());

        Assertions.assertEquals(filmes.getId(), actualResult.items().get(0).getId());

        // page 1
        expectedPage = 2;

        searchQuery = new CategorySearchQuery(2, 1, "", "name", "asc");
        actualResult = this.categoryGateway.findAll(searchQuery);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());

        Assertions.assertEquals(series.getId(), actualResult.items().get(0).getId());
    }

    @Test
    void givenPrePersistedCategoriesAndDocAsTerm_whenCallsFindAllAndTermsMatchCategoryName_thenShouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        Assertions.assertEquals(3, categoryRepository.count());

        final var searchQuery = new CategorySearchQuery(0, 1, "doc", "name", "asc");
        final var actualResult = this.categoryGateway.findAll(searchQuery);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());

        Assertions.assertEquals(documentarios.getId(), actualResult.items().get(0).getId());
    }

    @Test
    void givenPrePersistedCategoriesAndMaisAssistidasAsTerm_whenCallsFindAllAndTermsMatchCategoryName_thenShouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Séries", "Uma categoria assisida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria menos assistida", true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        Assertions.assertEquals(3, categoryRepository.count());

        final var searchQuery = new CategorySearchQuery(0, 1, "MAIS ASSISTIDA", "name", "asc");
        final var actualResult = this.categoryGateway.findAll(searchQuery);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());

        Assertions.assertEquals(filmes.getId(), actualResult.items().get(0).getId());
    }
}
