package com.automarket.entity;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDetails {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_brand_id")
    private CarBrand brand;

    @Column(name = "car_model", nullable = false, length = 100)
    private String model;

    @Column(name = "registration_year", nullable = false)
    private Integer registrationYear;

    @Column(name = "kilometers", nullable = false)
    private Integer kilometers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id")
    private FuelType fuelType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "body_type_id")
    private BodyType bodyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transmission_type_id")
    private TransmissionType transmissionType;

    @Column(name = "num_doors")
    private Integer numDoors;

    @Column(name = "num_seats")
    private Integer numSeats;

    @Column(name = "kilowatts")
    private Integer kilowatts;
}
