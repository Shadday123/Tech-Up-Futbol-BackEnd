package com.techcup.techcup_futbol.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@DiscriminatorValue("INSITUTIONAL")
public class InstitutionalPlayerEntity extends PlayerEntity {
}