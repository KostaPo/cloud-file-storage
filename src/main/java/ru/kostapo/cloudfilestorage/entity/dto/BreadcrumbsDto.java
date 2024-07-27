package ru.kostapo.cloudfilestorage.entity.dto;

import lombok.Data;

import java.util.LinkedHashMap;

@Data
public class BreadcrumbsDto {
    private String currentPath;
    private LinkedHashMap<String, String> directoryPath;
}
