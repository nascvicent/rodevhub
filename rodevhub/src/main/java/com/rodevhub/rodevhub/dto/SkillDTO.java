package com.rodevhub.rodevhub.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillDTO {

    @NotBlank(message = "Nome da skill é obrigatório")
    @Size(max = 60, message = "Nome máximo 60 caracteres")
    private String name;

    @Min(value = 0, message = "Nível mínimo é 0")
    @Max(value = 100, message = "Nível máximo é 100")
    private Integer level;
}
