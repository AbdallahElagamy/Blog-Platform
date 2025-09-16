package com.learning.blog.mapper;

import com.learning.blog.model.Category;
import com.learning.blog.model.Post;
import com.learning.blog.model.dtos.CategoryDto;
import com.learning.blog.model.dtos.CreateCategoryRequest;
import com.learning.blog.model.enums.PostStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
    CategoryDto toDto(Category category);
    Category toEntity(CreateCategoryRequest createCategoryRequest);

    @Named("calculatePostCount")
    default long calculatePostCount(List<Post> posts) {
        if(posts == null) {
            return 0;
        }
        return posts.stream()
                .filter(p -> p.getStatus().equals(PostStatus.PUBLISHED))
                .count();
    }

}
