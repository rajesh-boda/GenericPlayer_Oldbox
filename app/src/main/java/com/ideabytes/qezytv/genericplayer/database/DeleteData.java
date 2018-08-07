package com.ideabytes.qezytv.genericplayer.database;

import android.content.Context;

import com.ideabytes.qezytv.genericplayer.constants.DBConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : DeleteData
 * author:  Suman
 * Created Date : 18-12-2015
 * Description : This class is used to delet channel information from database table
 * Modified Date : 18-12-2015
 * Reason: Exception mail added
 *************************************************************/
public class DeleteData extends DatabaseHelper implements DBConstants {
    private Context context;//used in creating database connection

    public DeleteData(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * This method is to delete channel information stored in database table
     *
     * @return <Json Object channel information></Json>
     */
    public void deleteData() {
        try {
            //get database connection
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            db = databaseHelper.getWritableDatabase();
            //delete complete database so that it will delete all channel information of client
            context.deleteDatabase(DATABASE_NAME);
        } catch (Exception e) {
            //sending exception to email
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(DeleteData.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        } finally {
            db.close();
        }
    }
}
