package com.example.pickme_nebula0;

import com.example.pickme_nebula0.activities.UserInfoActivity;

import org.junit.Test;

public class UserInfoTest {

    @Test
    public void acceptsValidInputNoPhone(){
        String warn = UserInfoActivity.validateUserInfo("Name","email@mail.com","");
        assert(warn.isBlank());
    }

    @Test
    public void acceptsValidInputWithPhone(){
        String warn = UserInfoActivity.validateUserInfo("Name","email@mail.com","1234567890");
        assert(warn.isBlank());
    }

    @Test
    public void warnsNoName(){
        String warn = UserInfoActivity.validateUserInfo("","email@mail.com","1234567890");
        assert(!warn.isBlank());
    }

    @Test
    public void warnsNoEmail(){
        String warn = UserInfoActivity.validateUserInfo("Name","","1234567890");
        assert(!warn.isBlank());
    }

    @Test
    public void warnsInvalidEmail(){
        String warn = UserInfoActivity.validateUserInfo("Name","myMail","1234567890");
        assert(!warn.isBlank());
    }

    @Test
    public void warnsPhoneTooShort(){
        String warn = UserInfoActivity.validateUserInfo("Name","email@mail.com","123");
        assert(!warn.isBlank());
    }

    @Test
    public void warnsPhoneTooLong(){
        String warn = UserInfoActivity.validateUserInfo("Name","email@mail.com","123456789123456");
        assert(!warn.isBlank());
    }

    @Test
    public void warnsMultiErrors(){
        String warn = UserInfoActivity.validateUserInfo("Name","myMail","123");
        assert(!warn.isBlank());
    }

}
