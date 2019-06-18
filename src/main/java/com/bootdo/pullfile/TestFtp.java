package com.bootdo.pullfile;

public class TestFtp {
	public static void main(String[] args) {
		/*
		 * System.out.println(new Date()+"  开始进入ftpDownload定时器");
		 * 
		 * //ftp服务器登录凭证 String host=PropertiesManager.getProperty("ftpHost"); int
		 * port=Integer.parseInt(PropertiesManager.getProperty("ftpPort")); String
		 * user=PropertiesManager.getProperty("ftpUser"); String
		 * password=PropertiesManager.getProperty("ftpPassword");
		 * 
		 * //获取时间字段信息 SimpleDateFormat sdf1=new SimpleDateFormat("yyyyMMdd");
		 * SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); Date date=new
		 * Date(); String today1 = sdf1.format(date); String today = sdf.format(date);
		 * 
		 * String txtFileDir="/"; String txtSaveDir="E:/dataCenter/shengzhan/";
		 * 
		 * //检查本地磁盘目录是否存在txt文件 boolean flag = isTxtExit(today1,txtSaveDir);
		 * System.out.println(new Date()+"  判断txt文件是否存在："+flag);
		 * FlagUtil.ftpDownloadRunning=true;
		 * 
		 * //讲txt的下载操作和解析操作分成2个独立的操作进行，排除互相间的干扰 if(flag==false)//文件不存在进行ftp下载操作 {
		 * FTPClient ftp=null; try { //ftp的数据下载 ftp=new FTPClient(); ftp.connect(host,
		 * port); ftp.login(user, password);
		 * ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		 * 
		 * //设置linux环境 FTPClientConfig conf = new FTPClientConfig(
		 * FTPClientConfig.SYST_UNIX); ftp.configure(conf);
		 * 
		 * //判断是否连接成功 int reply = ftp.getReplyCode(); if
		 * (!FTPReply.isPositiveCompletion(reply)) { ftp.disconnect();
		 * System.out.println("FTP server refused connection."); return; }
		 * 
		 * //设置访问被动模式 ftp.setRemoteVerificationEnabled(false);
		 * ftp.enterLocalPassiveMode();
		 * 
		 * 
		 * //检索ftp目录下所有的文件，利用时间字符串进行过滤 boolean dir =
		 * ftp.changeWorkingDirectory(txtFileDir); if (dir) { FTPFile[]fs =
		 * ftp.listFiles(); for(FTPFile f:fs) { if(f.getName().indexOf(today1+"2000")>0)
		 * { System.out.println(new Date()+"  ftpDownload定时器下载txt成功"); File localFile =
		 * new File(txtSaveDir+f.getName()); OutputStream ios = new
		 * FileOutputStream(localFile); ftp.retrieveFile(f.getName(), ios); ios.close();
		 * break; } } } } catch (Exception e) { e.printStackTrace();
		 * System.out.println(new Date()+"  ftp下载txt文件发生错误"); } finally { if(ftp !=
		 * null) try {ftp.disconnect();} catch (IOException ioe) {} }
		 */
	}
}
