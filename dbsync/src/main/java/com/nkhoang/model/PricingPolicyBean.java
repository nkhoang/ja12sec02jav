package com.nkhoang.model;

import com.nkhoang.common.persistence.PricingPolicyDataService;
import org.hibernate.annotations.Index;

import javax.persistence.*;

@Entity(name = "IPricingPolicy")
@Table(name = "PRICING_POLICY")
@NamedQueries(value = {
      @NamedQuery(name = PricingPolicyDataService.QUERY_FIND_COUNT, query = "select count(d."
            + IPricingPolicy.KEY + ") from IPricingPolicy d")
})
public class PricingPolicyBean implements IPricingPolicy{

    /** The PricingPolicy key property */
    private Long key;

    /** The PricingPolicy name property */
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
    @Index(name = "IDX_PRICING_POLICY_NAME")
    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }
}