package com.cunhaedu.admin.catalogo.application.category.update;

import com.cunhaedu.admin.catalogo.IntegrationTest;
import com.cunhaedu.admin.catalogo.domain.category.Category;
import com.cunhaedu.admin.catalogo.domain.category.CategoryGateway;
import com.cunhaedu.admin.catalogo.domain.exceptions.DomainException;
import com.cunhaedu.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.cunhaedu.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@IntegrationTest
class UpdateCategoryUseCaseIT {

    @Autowired
    private UpdateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockitoSpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        final var category = Category.newCategory("Film", null, true);

        save(category);

        final var expectedId = category.getId();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        Assertions.assertEquals(1, categoryRepository.count());

        final var actualOutput = useCase.execute(aCommand).get();

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final var actualCategory = categoryRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertTrue(category.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertEquals(category.getDeletedAt(), actualCategory.getDeletedAt());
    }

    @Test
    void givenAInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() {
        final var category = Category.newCategory("Film", null, true);

        save(category);

        final var expectedId = category.getId();

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var aCommand =
                UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        final var notification = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());

        Mockito.verify(categoryGateway, times(0)).update(any());
    }

    @Test
    void givenAValidCommandWithInactiveCategory_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
        final var category = Category.newCategory("Film", null, true);

        save(category);

        final var expectedId = category.getId();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        Assertions.assertTrue(category.isActive());
        Assertions.assertNull(category.getDeletedAt());

        final var actualOutput = useCase.execute(aCommand).get();

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final var actualCategory = categoryRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertTrue(category.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnAException() {
        final var category = Category.newCategory("Film", null, true);

        save(category);

        final var expectedId = category.getId();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Gateway error";

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        Mockito.doThrow(new IllegalStateException(expectedErrorMessage))
                        .when(categoryGateway).update(any());

        final var notification = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());

        final var actualCategory = categoryRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(category.getName(), actualCategory.getName());
        Assertions.assertEquals(category.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(category.isActive(), actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(category.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    void givenACommandWithInvalidId_whenCallsUpdateCategory_shouldReturnNotFoundException() {
        final var expectedId = "123";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedError = "Category with id 123 was not found";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedError, actualException.getErrors().get(0).message());

    }

    private void save(final Category... aCategories) {
        categoryRepository.saveAllAndFlush(
                Arrays.stream(aCategories)
                        .map(CategoryJpaEntity::from)
                        .toList()
        );
    }
}
