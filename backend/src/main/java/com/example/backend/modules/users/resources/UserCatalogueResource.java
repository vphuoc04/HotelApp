package com.example.backend.modules.users.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCatalogueResource {
    private final Long id;
    private final Long addedBy;
    private final Long editedBy;
    private final String name;
    private final Integer publish;
}
