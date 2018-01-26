package com.osp.imagecheck.filenet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.efounder.image.filenet.ContentEngineConnection;
import com.efounder.image.filenet.ContentEngineUtil;
import com.efounder.image.util.CommonUtil;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.BatchItemHandle;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.core.UpdatingBatch;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;
import com.osp.imagecheck.config.PythonConfig;
import com.osp.imagecheck.util.BaseUtils;
import com.osp.imagecheck.util.SFTPUtil;

/**
 * 
 * @Description:TODO filenet处理工具类
 * 
 * @author: zjq
 * @time:2017年7月26日 上午10:27:35
 */
public class FileNetManager {

	/**
	 * filenet 连接 <br>
	 * 
	 * 在\WEB-INF\classes\Package\server\package*.xml中配置 <br>
	 * 
	 * 
	 * {@code  <filenet>  } <br>
	 * {@code <item id="ImageManager" ****** /> 此处id为固定值，不可修改 } <br>
	 * {@code </filenet> } <br>
	 * 
	 * 需要配置参数以及参数说明： <br>
	 * userName 用户名 <br>
	 * password 密码 <br>
	 * JAASStanzaName 凭证验证 <br>
	 * uri 地址 <br>
	 * domainName 域 包含一系列物理资源(如 ObjectStore) <br>
	 * objectStoreName 文件库的名称 <br>
	 * 
	 * @throws Exception
	 * 
	 */
	public static ContentEngineConnection estanblishConnection() throws Exception {
		ContentEngineConnection ce = new ContentEngineConnection();

		String userName = PythonConfig.Username; // userName 用户名
		String password = PythonConfig.Password; // password 密码
		String JAASStanzaName = PythonConfig.JAASStanzaName; // JAASStanzaName
																// 凭证验证
		String uri = PythonConfig.URI; // uri 地址
		String domainName = PythonConfig.DomainName; // domainName 域 包含一系列物理资源(如
		// ObjectStore)

		String objectStoreName = "测试存储库"; // 文件库的名称
		try {
			ce.establishConnection(userName, password, JAASStanzaName, uri, domainName, objectStoreName);
		} catch (EngineRuntimeException e) {
			if (e.getExceptionCode() == ExceptionCode.E_NOT_AUTHENTICATED) {
				System.out.println("提供的登录凭据无效-请再试一次。");
				e.printStackTrace();
				throw CommonUtil.getServiceException("提供的登录凭据无效-请再试一次。");
			} else if (e.getExceptionCode() == ExceptionCode.API_UNABLE_TO_USE_CONNECTION) {
				System.out.println("无法连接到服务器。 请检查网址是否正确，服务器是否正在运行");
				e.printStackTrace();
				throw CommonUtil.getServiceException("无法连接到服务器。 请检查网址是否正确，服务器是否正在运行");
			} else {
				System.out.println(e.getMessage());
				e.printStackTrace();
				throw CommonUtil.getServiceException(e.getMessage());
			}
		}
		return ce;
	}

	/**
	 * 下载影像到获取影像dhash接口的服务器
	 * 
	 * @param connection
	 * @param objectId
	 * @param type
	 * @return
	 */
	public static String downloadFile(ContentEngineConnection connection, String objectId, String type) {
		String fileName = "";
		try {
			ObjectStore os = connection.getDefaultObjectStore();
			Document doc = ContentEngineUtil.fetchDocById(os, objectId);
			ContentElementList eeList = doc.get_ContentElements();
			InputStream is = null;
			if (eeList.size() > 0) {
				is = doc.accessContentStream(0);
				try {
					String path = BaseUtils.getLocalDirectoryPath();
					fileName = objectId + "." + type;
					SFTPUtil sftp = new SFTPUtil(PythonConfig.SFTPUser, PythonConfig.SFTPPSW, PythonConfig.PythonIP, PythonConfig.SFTPPort);  
				    sftp.login();  
				    sftp.upload(path, fileName, is);  		
					fileName = path + "/" + objectId + "." + type;
				    sftp.logout();  
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("影像" + fileName + "下载成功");
			if (FileNetManager.isConnected(connection) == true) {
				connection.closeConnnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}
	

	/**
	 * 是否建立了连接
	 * 
	 * @param ce
	 *            filenet 连接
	 * @return
	 */
	public static boolean isConnected(ContentEngineConnection ce) {
		return ce.isConnected();
	}

	/**
	 * 关闭连接
	 * 
	 * @param ce
	 *            filenet 连接
	 */
	public static void colseConnection(ContentEngineConnection ce) {
		ce.closeConnnection();
	}

	/**
	 * 通过地址 唯一标识下载文件到指定地址
	 * 
	 * @param ce
	 *            filenet 连接
	 * @param objectId
	 *            来源文件的文件id
	 * @param localDirectoryPath
	 *            文件保存的地址
	 * @param nameType
	 *            如果是true 则保存的文件名是文件id命名 id.pdf false时保存的是文件名称名称 filename.pdf
	 */
	public static void downloadDocument(ContentEngineConnection ce, String objectId, String localDirectoryPath,
			boolean nameType) {
		try {
			ObjectStore os = ce.getDefaultObjectStore();
			Document doc = ContentEngineUtil.fetchDocById(os, objectId);
			ContentEngineUtil.writeDocContentToFile(doc, localDirectoryPath, nameType);
		} catch (EngineRuntimeException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 通过地址 文件地址下载文件到指定地址
	 * 
	 * @param ce
	 *            filenet 连接
	 * @param filepath
	 *            来源文件的地址 包含文件名
	 * @param localDirectoryPath
	 *            文件保存的地址
	 * @param nameType
	 *            如果是true 则保存的文件名是文件id命名 id.pdf false时保存的是文件名称名称 filename.pdf
	 */
	public static void downloadDocumentByPath(ContentEngineConnection ce, String filepath, String localDirectoryPath,
			boolean nameType) {
		try {
			ObjectStore os = ce.getDefaultObjectStore();
			Document doc = ContentEngineUtil.fetchDocByPath(os, filepath);
			ContentEngineUtil.writeDocContentToFile(doc, localDirectoryPath, nameType);
		} catch (EngineRuntimeException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 根据文件唯一标识进行单个文件的下载
	 * 
	 * @param ce
	 *            filenet 连接
	 * @param objectId
	 *            来源文件的文件id
	 * @return InputStream
	 */
	public static InputStream downloadDocumentInputStream(ContentEngineConnection ce, String objectId) {
		ObjectStore os = ce.getDefaultObjectStore();
		Document document = ContentEngineUtil.fetchDocById(os, objectId);
		ContentElementList eeList = document.get_ContentElements();
		InputStream inputStream = null;
		if (eeList.size() > 0) {
			inputStream = document.accessContentStream(0);
		}
		return inputStream;
	}

	/**
	 * 根据文件唯一标识进行单个文件的下载
	 * 
	 * @param ce
	 *            filenet 连接
	 * @param objectId
	 *            来源文件的文件id
	 * @return byte[]
	 */
	public static byte[] downloadDocumentByte(ContentEngineConnection ce, String objectId) {
		ObjectStore os = ce.getDefaultObjectStore();
		Document document = ContentEngineUtil.fetchDocById(os, objectId);
		ContentElementList eeList = document.get_ContentElements();
		InputStream inputStream = null;
		if (eeList.size() > 0) {
			inputStream = document.accessContentStream(0);
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[100]; // buff用于存放循环读取的临时数据
			int rc = 0;
			try {
				while ((rc = inputStream.read(buff, 0, 100)) > 0) {
					swapStream.write(buff, 0, rc);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return swapStream.toByteArray();
		} else {
			System.out.println(objectId + "文件内容为空");
		}
		return null;
	}

	/**
	 * 根据文件唯一标识进行单个文件的下载
	 * 
	 * @param ce
	 *            filenet 连接
	 * @param objectId
	 *            来源文件的文件id
	 * @return Document
	 */
	public static Document downloadDocument(ContentEngineConnection ce, String objectId) {
		ObjectStore os = ce.getDefaultObjectStore();
		Document document = ContentEngineUtil.fetchDocById(os, objectId);
		return document;
	}

	/**
	 * 检测文件夹是否存在
	 * 
	 * @param ce
	 *            filenet 连接
	 * @param folderPath
	 *            文件夹地址
	 * @return boolean
	 */
	public static boolean folderExists(ContentEngineConnection ce, String folderPath) {
		ObjectStore os = ce.getDefaultObjectStore();
		return ContentEngineUtil.folderExists(os, folderPath);
	}

	/**
	 * 创建文件夹
	 * 
	 * @param ce
	 *            filenet 连接
	 * @param fPath
	 *            目标路径
	 * @param foldName
	 *            创建文件夹名称
	 * @return 是否创建完成
	 */
	public static boolean creatFold(ContentEngineConnection ce, String fPath, String foldName) {
		ObjectStore os = ce.getDefaultObjectStore();
		try {
			ContentEngineUtil.createFolder(os, fPath, foldName, "Folder");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 检测路径是否存在，如果不存在则创建
	 * 
	 * @param ce
	 *            filenet 连接
	 * @param folderPath
	 *            文件地址
	 */
	public static void checkAndCreatFold(ContentEngineConnection ce, String folderPath) {
		folderPath = folderPath.replaceAll("\\\\", "/");// 格式化路径
		boolean isExist = folderExists(ce, folderPath);
		if (!isExist) {
			int i = folderPath.lastIndexOf("/");
			if (i <= 0) {// 到根目录了，在根目录下创建
				String foldName = folderPath.split("/")[0];
				creatFold(ce, "/", foldName);
			} else {
				String foldName = folderPath.substring(i + 1, folderPath.length());
				String newPath = folderPath.substring(0, i);
				boolean isNewPathExist = folderExists(ce, newPath);
				if (!isNewPathExist) {
					checkAndCreatFold(ce, newPath);
				}
				creatFold(ce, newPath, foldName);
			}
		}
	}

	/**
	 * 删除文件夹 (如果该文件夹中存在子文件夹，并且子文件夹不为空，则无法删除)
	 * 
	 * @param fPath
	 *            文件地址
	 */
	public static void deleteFold(ContentEngineConnection ce, String fPath) {
		ObjectStore os = ce.getDefaultObjectStore();
		Folder folder = Factory.Folder.fetchInstance(os, fPath, null);
		ContentEngineUtil.deleteFolder(folder);
	}

	/**
	 * 上传单个文件
	 * 
	 * @param ce
	 *            filenet 连接
	 * @param filePath
	 *            文件存放地址
	 * @param file
	 *            上传的文件
	 * @return 文件的唯一标识
	 */
	public static String uploadDocument(ContentEngineConnection ce, String filePath, File file) {
		Document doc = null;
		try {
			ObjectStore os = ce.getDefaultObjectStore();
			String fileName = file.getName();
			String conType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			String docTitle = fileName;
			String docClass = "Document";

			doc = ContentEngineUtil.createDocWithContent(file, conType, os, docTitle, docClass);
			doc.save(RefreshMode.REFRESH);
			ReferentialContainmentRelationship rcr = ContentEngineUtil.fileObject(os, doc, filePath);
			rcr.save(RefreshMode.REFRESH);
			ContentEngineUtil.checkinDoc(doc);
		} catch (EngineRuntimeException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		String id = "";
		if (doc != null) {
			id = doc.get_Id().toString();
		}
		return getGuid(id);

	}

	/**
	 * 上传文件
	 * 
	 * @param filePath
	 *            目标地址
	 * @param file
	 *            上传的文件
	 * @param fileName
	 *            文件名称
	 * @return 文件的唯一标识
	 * @throws Exception
	 */
	public static String uploadDocument(ContentEngineConnection ce, String filePath, byte[] file, String fileName)
			throws Exception {
		Document doc = null;
		HashMap<String, Object> docInfoMap = new HashMap<String, Object>();
		try {

			ObjectStore os = ce.getDefaultObjectStore();
			String conType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			String docTitle = fileName;
			String docClass = "Document";

			doc = ContentEngineUtil.createDocWithContent(file, conType, os, docTitle, docClass, fileName);
			doc.save(RefreshMode.REFRESH);
			ReferentialContainmentRelationship rcr = ContentEngineUtil.fileObject(os, doc, filePath);
			rcr.save(RefreshMode.REFRESH);
			ContentEngineUtil.checkinDoc(doc);

		} catch (Exception e) {
			throw e;
		}
		String id = "";
		if (doc != null) {
			id = doc.get_Id().toString();
		}

		return getGuid(id);
	}

	/**
	 * @param ce
	 *            filenet 连接
	 * @param filePath
	 *            文件存放路径
	 * @param fileNameList
	 *            文件名称列表
	 * @param fileList
	 *            文件内容列表
	 * 
	 * @return list
	 * 
	 *         返回结果集 解析方法 <br>
	 *         int batchItemListLen = batchItemList.size(); <br>
	 *         for (int i = 0; i < batchItemListLen; i++) { <br>
	 *         BatchItemHandle bi = batchItemList.get(i); <br>
	 *         if (bi.getObject() instanceof Document) { <br>
	 *         Document document = (Document) bi.getObject(); <br>
	 *         String id = document.get_Id().toString();//其他属性类似这样读取 <br>
	 *         } <br>
	 *         } <br>
	 */
	public static List<BatchItemHandle> batchUploadDocuments(ContentEngineConnection ce, String filePath,
			List<String> fileNameList, List<byte[]> fileList) {

		// 存放key为文件名，value为guid的map
		List<Map<String, String>> responseMapList = null;
		List batchItemList = null;
		try {
			ObjectStore os = ce.getDefaultObjectStore();

			UpdatingBatch ub = UpdatingBatch.createUpdatingBatchInstance(os.get_Domain(), RefreshMode.REFRESH);
			int listLen = fileList.size();
			for (int i = 0; i < listLen; i++) {
				byte[] file = fileList.get(i);
				String fileName = fileNameList.get(i);
				String conType = fileName.substring(fileName.indexOf(".") + 1, fileName.length());
				String docTitle = fileName;
				String docClass = "Document";

				Document doc = ContentEngineUtil.createDocWithContent(file, conType, os, docTitle, docClass, fileName);
				doc.checkin(AutoClassify.AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
				ReferentialContainmentRelationship rcr = ContentEngineUtil.fileObject(os, doc, filePath, fileName);
				ub.add(doc, null);
				ub.add(rcr, null);
			}
			ub.updateBatch();
			batchItemList = ub.getBatchItemHandles(null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return batchItemList;
	}

	/**
	 * 批量上传文件
	 * 
	 * @param ce
	 *            filenet 连接
	 * @param filePath
	 *            文件存放路径
	 * @param fileNameList
	 *            文件名称列表
	 * @param fileList
	 *            文件内容列表
	 * 
	 * @return 上传文件生成的ObjectId的列表
	 */
	public static List<String> batchUploadDocument(ContentEngineConnection ce, String filePath,
			List<String> fileNameList, List<byte[]> fileList) {

		List<String> responseList = null;
		try {
			ObjectStore os = ce.getDefaultObjectStore();

			UpdatingBatch ub = UpdatingBatch.createUpdatingBatchInstance(os.get_Domain(), RefreshMode.REFRESH);
			int listLen = fileList.size();
			for (int i = 0; i < listLen; i++) {
				byte[] file = fileList.get(i);
				String fileName = fileNameList.get(i);
				String conType = fileName.substring(fileName.indexOf(".") + 1, fileName.length());
				String docTitle = fileName;
				String docClass = "Document";

				Document doc = ContentEngineUtil.createDocWithContent(file, conType, os, docTitle, docClass, fileName);
				doc.checkin(AutoClassify.AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
				ReferentialContainmentRelationship rcr = ContentEngineUtil.fileObject(os, doc, filePath, fileName);
				ub.add(doc, null);
				ub.add(rcr, null);
			}
			ub.updateBatch();

			List batchItemList = ub.getBatchItemHandles(null);
			responseList = new ArrayList<String>();
			int batchItemListLen = batchItemList.size();
			for (int i = 0; i < batchItemListLen; i++) {
				BatchItemHandle bi = (BatchItemHandle) batchItemList.get(i);
				if (bi.getObject() instanceof Document) {
					Document document = (Document) bi.getObject();
					responseList.add(document.get_Id().toString());
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (responseList != null) {
			return responseList;
		}
		return null;
	}

	/**
	 * 判断文档是否存在
	 * 
	 * @param ce
	 *            filenet 连接
	 * @param objectId
	 *            文件的唯一标识
	 * @return boolean
	 */
	public static boolean checkDocumentExists(ContentEngineConnection ce, String objectId) {
		boolean flag = false;
		try {
			ObjectStore os = ce.getDefaultObjectStore();
			flag = ContentEngineUtil.documentExists(os, objectId);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 删除单个文档
	 * 
	 * @param objectId
	 *            文件的唯一标识
	 * @return boolean
	 */
	public static boolean deleteDocument(ContentEngineConnection ce, String objectId) {
		try {
			ObjectStore os = ce.getDefaultObjectStore();
			Document document = ContentEngineUtil.fetchDocById(os, objectId);
			document.delete();
			document.save(RefreshMode.NO_REFRESH);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 批量删除文档
	 * 
	 * @param ce
	 *            filenet 连接
	 * @param objectIdList
	 *            删除的文件的objectId 的list
	 * @return boolean
	 */
	public static void batchDeleteDocument(ContentEngineConnection ce, List objectIdList) throws Exception {
		ObjectStore os = ce.getDefaultObjectStore();
		UpdatingBatch ub = UpdatingBatch.createUpdatingBatchInstance(os.get_Domain(), RefreshMode.NO_REFRESH);
		int listLen = objectIdList.size();
		for (int i = 0; i < listLen; i++) {
			String docGUID = (String) objectIdList.get(i);
			Document document = ContentEngineUtil.fetchDocById(os, docGUID);
			document.setUpdateSequenceNumber(null);
			document.delete();
			ub.add(document, null);
		}
		ub.updateBatch();
	}

	/**
	 * 去掉GUID的大括号
	 * 
	 * @param fguid
	 * @return
	 */
	public static String getGuid(String guid) {
		if (guid.startsWith("{") && guid.endsWith("}")) {
			guid = guid.substring(1, guid.length() - 1);// 去掉前后大括号
		}
		return guid;
	}

}
