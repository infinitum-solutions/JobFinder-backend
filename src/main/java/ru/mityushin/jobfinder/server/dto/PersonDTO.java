package ru.mityushin.jobfinder.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import ru.mityushin.jobfinder.server.util.enums.Sex;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class PersonDTO {

    private UUID uuid;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String oldPassword;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> roles;
    @NotNull
    @Size(min = 1, message = "Title length must be more than 1.")
    private String firstName;
    @NotNull
    @Size(min = 1, message = "Title length must be more than 1.")
    private String lastName;
    private Sex sex;
    private String country;
}
