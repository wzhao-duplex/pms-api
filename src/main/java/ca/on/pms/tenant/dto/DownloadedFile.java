package ca.on.pms.tenant.dto;

public record DownloadedFile(
        byte[] data,
        String fileName,
        String contentType
) {}
