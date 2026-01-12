package com.finpulse_engine.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExpenseSetting implements Serializable {
    private String heading;
    private boolean isMandatory;
}
