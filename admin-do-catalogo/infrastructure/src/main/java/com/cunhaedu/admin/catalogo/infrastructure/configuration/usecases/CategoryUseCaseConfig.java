package com.cunhaedu.admin.catalogo.infrastructure.configuration.usecases;

import com.cunhaedu.admin.catalogo.application.category.create.CreateCategoryUseCase;
import com.cunhaedu.admin.catalogo.application.category.create.DefaultCreateCategoryUseCase;
import com.cunhaedu.admin.catalogo.application.category.delete.DefaultDeleteCategoryUseCase;
import com.cunhaedu.admin.catalogo.application.category.delete.DeleteCategoryUseCase;
import com.cunhaedu.admin.catalogo.application.category.retrieve.get.DefaultGetCategoryByIdUseCase;
import com.cunhaedu.admin.catalogo.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.cunhaedu.admin.catalogo.application.category.retrieve.list.DefaultListCategoriesUseCase;
import com.cunhaedu.admin.catalogo.application.category.retrieve.list.ListCategoriesUseCase;
import com.cunhaedu.admin.catalogo.application.category.update.DefaultUpdateCategoryUseCase;
import com.cunhaedu.admin.catalogo.application.category.update.UpdateCategoryUseCase;
import com.cunhaedu.admin.catalogo.domain.category.CategoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryUseCaseConfig {

    private final CategoryGateway categoryGateway;

    public CategoryUseCaseConfig(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase() {
        return new DefaultCreateCategoryUseCase(this.categoryGateway);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase() {
        return new DefaultUpdateCategoryUseCase(this.categoryGateway);
    }

    @Bean
    public GetCategoryByIdUseCase getCategoryByIdUseCase() {
        return new DefaultGetCategoryByIdUseCase(this.categoryGateway);
    }

    @Bean
    public ListCategoriesUseCase listCategoriesUseCase() {
        return new DefaultListCategoriesUseCase(this.categoryGateway);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase() {
        return new DefaultDeleteCategoryUseCase(this.categoryGateway);
    }
}
