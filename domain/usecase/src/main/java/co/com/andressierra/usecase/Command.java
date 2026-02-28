package co.com.andressierra.usecase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Command {
    private String traceId;
}
