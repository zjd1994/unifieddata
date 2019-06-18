package com.bootdo.pullfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpTestConnect {
	private static final Logger LOGGER=LoggerFactory.getLogger(FtpUtil.class);
	private  String ip;
	private  int port;
	private  String userName;
	private String password;
	private FTPClient ftpClient=null;
	private FTPSClient ftpClients=null;
	
	public FtpTestConnect(String ip, int port, String userName, String password) {
		super();
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
	
	public boolean connect() throws SocketException, IOException {
		boolean flag=true;
		if(ftpClient==null) {
			ftpClient=new FTPClient();
    		ftpClient.connect(ip, port);
    		LOGGER.info("connect to "+ip);
    		LOGGER.info(ftpClient.getReplyString());
    		int reply=ftpClient.getReplyCode();
    		if(!FTPReply.isPositiveCompletion(reply)) {
    			ftpClient.disconnect();
    			LOGGER.warn("FTP server refused connection");
    			return false;
    		}
    		boolean bok=ftpClient.login(userName, password);
    		if(!bok) {
    			ftpClient.disconnect();
    			ftpClient=null;
    		}
    		ftpClient.setBufferSize(1024);
    		ftpClient.setControlEncoding("GBK");
    		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    		ftpClient.setDataTimeout(120*1000);
    		ftpClient.enterLocalPassiveMode();
    		ftpClient.setUseEPSVwithIPv4(false);
		}
		return flag;
	}
	public List<String>listRemoteAllFiles(String path) throws IOException{
    	ftpClient.enterLocalPassiveMode();
    	FTPFile[]files=ftpClient.listFiles(path,new FTPFileFilter() {
			@Override
			public boolean accept(FTPFile file) {
				if(file.isFile())return true;
				return false;
			}
		});
    	List<String>list=new ArrayList();
    	for(FTPFile file:files) {
    		list.add(file.getName());
    	}
    	return list;
    	
    }
    
    public void closeConect() {
    	if(ftpClient!=null) {
    		try {
				ftpClient.logout();
				ftpClient.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    }
    
    //下载文件
    public boolean downloadFile(String remotePath, String fileName, String localPath) throws Exception {
         
        FileOutputStream fos = null ; 
        try {
            File localFile = new File(localPath, fileName);
            fos = new FileOutputStream(localFile);
            if(!System.getProperty("os.name").contains("Windows")) {
            	//设置访问被动模式
                ftpClient.setRemoteVerificationEnabled(false);
                ftpClient.enterLocalPassiveMode();
            }
            ftpClient.enterLocalPassiveMode(); 
            ftpClient.changeWorkingDirectory(remotePath) ;
            boolean bok = ftpClient.retrieveFile(fileName, fos);
             
            fos.close() ;
            fos = null ;
             
            return bok ;
        } catch (Exception e) {
            throw e ;
        }
        finally {
            if (fos!=null) {
                try {
                    fos.close() ;
                    fos = null ;
                } catch (Exception e2) { }
            }
        } 
         
    }
        //上传文件
    public boolean uploadFile(String remotePath, String filename, String localFilePath) throws Exception {
        FileInputStream fis = null ;
        try {
            fis = new FileInputStream(new File(localFilePath));
             
            ftpClient.enterLocalPassiveMode(); 
            ftpClient.changeWorkingDirectory(remotePath);
            boolean bok = ftpClient.storeFile(filename, fis); 
             
            fis.close();
            fis = null ;
             
            return bok ;
        } catch (Exception e) {
            throw e ;
        }
        finally {
            if (fis!=null) {
                try {
                    fis.close() ;
                    fis = null ;
                } catch (Exception e2) { }
            }
        }
 
    }
    //删除文件
    public boolean removeFile(String remotePath, String filename) throws Exception {
        ftpClient.changeWorkingDirectory(remotePath);
        boolean bok = ftpClient.deleteFile(filename) ; 
        return bok ;
    }
    public static void main(String[] args) throws Exception {
    	FtpTestConnect ftp=new FtpTestConnect( "10.186.65.218", 5389,"jtoper01", "&YHM2rxn");
    	String name = System.getProperty("os.name");
		ftp.connect();
    	ftp.downloadFile("", "", "");
    	
    	
    	
    	
	}
}
