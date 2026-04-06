package com.techcup.techcup_futbol.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.DiscriminatorValue;

@EqualsAndHashCode(callSuper = true)
@Data
@DiscriminatorValue("EXTERNAL")
public class ExternalPlayerEntity extends PlayerEntity {
}