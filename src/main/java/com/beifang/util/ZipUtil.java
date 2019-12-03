package com.beifang.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.io.Files;

public class ZipUtil {

	public static File makeZip(List<File> files, File zip) throws IOException {
		if (!zip.exists()) {
			zip.createNewFile();
		}
		if (ListUtil.isEmpty(files)) {
			return zip;
		}
		try (ZipOutputStream out = new ZipOutputStream(
				new BufferedOutputStream(new FileOutputStream(zip)), Charset.forName("GB2312"));) {
			for (File f : files) {
				out.putNextEntry(new ZipEntry(f.getName()));
				out.write(Files.toByteArray(f));
				out.closeEntry();
			}
			out.flush();
		}
		return zip;
	}

}
