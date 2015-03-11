package com.netra.thepuzzler.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.widget.Toast;

public class TestUtil {
	
	private Context mContext;

	
	public TestUtil(Context context) {
		mContext = context;
	}
	
	
	
	/*
	 * Dump the db at various instances at check the values of diff fields inside sqlite db
	 */
	public void dumpDb() {
		File f=new File("/data/data/com.netra.thepuzzler/databases/puzzledb");
		FileInputStream fis=null;
		FileOutputStream fos=null;

		try
		{
		  fis=new FileInputStream(f);
		  fos=new FileOutputStream("/mnt/sdcard/puzzledb-dump.sqlite");
		  while(true)
		  {
		    int i=fis.read();
		    if(i!=-1)
		    {fos.write(i);}
		    else
		    {break;}
		  }
		  fos.flush();
		  Toast.makeText(mContext, "DB dump OK", Toast.LENGTH_LONG).show();
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		  Toast.makeText(mContext, "DB dump ERROR", Toast.LENGTH_LONG).show();
		}
		finally
		{
			  try
			  {
				  fos.close();
				  fis.close();
			  }
			  catch(IOException ioe)
			  {
				  ioe.printStackTrace();
			  }
		}
	}

}
