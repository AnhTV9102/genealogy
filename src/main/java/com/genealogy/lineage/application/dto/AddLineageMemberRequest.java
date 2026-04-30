package com.genealogy.lineage.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddLineageMemberRequest(@NotNull Long userId, @NotBlank String roleCode) {}
