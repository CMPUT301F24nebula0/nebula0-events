package com.example.pickme_nebula0.qr;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;

public class QRCodeManager {
    public QRCodeManager() {
    }

    public String generateQRCodeURI(String eventID) {
        return "PickMe://event/" + eventID;
    }

    public Bitmap generateQRCodeBitmap(String uri) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            int size = 512;
            com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(uri, BarcodeFormat.QR_CODE, size, size);
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) { return null; }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

}
