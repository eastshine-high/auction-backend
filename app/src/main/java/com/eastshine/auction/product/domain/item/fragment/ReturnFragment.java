package com.eastshine.auction.product.domain.item.fragment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor
@Embeddable
public class ReturnFragment {
    private String returnChargeName;
    private String returnContactNumber;
    private String returnZipCode;
    private String returnAddress;
    private String returnAddressDetail;
    private Integer returnCharge;

    @Builder
    public ReturnFragment(String returnChargeName, String returnContactNumber, String returnZipCode, String returnAddress, String returnAddressDetail, Integer returnCharge) {
        this.returnChargeName = returnChargeName;
        this.returnContactNumber = returnContactNumber;
        this.returnZipCode = returnZipCode;
        this.returnAddress = returnAddress;
        this.returnAddressDetail = returnAddressDetail;
        this.returnCharge = returnCharge;
    }
}
