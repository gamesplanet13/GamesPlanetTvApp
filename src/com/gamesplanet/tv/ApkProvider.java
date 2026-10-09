package com.gamesplanet.tv;
import android.content.*;import android.database.*;import android.net.Uri;import android.os.*;import java.io.*;
public class ApkProvider extends ContentProvider {
 public boolean onCreate(){return true;}
 public String getType(Uri u){return "application/vnd.android.package-archive";}
 public ParcelFileDescriptor openFile(Uri u,String mode)throws FileNotFoundException{
  if(!"/retroarch.apk".equals(u.getPath())||!"r".equals(mode))throw new FileNotFoundException();
  return ParcelFileDescriptor.open(new File(getContext().getExternalFilesDir(null),"retroarch.apk"),ParcelFileDescriptor.MODE_READ_ONLY);
 }
 public Cursor query(Uri u,String[]p,String s,String[]a,String o){return null;}
 public Uri insert(Uri u,ContentValues v){throw new UnsupportedOperationException();}
 public int delete(Uri u,String s,String[]a){throw new UnsupportedOperationException();}
 public int update(Uri u,ContentValues v,String s,String[]a){throw new UnsupportedOperationException();}
}
