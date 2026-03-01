package co.com.andressierra.usecase.enrollcard;

import co.com.andressierra.usecase.Command;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnrollCommand extends Command {
    private String identifier;
    private Integer validationNumber;
}