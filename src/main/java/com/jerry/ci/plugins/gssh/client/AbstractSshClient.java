package com.jerry.ci.plugins.gssh.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Random;

import com.jerry.ci.plugins.gssh.GsshPluginException;
import com.jerry.ci.plugins.gssh.Utils;

public abstract class AbstractSshClient implements SshClient {
	public static final String TEMP_PATH = "/var";
	public static final String LATEEST_EXEC_SHELL_DEBUG = "/var/gssh_debug.sh";

	public void uploadFile(PrintStream logger, String fileName, File file,
			String serverLocation) {
		logger.println("sftp upload file [" + file + "] to target location ["
				+ serverLocation + "] with file name is [" + fileName + "]");
		InputStream fileContent = null;
		try {
			if (!file.exists()) {
				logger.println("[GSSH-FTP] ERROR as: sftp upload local file ["
						+ file + "] can't find !");
			}
			fileContent = new FileInputStream(file);
			uploadFile(logger, fileName, fileContent, serverLocation);
		} catch (FileNotFoundException e) {
			String message = "[GSSH-FTP] ERROR as: sftp upload local file ["
					+ file + "] can't find !";
			logger.println(message);
			e.printStackTrace(logger);
			throw new GsshPluginException(message, e);
		} catch (Exception e) {
			String message = "[GSSH-FTP] ERROR as with below errors logs:";
			logger.println(message);
			e.printStackTrace(logger);
			throw new GsshPluginException(message, e);
		} finally {
			if (null != fileContent) {
				try {
					fileContent.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void uploadFile(PrintStream logger, String fileName,
			String fileContent, String serverLocation) {
		InputStream bis = new ByteArrayInputStream(fileContent.getBytes());
		uploadFile(logger, fileName, bis, serverLocation);
		if (null != bis) {
			try {
				bis.close();
			} catch (IOException e) {
			}
		}
	}

	public void downloadFile(PrintStream logger, String remoteFile,
			String localFolder) {
		File rf = new File(remoteFile);
		downloadFile(logger, remoteFile, localFolder, rf.getName());
	}

	public void executeShellByFTP(PrintStream logger, InputStream shell) {
		Random random = new Random();

		String shellName = "tempshell_" + System.currentTimeMillis()
				+ random.nextInt() + ".sh";
		String shellFile = TEMP_PATH + "/" + shellName;
		try {
			uploadFile(logger, shellName, shell, TEMP_PATH);
			chmod(logger, 777, shellFile);
			executeCommand(logger, ". " + shellFile);
		} finally {
			rm_Rf(logger, LATEEST_EXEC_SHELL_DEBUG);
			mv(logger, shellFile, LATEEST_EXEC_SHELL_DEBUG);
		}
	}

	public void executeShellByFTP(PrintStream logger, String shell) {
		Random random = new Random();
		logger.println("execute shell as : ");
		logger.println(shell);
		String shellName = "tempshell_" + System.currentTimeMillis()
				+ random.nextInt() + ".sh";

		String shellFile = TEMP_PATH + "/" + shellName;
		try {
			uploadFile(logger, shellName, shell, TEMP_PATH);
			chmod(logger, 777, shellFile);
			executeCommand(logger, ". " + shellFile);
		} finally {
			rm_Rf(logger, LATEEST_EXEC_SHELL_DEBUG);
			mv(logger, shellFile, LATEEST_EXEC_SHELL_DEBUG);
		}
	}

	public void chmod(PrintStream logger, int mode, String path) {
		executeCommand(logger, "chmod " + mode + " " + path);
	}

	public void chown(PrintStream logger, String own, String path) {
		executeCommand(logger, "chown " + own + " " + path);
	}

	public void mv(PrintStream logger, String source, String dest) {
		executeCommand(logger, "mv " + source + " " + dest);
	}

	public void rm_Rf(PrintStream logger, String path) {
		executeCommand(logger, "rm -rf " + path);
	}

	public void executeCommand(PrintStream logger, InputStream command) {
		String content = Utils.getStringFromStream(command);
		executeCommand(logger, content);
	}
}
