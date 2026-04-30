package com.genealogy.lineage.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLineageRequest(@NotBlank @Size(max = 255) String name) {}
