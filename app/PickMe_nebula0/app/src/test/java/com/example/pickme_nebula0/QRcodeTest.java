package com.example.pickme_nebula0;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.pickme_nebula0.qr.QRCodeActivity;
import com.example.pickme_nebula0.qr.QRCodeManager;
import org.junit.Test;

public class QRcodeTest {

    public void TestURIgen(){
        QRCodeManager Test=new QRCodeManager();

        String TestURI=Test.generateQRCodeURI("hello");
        assert("PickMe://event/hello"== TestURI);

    }
    public void TestQRcodegen(){

        QRCodeManager Test=new QRCodeManager();
     assert(Test.generateQRCodeBitmap("PickMe://event/hello") instanceof Bitmap);

    }

    public void TestturntoBase64(){
        QRCodeManager Test=new QRCodeManager();
        Bitmap testbitmap=Bitmap.createBitmap(12,16, Bitmap.Config.ARGB_8888);
       assert( Test.bitmapToBase64(testbitmap) instanceof String);
    }

}
