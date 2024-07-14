package ru.kostapo.cloudfilestorage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.kostapo.cloudfilestorage.entity.dto.BreadcrumbsDto;

import java.util.LinkedHashMap;

@Mapper
public interface BreadcrumbsMapper {

    BreadcrumbsMapper INSTANCE = Mappers.getMapper(BreadcrumbsMapper.class);

    @Mapping(target = "directoryPath", source = "path", qualifiedByName = "splitPath")
    BreadcrumbsDto mapToDto(String path);

    @Named("splitPath")
    default LinkedHashMap<String,String> splitPath(String path) {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        if(!path.isEmpty()) {
            String[] parts = path.split("/");
            String key = "";
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {
                    key = parts[i];
                } else {
                    key += "/" + parts[i];
                }
                linkedHashMap.put(parts[i], key);
            }
        }
        return linkedHashMap;
    }
}