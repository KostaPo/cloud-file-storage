package ru.kostapo.cloudfilestorage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.entity.dto.FileReqDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);
    @Mapping(target = "holder", source = "username")
    @Mapping(target = "fullPath", source = "multipartFile.originalFilename")
    @Mapping(target = "file", source = "multipartFile")
    FileReqDto multipartFileToFileDto(String username, MultipartFile multipartFile);

    default List<FileReqDto> multipartFilesToListDto(String username, MultipartFile[] multipartFiles) {
        return Arrays.stream(multipartFiles)
                .map(multipartFile -> multipartFileToFileDto(username, multipartFile))
                .collect(Collectors.toList());
    }
}
