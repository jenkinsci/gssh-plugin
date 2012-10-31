package com.jerry.ci.plugins.gssh.client;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

public interface SshClient {

	void executeCommand(PrintStream logger, String command);

	void executeShell(PrintStream logger, String shell);

	void executeShellByFTP(PrintStream logger, String shell);

	void uploadFile(PrintStream logger, String fileName, String fileContent,
			String serverLocation);

	void uploadFile(PrintStream logger, String fileName,
			InputStream fileContent, String serverLocation);

	void uploadFile(PrintStream logger, String fileName, File file,
			String serverLocation);

	void downloadFile(PrintStream logger, String remoteFile,
			String localFolder, String fileName);

	void downloadFile(PrintStream logger, String remoteFile, String localFolder);

	void chmod(PrintStream logger, int mode, String path);

	void chown(PrintStream logger, String own, String path);

	void mv(PrintStream logger, String source, String dest);

	void rm_Rf(PrintStream logger, String path);

	boolean testConnection(PrintStream logger);

}