package com.nkhoang.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;

@Entity(name = "IPricingPolicy")
@Table(name = "PRICING_POLICY")
@DynamicUpdate(value = true)
@SelectBeforeUpdate(value = true)
@DynamicInsert(value = true)
public class PricingPolicyBean {
    public static final String ID = "pricingPolicyKey";
    public static final String NAME = "name";

    /** The bookingType key property */
    private Long key;

    /** The bookingType name property */
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    public Long getKey() {
        return key;
    }

    public void setKey(Long value) {
        key = value;
    }

    @Column(name = NAME, length = 128, nullable = false, updatable = true)
    @Index(name = "IDX_BS_PRICING_POLICY_NAME")
    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }
}