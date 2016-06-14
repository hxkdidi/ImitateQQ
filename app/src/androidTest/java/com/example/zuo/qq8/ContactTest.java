package com.example.zuo.qq8;

import android.test.AndroidTestCase;

import com.example.zuo.qq8.utils.ContactsUtils;

/**
 * Created by taojin on 2016/6/8.15:27
 */
public class ContactTest extends AndroidTestCase {
    public void testContactUtils(){
        String initialChar = ContactsUtils.getInitialChar("张三");
    }
}
