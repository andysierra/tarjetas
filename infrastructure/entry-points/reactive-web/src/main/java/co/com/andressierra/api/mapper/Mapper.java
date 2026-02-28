package co.com.andressierra.api.mapper;

import co.com.andressierra.api.rest.request.CreateCardRequest;
import co.com.andressierra.usecase.createcard.CreateCommand;

public class Mapper {
    private Mapper(){}

    public static CreateCommand toCommand(CreateCardRequest request) {
        return CreateCommand.builder()
                .pan(request.getPan())
                .cardholderName(request.getCardholderName())
                .cardholderId(request.getCardholderId())
                .cardType(request.getCardType())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }
}
