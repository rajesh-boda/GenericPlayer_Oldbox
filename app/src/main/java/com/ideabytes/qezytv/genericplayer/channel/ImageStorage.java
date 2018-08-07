package com.ideabytes.qezytv.genericplayer.channel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : ImageStorage
 * author:  Suman
 * Created Date : 08-12-2015
 * Description :This class is to check client existence in sd card
 * Modified Date : 26-04-2016
 * Reason: loadFromFile method added to get logo bitmap
 *************************************************************/
public class ImageStorage implements FolderAndURLConstants {
    private static final String LOGTAG = "ImageStorage";
    private Activity activity;
    public ImageStorage(Activity activity) {
        this.activity = activity;
    }

    /**
     * This method is to check image bitmap of client default_logo
     * @param filePath
     * @return image bitmap
     */
    public static Bitmap getImage(String filePath) {
        Bitmap bitmap = null;
        try {
            File file = new File(filePath);
            if(file.exists() && file.isDirectory()) {
                // do something here
               // Log.e(LOGTAG, "Boss file already exist so deleting");
                file.delete();
            }
            //Log.v(LOGTAG,"image path "+file.getAbsolutePath());
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
           // Log.v(LOGTAG,"bitmap "+bitmap);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Utils.convertExceptionToString(e);
        }
        return bitmap;
    }

    /**
     * This method returns bitmap of client logo
     * @param filename
     * @return bitmap
     */
    public static Bitmap loadFromFile(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) { return null; }
            Bitmap tmp = BitmapFactory.decodeFile(filename);
            return tmp;
        } catch (Exception e) {
            return null;
        }
    }
}
