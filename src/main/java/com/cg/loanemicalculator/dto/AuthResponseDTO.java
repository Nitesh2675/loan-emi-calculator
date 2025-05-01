package com.cg.loanemicalculator.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO<T,U> {
    private String message;
    private String messageData;
    private T data;
    private U token;

}
