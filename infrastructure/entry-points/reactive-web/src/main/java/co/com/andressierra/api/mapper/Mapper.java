package co.com.andressierra.api.mapper;

import co.com.andressierra.api.rest.request.CreateCardRequest;
import co.com.andressierra.api.rest.request.EnrollCardRequest;
import co.com.andressierra.api.rest.response.CreateCardResponse;
import co.com.andressierra.api.rest.response.EnrollCardResponse;
import co.com.andressierra.api.rest.response.GetCardResponse;
import co.com.andressierra.model.card.Card;
import co.com.andressierra.usecase.createcard.CreateCommand;
import co.com.andressierra.usecase.enrollcard.EnrollCommand;

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

    public static EnrollCommand toCommand(EnrollCardRequest request, String identifier) {
        return EnrollCommand.builder()
                .identifier(identifier)
                .validationNumber(request.getValidationNumber())
                .build();
    }

    public static CreateCardResponse toResponse(Card card) {
        return CreateCardResponse.builder()
                .validationNumber(card.getValidationNumber())
                .maskedPan(card.getMaskedPan())
                .identifier(card.getIdentifier())
                .build();
    }

    public static EnrollCardResponse toEnrollResponse(Card card) {
        return EnrollCardResponse.builder()
                .maskedPan(card.getMaskedPan())
                .build();
    }

    public static GetCardResponse toGetCardResponse(Card card) {
        return GetCardResponse.builder()
                .maskedPan(card.getMaskedPan())
                .cardholderName(card.getCardholderName())
                .cardholderId(card.getCardholderId())
                .phoneNumber(card.getPhoneNumber())
                .status(card.getStatus())
                .build();
    }
}
