package ru.kostapo.cloudfilestorage.entity.dto;

import lombok.Data;

@Data
public class MinIoResObject {
    private boolean itIsDir;
    private String fullPath;
    private String objectName;
}
