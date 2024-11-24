package com.example.pickme_nebula0;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.pickme_nebula0.qr.QRCodeActivity;
import com.example.pickme_nebula0.qr.QRCodeGenerator;
import org.junit.Test;

public class QRcodeTest {
QRCodeGenerator QRTester = new QRCodeGenerator();

    public void TestURIgen(){
        String TestURI=QRTester.generateQRCodeURI("hello");
        assert("PickMe://event/hello".equals(TestURI));

    }
    public void TestQRcodegen(){
        Bitmap bitmap = QRTester.generateQRCodeBitmap("PickMe://event/hello");
        assert(bitmap != null);
        // if passes null assertion, bitmap is an instance of Bitmap
    }

    public void TestturntoBase64(){
        Bitmap testbitmap=Bitmap.createBitmap(12,16, Bitmap.Config.ARGB_8888);
       assert(QRTester.bitmapToBase64(testbitmap) instanceof String);
    }

}
