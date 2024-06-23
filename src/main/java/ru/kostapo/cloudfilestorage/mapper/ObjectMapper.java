package ru.kostapo.cloudfilestorage.mapper;

import io.minio.messages.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoReqObject;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoResObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ObjectMapper {
    ObjectMapper INSTANCE = Mappers.getMapper(ObjectMapper.class);
    @Mapping(target = "holder", source = "username")
    @Mapping(target = "fullPath", source = "multipartFile.originalFilename")
    @Mapping(target = "file", source = "multipartFile")
    MinIoReqObject multipartFileToMinIoObject(String username, MultipartFile multipartFile);

    default List<MinIoReqObject> multipartFilesToMinIoObjectList(String username, MultipartFile[] multipartFiles) {
        return Arrays.stream(multipartFiles)
                .map(multipartFile -> multipartFileToMinIoObject(username, multipartFile))
                .collect(Collectors.toList());
    }

    @Mapping(target = "itIsDir", expression = "java(item.isDir())")
    @Mapping(target = "objectName", expression = "java(getObjectName(item))")
    MinIoResObject itemToMinIoResObject(Item item);

    default String getObjectName(Item item) {
        String[] parts = item.objectName().split("/");
        return parts[parts.length - 1];
    }

    default List<MinIoResObject> mapItemsToMinIoResObjects(List<Item> items) {
        return items.stream()
                .filter(item -> !(!item.isDir() && item.objectName().endsWith("/")))
                .map(this::itemToMinIoResObject)
                .collect(Collectors.toList());
    }
}
