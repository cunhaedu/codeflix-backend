package com.cunhaedu.admin.catalogo.application.category.delete;

import com.cunhaedu.admin.catalogo.IntegrationTest;
import com.cunhaedu.admin.catalogo.domain.category.Category;
import com.cunhaedu.admin.catalogo.domain.category.CategoryGateway;
import com.cunhaedu.admin.catalogo.domain.category.CategoryId;
import com.cunhaedu.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.cunhaedu.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@IntegrationTest
class DeleteCategoryUseCaseIT {

    @Autowired
    private DeleteCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockitoSpyBean
    private CategoryGateway categoryGateway;


    @Test
    void givenAValidId_whenCallsDeleteCategory_shouldBeOK() {
        final var aCategory =
                Category.newCategory("Filme", "A categoria mais assistida", true);

        final var expectedId = aCategory.getId();

        save(aCategory);

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    void givenAnInvalidId_whenCallsDeleteCategory_shouldBeOK() {
        final var expectedId = CategoryId.from("123");

        Assertions.assertEquals(0, categoryRepository.count());

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    void givenAValidId_whenGatewayThrowsError_shouldReturnException() {
        final var expectedId = CategoryId.from("123");

        doThrow(new IllegalStateException("Gateway error"))
                .when(this.categoryGateway).deleteByID(expectedId);

        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        Mockito.verify(categoryGateway, times(1)).deleteByID(expectedId);
    }

    private void save(final Category... aCategories) {
        categoryRepository.saveAllAndFlush(
                Arrays.stream(aCategories)
                        .map(CategoryJpaEntity::from)
                        .toList()
        );
    }
}
