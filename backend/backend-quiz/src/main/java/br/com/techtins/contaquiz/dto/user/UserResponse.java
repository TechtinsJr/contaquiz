package br.com.techtins.contaquiz.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.com.techtins.contaquiz.model.SystemRole;

public record UserResponse(
    Long id,
    String name,
    String email,
    @JsonProperty("systemRole") SystemRole role
) {

}
