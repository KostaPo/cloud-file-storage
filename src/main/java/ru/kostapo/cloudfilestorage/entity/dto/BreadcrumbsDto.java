package ru.kostapo.cloudfilestorage.entity.dto;

import lombok.Data;

import java.util.LinkedHashMap;

@Data
public class BreadcrumbsDto {
    private LinkedHashMap<String, String> directoryPath;
}
