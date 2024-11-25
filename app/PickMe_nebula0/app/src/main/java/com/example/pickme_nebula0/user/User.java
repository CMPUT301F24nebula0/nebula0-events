package com.example.pickme_nebula0.user;

import static java.lang.System.in;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import com.example.pickme_nebula0.qr.QRCodeGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.squareup.picasso.Picasso;
import java.util.Random;



/**
 * User class
 */
public class User {
    protected String userID;
    protected String name;
    protected String email;

    protected String phone;
    boolean notificationsEnabled = true;
    protected String profilePic;

    // todo - start as entrant only then add organizer, admin role conditionally
    protected String role;
    private final ArrayList<String> roles = new ArrayList<String>(Arrays.asList("admin", "entrant", "organizer"));
    protected String status;
    boolean admin;


    public User(){

    }

    public User(String userID, String status) {
        this.userID = userID;
        this.status = status;
    }
    /**
     * Minimum constructor for this class
     *
     * @param deviceID
     * @param name
     * @param email
     */
    public User(String deviceID, String name, String email) {
        this.userID = deviceID;
        this.name = name;
        this.email = email;
        this.genProfilePic();
    }

    public User(Boolean admin, String email, String nam, boolean notificationsEnabled, String phone, String profilePic)
    {
        this.admin = admin;
        this.email = email;
        this.name = nam;
        this.notificationsEnabled = notificationsEnabled;
        this.phone = phone;
        this.profilePic = profilePic;
    }

    /**
     * Additional parameterized constructor
     *
     * @param deviceID
     * @param name
     * @param email
     * @param phoneNumber
     * @param notifEnabled
     */
    public User(String deviceID, String name, String email, String phoneNumber ,Boolean notifEnabled) {
        this.userID = deviceID;
        this.name = name;
        this.email = email;
        this.phone = phoneNumber;
        this.notificationsEnabled = notifEnabled;
        this.genProfilePic();
    }

    public User(String deviceID, String name, String email, String phoneNumber ,String profilePicture) {
        this.userID = deviceID;
        this.name = name;
        this.email = email;
        this.phone = phoneNumber;
        this.profilePic = profilePicture;
    }


    /**
     * Returns the user ID
     *
     * @return  userID  the userID of the user
     */
    public String getUserID() {
        return this.userID;
    }

    /**
     * Sets the user ID
     *
     * @param  userID  the userID of the user
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Returns the first name of the user
     *
     * @return  firstName  the first name of the user
     */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getAdmin() {
        return this.admin;
    }
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean getNotificationsEnabled() {
        return this.notificationsEnabled;
    }
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * correct version
     * @return
     */
    public String getProfilePic() {
        return this.profilePic;
    }
    private void genProfilePic(){
        byte[] byte_name= name.getBytes();
        Bitmap name_bit = BitmapFactory.decodeByteArray(byte_name, 0, byte_name.length);
        Bitmap profile_pic=Bitmap.createBitmap(name_bit.getWidth(), name_bit.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas drawing_tool=new Canvas(profile_pic);
        drawing_tool.drawColor(Color.WHITE);
        ArrayList <Integer> rand_array=new ArrayList<Integer>();
        rand_array.add(Color.BLACK);
        rand_array.add(Color.BLUE);
        rand_array.add( Color.GREEN);
        rand_array.add(Color.CYAN);
        rand_array.add( Color.DKGRAY);
        rand_array.add( Color.GRAY);
        rand_array.add( Color.LTGRAY);
        rand_array.add(Color.RED);
        rand_array.add( Color.MAGENTA);
        rand_array.add( Color.YELLOW);

        Random rand=new Random();
        float r= rand.nextFloat();
        float g= rand.nextFloat();
        float b=rand.nextFloat();
        float a=rand.nextFloat();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Color RandomColor=Color.valueOf(r,b,g,a);

            Paint Randompaint=new Paint();
            Randompaint.setColor(Color.parseColor(RandomColor.toString()));
            drawing_tool.drawBitmap(name_bit,0,0,Randompaint);
        }
        else {
            int k=rand.nextInt(rand_array.size());
            Paint Randompaint=new Paint();
            Randompaint.setColor(Color.parseColor(rand_array.get(k).toString()));
            drawing_tool.drawBitmap(name_bit,0,0,Randompaint);
        }
        this.setProfilePic(Save(profile_pic.toString(), profile_pic));

    }
    public String Save(String name, Bitmap bitmapImage) {

        File path = new File(name);

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(path);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();

        } catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }

        return path.toString();
    }


    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }



    /**
     * Returns the email of the user
     *
     * @return  email  the email of the user
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email of the user
     *
     * @param  email  the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }


    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status;}

    /**
     * Returns the role of the user
     *
     * @return  role  the role of the user
     */
    public String getRole() {
        // check if role is valid
        if (this.roles.contains(this.role)) {
            return this.role;
        } else {
            // in future, throw an exception or return an error message in UI
            return "Invalid role";
        }
    }

    /**
     * Sets the role of the user
     *
     * @param  role  the role of the user
     */
    public void setRole(String role) {
        // check if role is valid
        if (this.roles.contains(role)) {
            this.role = role;
        } else {
            // in future, throw an exception or return an error message in UI
            System.out.println("Invalid role");
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", profilePicture='" + profilePic+ '\'' +
                ", notificationsEnabled=" + notificationsEnabled +
                ", status='" + status + '\'' +
                '}';
    }

}
