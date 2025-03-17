package com.cunhaedu.admin.catalogo.application.category.retrieve.get;

import com.cunhaedu.admin.catalogo.IntegrationTest;
import com.cunhaedu.admin.catalogo.domain.category.Category;
import com.cunhaedu.admin.catalogo.domain.category.CategoryGateway;
import com.cunhaedu.admin.catalogo.domain.category.CategoryId;
import com.cunhaedu.admin.catalogo.domain.exceptions.DomainException;
import com.cunhaedu.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.cunhaedu.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@IntegrationTest
class GetCategoryByIdUseCaseIT {

    @Autowired
    private GetCategoryByIdUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockitoSpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidId_whenCallsGetCategory_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var expectedId = aCategory.getId();

        save(aCategory);

        final var actualCategory = useCase.execute(expectedId.getValue());

        Assertions.assertEquals(expectedId, actualCategory.id());
        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.createdAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.updatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.deletedAt());
    }

    @Test
    void givenAnInvalidId_whenCallsGetCategory_shouldReturnNotFound() {
        final var expectedId = CategoryId.from("123");
        final var expectedErrorMessage = "Category with id 123 was not found";

        final var actualException = Assertions.assertThrows(
                DomainException.class,
                () -> useCase.execute(expectedId.getValue())
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    void givenAValidId_whenGatewayThrowsRandomException_shouldReturnAException() {
        final var expectedId = CategoryId.from("123");
        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(categoryGateway).findByID(expectedId);

        final var actualException = Assertions.assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(expectedId.getValue())
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    private void save(final Category... aCategories) {
        categoryRepository.saveAllAndFlush(
                Arrays.stream(aCategories)
                        .map(CategoryJpaEntity::from)
                        .toList()
        );
    }
}
