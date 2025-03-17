package com.cunhaedu.admin.catalogo.infrastructure.category;

import com.cunhaedu.admin.catalogo.domain.category.Category;
import com.cunhaedu.admin.catalogo.domain.category.CategoryGateway;
import com.cunhaedu.admin.catalogo.domain.category.CategoryId;
import com.cunhaedu.admin.catalogo.domain.category.CategorySearchQuery;
import com.cunhaedu.admin.catalogo.domain.pagination.Pagination;
import com.cunhaedu.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.cunhaedu.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.cunhaedu.admin.catalogo.infrastructure.utils.SpecificationUtils.like;

import java.util.Optional;

@Service
public class CategoryMySQLGateway implements CategoryGateway {
    
    private final CategoryRepository categoryRepository;

    public CategoryMySQLGateway(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category create(final Category aCategory) {
        return this.save(aCategory);
    }

    @Override
    public Category update(final Category aCategory) {
        return this.save(aCategory);
    }

    @Override
    public void deleteByID(final CategoryId anId) {
        String anIdValue = anId.getValue();

        // using this if to avoid an exception if the id does not exist
        if(this.categoryRepository.existsById(anIdValue)) {
            this.categoryRepository.deleteById(anIdValue);
        }
    }

    @Override
    public Optional<Category> findByID(final CategoryId anId) {
        return this.categoryRepository.findById(anId.getValue())
                .map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Pagination<Category> findAll(final CategorySearchQuery aQuery) {
        // Paginação
        final var page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        // Busca dinamica pelo criterio terms (name ou description)
        final var specifications = Optional.ofNullable(aQuery.terms())
                .filter(str -> !str.isBlank())
                .map(this::assembleSpecification)
                .orElse(null);

        final var pageResult =
                this.categoryRepository.findAll(Specification.where(specifications), page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CategoryJpaEntity::toAggregate).toList()
        );
    }

    public Category save(final Category aCategory) {
        return this.categoryRepository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
    }

    private Specification<CategoryJpaEntity> assembleSpecification(final String str) {
        final Specification<CategoryJpaEntity> nameLike = like("name", str);
        final Specification<CategoryJpaEntity> descriptionLike = like("description", str);
        return nameLike.or(descriptionLike);
    }
}
