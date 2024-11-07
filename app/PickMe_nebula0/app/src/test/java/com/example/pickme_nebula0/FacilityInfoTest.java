package com.example.pickme_nebula0;

import com.example.pickme_nebula0.user.activities.FacilityInfoActivity;

import org.junit.Test;

/**
 * Tests input validation for FacilityInfoActivity where organizers create/manage a facility profile
 * @author Stephine Yearley
 * @see FacilityInfoActivity
 */
public class FacilityInfoTest {
    @Test
    public void acceptsValidInput(){
        String warn = FacilityInfoActivity.validateFacilityInfo("myFacilityName", "myFacilityAddress");
        assert (warn.isBlank());
    }

    @Test
    public void warnsNoFacilityName(){
        String warn = FacilityInfoActivity.validateFacilityInfo("", "myFacilityAddress");
        assert (!warn.isBlank());
    }

    @Test
    public void warnsNoFacilityAddress(){
        String warn = FacilityInfoActivity.validateFacilityInfo("myFacilityName", "");
        assert (!warn.isBlank());
    }
}
