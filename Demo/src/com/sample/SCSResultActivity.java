package com.sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;

import com.sina.cloudstorage.auth.AWSCredentials;
import com.sina.cloudstorage.auth.BasicAWSCredentials;
import com.sina.cloudstorage.services.scs.SCS;
import com.sina.cloudstorage.services.scs.SCSClient;
import com.sina.cloudstorage.services.scs.model.AccessControlList;
import com.sina.cloudstorage.services.scs.model.Bucket;
import com.sina.cloudstorage.services.scs.model.BucketInfo;
import com.sina.cloudstorage.services.scs.model.ObjectInfo;
import com.sina.cloudstorage.services.scs.model.ObjectListing;
import com.sina.cloudstorage.services.scs.model.ObjectMetadata;
import com.sina.cloudstorage.services.scs.model.Permission;
import com.sina.cloudstorage.services.scs.model.PutObjectResult;
import com.sina.cloudstorage.services.scs.model.S3Object;
import com.sina.cloudstorage.services.scs.model.UserIdGrantee;

public class SCSResultActivity extends Activity{
	
	String accessKey = "accessKey";
	String accessSecret = "accessSecret";
	
	AWSCredentials credentials = new BasicAWSCredentials(accessKey,accessSecret);
	SCS conn = new SCSClient(credentials);
	
	Handler handler = null;
	ProgressDialog progressDialog = null;
	
	private EditText resultET;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scsresult);
	
		handler = new Handler(getMainLooper());
		resultET = (EditText)findViewById(R.id.result_edittext);
		
		Intent intent = getIntent();
		final int position = intent.getIntExtra("position", 0);
		String title = intent.getStringExtra("title");
		setTitle(title);
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					requestServerByPosition(position);
				} catch (Exception e) {
					e.printStackTrace();
					final String errorMsg = e.getLocalizedMessage();
					handler.post(new Runnable() {
						@Override
						public void run() {
							updateUI(errorMsg);
						}
					});
				}
			}
		});
		
		thread.start();
		progressDialog = ProgressDialog.show(this, title, "请求服务器中，请稍后...", true, false);
	}
	
	/**
	 * 根据position调用对应功能
	 * @param position
	 * @throws Exception
	 */
	@SuppressWarnings("serial")
	private void requestServerByPosition(int position) throws Exception{
		String resultString = null;
		switch(position){
		case 1:
			//列所有Bucket
			resultString = conn.listBuckets().toString();
			break;
		case 3:
			//列Bucket中所有object
			ObjectListing objectListing = conn.listObjects("test11");
			resultString = objectListing.toString();
			break;
		case 4:
			//创建Bucket
			Bucket bucket = conn.createBucket("create-a-bucket22");
			resultString = bucket.toString();
			break;
		case 5:
			//删除Bucket
			conn.deleteBucket("create-a-bucket22");
			resultString = "Operation is Done!";
			break;
		case 6:{
			//获取Bucket ACL
			AccessControlList acl = conn.getBucketAcl("create-a-bucket22");
			resultString = acl.toString();
			break;
		}
		case 7:{
			//设置Bucket ACL
			AccessControlList acl = new AccessControlList();
			acl.grantPermissions(UserIdGrantee.CANONICAL, Permission.Read,Permission.ReadAcp);
			acl.grantPermissions(UserIdGrantee.ANONYMOUSE,Permission.ReadAcp,Permission.Write,Permission.WriteAcp);
			acl.grantPermissions(new UserIdGrantee("SINA000000"+accessKey), Permission.Read,Permission.ReadAcp,Permission.Write,Permission.WriteAcp);
			conn.setBucketAcl("create-a-bucket22", acl);
			resultString = "Operation is Done!";
			break;
		}
		case 8:{
			//获取Bucket Info
			BucketInfo bucketInfo = conn.getBucketInfo("create-a-bucket22");
			resultString = bucketInfo.toString();
			break;
		}
		case 10:{
			//获取Object信息
			ObjectMetadata metadata = conn.getObjectMetadata("asdasdasdasd", "aaa111a.txt");
			
			StringBuffer sb = new StringBuffer("");
			sb.append("UserMetadata："+metadata.getUserMetadata()+"\n");
			sb.append("ETag："+metadata.getETag()+"\n");
			sb.append("LastModified："+metadata.getLastModified()+"\n");
			sb.append("RawMetadata："+metadata.getRawMetadata()+"\n");
			resultString = sb.toString();
			break;
		}
		case 11:{
			//下载Object
			String objectFilePath = "aaa111a.txt";
			File destFile = null;
			String basePath = getExternalStorageBasePath();
			if(basePath!=null){
				destFile = new File(basePath+"/"+objectFilePath);
				S3Object s3Obj = conn.getObject("asdasdasdasd", objectFilePath);
				InputStream in = s3Obj.getObjectContent();
				byte[] buf = new byte[1024];
				OutputStream out = null;
				try {
					out = new FileOutputStream(destFile);
					int count;
					while( (count = in.read(buf)) != -1)
					{
					   if( Thread.interrupted() )
					   {
					       throw new InterruptedException();
					   }
					   out.write(buf, 0, count);
					}
					
					resultString = "Operation is Done!";
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else{
				resultString = "请检查是否有可用的externalStorage！";
			}
			break;
		}
		case 12:{
			//上传Object
			String localFilePath = "aaa111a.txt";
			String basePath = getExternalStorageBasePath();
			if(basePath!=null){
				File localFile = new File(basePath+"/"+localFilePath);
				if(localFile.exists()){
					PutObjectResult putObjectResult = conn.putObject("asdasdasdasd", "android_upload.txt", localFile);
					resultString = putObjectResult.toString();
				}else{
					resultString = "本地文件不存在，请先点击下载文件！";
				}
			}else{
				resultString = "请检查是否有可用的externalStorage！";
			}
			
			break;
		}
		case 13:
			//Object秒传
			PutObjectResult por = conn.putObjectRelax("asdasdasdasd","android_put_relax.txt","4a09518d3c402d0a444e2f6c964a1b5a0c206455",48);
			resultString = por.toString();
			break;
		case 14:{
			//设置Object Metadata
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setUserMetadata(new HashMap<String,String>(){{
				put("aaa","1111");
				put("bbb","222");
				put("ccc","3333");
				put("asdfdsaf","vvvvvv");
			}});
			conn.setObjectMetadata("asdasdasdasd", "aaa111a.txt", objectMetadata);
			resultString = "Operation is Done!";
			break;
		}
		case 15:{
			//获取Object Metadata
			ObjectMetadata metadata = conn.getObjectMetadata("asdasdasdasd", "aaa111a.txt");
			StringBuffer sb = new StringBuffer("");
			sb.append("UserMetadata："+metadata.getUserMetadata()+"\n");
			sb.append("ETag："+metadata.getETag()+"\n");
			sb.append("RawMetadata："+metadata.getRawMetadata()+"\n");
			resultString = sb.toString();
			break;
		}
		case 16:{
			//删除Object
			conn.deleteObject("asdasdasdasd", "android_upload.txt");
			resultString = "Operation is Done!";
			break;
		}
		case 17:{
			//获取Object ACL
			AccessControlList acl = conn.getObjectAcl("asdasdasdasd", "awsdas阿斯顿.txt");
			resultString = acl.toString();
			break;
		}
		case 18:{
			//设置Object ACL
			AccessControlList acl = new AccessControlList();
			acl.grantPermissions(UserIdGrantee.CANONICAL, Permission.Read,Permission.ReadAcp);
			acl.grantPermissions(UserIdGrantee.ANONYMOUSE,Permission.ReadAcp,Permission.Write,Permission.WriteAcp);
			acl.grantPermissions(new UserIdGrantee("SINA000000"+accessKey), Permission.Read,Permission.ReadAcp,Permission.Write,Permission.WriteAcp);
			
			conn.setObjectAcl("asdasdasdasd", "awsdas阿斯顿.txt", acl);
			resultString = "Operation is Done!";
			break;
		}
		case 19:{
			//获取Object Info
			ObjectInfo objectInfo = conn.getObjectInfo("asdasdasdasd", "awsdas阿斯顿.txt");
			resultString = objectInfo.toString();
			break;
		}
		case 21:
			//文件分片上传
			break;
		case 22:
			//获取已上传的分片列表
			break;
		case 24:{
			//生成URL文件地址
			Date expiration = new Date();
	        long epochMillis = expiration.getTime();
	        epochMillis += 60*5*1000;
	        expiration = new Date(epochMillis);   
	        
			URL presignedUrl = conn.generatePresignedUrl("asdasdasdasd", "awsdas阿斯顿.txt", expiration, false);
			resultString = presignedUrl.toString();
			break;
		}
		}
		
		final String resultStr = resultString;
		handler.post(new Runnable() {
			@Override
			public void run() {
				updateUI(resultStr);
			}
		});
	}
	
	/////////////////////
	//	工具方法
	/////////////////////
	/**
	 * 更新UI内容
	 * @param requestResult
	 */
	private void updateUI(final String requestResult){
		if(Looper.myLooper() != Looper.getMainLooper()){
			handler.post(new Runnable() {
				@Override
				public void run() {
					resultET.setText(requestResult);
					progressDialog.dismiss();
				}
			});
		}else{
			resultET.setText(requestResult);
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 判断sd卡是否可写
	 * @return
	 */
	private boolean isExternalStorageWriteable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 获取存储文件的根路径
	 * @return
	 */
	private String getExternalStorageBasePath(){
		if(isExternalStorageWriteable()){
			File file = new File(Environment.getExternalStorageDirectory()+"/sinastorage/");
			file.mkdirs();
			
			return file.getAbsolutePath();
		}
		
		return null;
	}
	
}
