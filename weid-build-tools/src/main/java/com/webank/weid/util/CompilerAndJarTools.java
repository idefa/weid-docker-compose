/*
 *       CopyrightÂ© (2018-2020) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-build-tools.
 *
 *       weidentity-build-tools is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-build-tools is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-build-tools.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

@Slf4j
public class CompilerAndJarTools {
    
    private static volatile JavaCompiler javaCompiler;
    
    private String javaSourcePath;
    
    private String javaClassPath;
    
    private String targetPath;
    
    CompilerAndJarTools(String javaSourcePath, String javaClassPath, String targetPath){
        this.javaSourcePath=javaSourcePath;
        this.javaClassPath=javaClassPath;
        this.targetPath = targetPath;
    }

    private static JavaCompiler getJavaCompiler() {
        if (javaCompiler == null) {
            synchronized (CompilerAndJarTools.class) {
                if (javaCompiler == null) {
                    javaCompiler = ToolProvider.getSystemJavaCompiler();
                }
            }
        }
        return javaCompiler;
    }
    
    public static CompilerAndJarTools instance(
        String javaSourcePath,
        String javaClassPath,
        String targetPath) {
        return new CompilerAndJarTools(javaSourcePath, javaClassPath, targetPath);
    }
    
    public CompilerAndJarTools complier() throws IOException {  
        log.info("[complier] begin complier java source code.");
        File javaclassDir = new File(javaClassPath);  
        if (!javaclassDir.exists()) {  
            javaclassDir.mkdirs();  
        }  
        List<File> javaSourceList = new ArrayList<File>();  
        getFileList(new File(javaSourcePath), javaSourceList);  
        // ĺ»şç«‹DiagnosticCollectorĺŻąč±ˇ
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        //čŻĄć–‡ä»¶ç®ˇç?†ĺ™¨ĺ®žäľ‹çš„ä˝śç”¨ĺ°±ć?Żĺ°†ć?‘ä»¬éś€č¦?ĺŠ¨ć€?çĽ–čŻ‘çš„javaćş?ć–‡ä»¶č˝¬ćŤ˘ä¸şgetTaskéś€č¦?çš„çĽ–čŻ‘ĺŤ•ĺ…?
        StandardJavaFileManager fileManager = getJavaCompiler()
            .getStandardFileManager(diagnostics, null, null);
        // čŽ·ĺŹ–č¦?çĽ–čŻ‘çš„çĽ–čŻ‘ĺŤ•ĺ…?
        Iterable<? extends JavaFileObject> compilationUnits = fileManager
            .getJavaFileObjectsFromFiles(javaSourceList);
        /**
         * çĽ–čŻ‘é€‰éˇąďĽŚĺś¨çĽ–čŻ‘javać–‡ä»¶ć—¶ďĽŚçĽ–čŻ‘ç¨‹ĺşŹäĽšč‡ŞĺŠ¨çš„ĺŽ»ĺŻ»ć‰ľjavać–‡ä»¶ĺĽ•ç”¨çš„ĺ…¶ä»–çš„javaćş?ć–‡ä»¶ć?–č€…classă€‚ 
         * -sourcepathé€‰éˇąĺ°±ć?Żĺ®šäą‰javaćş?ć–‡ä»¶çš„ćźĄć‰ľç›®ĺ˝•ďĽŚ
         * -classpathé€‰éˇąĺ°±ć?Żĺ®šäą‰classć–‡ä»¶çš„ćźĄć‰ľç›®ĺ˝•ďĽŚ
         * -dĺ°±ć?ŻçĽ–čŻ‘ć–‡ä»¶çš„čľ“ĺ‡şç›®ĺ˝•ă€‚
         */
        String jars = JsonInclude.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        Iterable<String> options = Arrays.asList(
            "-encoding","utf-8","-classpath",jars,"-d", javaClassPath, "-sourcepath", javaSourcePath);
        /**
         * ç¬¬ä¸€ä¸ŞĺŹ‚ć•°ä¸şć–‡ä»¶čľ“ĺ‡şďĽŚčż™é‡Ść?‘ä»¬ĺŹŻä»Ąä¸ŤćŚ‡ĺ®šďĽŚć?‘ä»¬é‡‡ç”¨javacĺ‘˝ä»¤çš„-dĺŹ‚ć•°ćťĄćŚ‡ĺ®šclassć–‡ä»¶çš„ç”źć??ç›®ĺ˝•
         * ç¬¬äşŚä¸ŞĺŹ‚ć•°ä¸şć–‡ä»¶ç®ˇç?†ĺ™¨ĺ®žäľ‹  fileManager
         * ç¬¬ä¸‰ä¸ŞĺŹ‚ć•°DiagnosticCollector<JavaFileObject> diagnosticsć?Żĺś¨çĽ–čŻ‘ĺ‡şé”™ć—¶ďĽŚĺ­?ć”ľçĽ–čŻ‘é”™čŻŻäżˇć?Ż
         * ç¬¬ĺ››ä¸ŞĺŹ‚ć•°ä¸şçĽ–čŻ‘ĺ‘˝ä»¤é€‰éˇąďĽŚĺ°±ć?Żjavacĺ‘˝ä»¤çš„ĺŹŻé€‰éˇąďĽŚčż™é‡Ść?‘ä»¬ä¸»č¦?ä˝żç”¨äş†-dĺ’Ś-sourcepathčż™ä¸¤ä¸Şé€‰éˇą
         * ç¬¬äş”ä¸ŞĺŹ‚ć•°ä¸şç±»ĺ?Ťç§°
         * ç¬¬ĺ…­ä¸ŞĺŹ‚ć•°ä¸şä¸Šéť˘ćŹ?ĺ?°çš„çĽ–čŻ‘ĺŤ•ĺ…?ďĽŚĺ°±ć?Żć?‘ä»¬éś€č¦?çĽ–čŻ‘çš„javaćş?ć–‡ä»¶
         */
        JavaCompiler.CompilationTask task = getJavaCompiler().getTask(
                null,
                fileManager,
                diagnostics,
                options,
                null,
                compilationUnits);
        // çĽ–čŻ‘ćş?ç¨‹ĺĽŹ
        boolean success = task.call();
        fileManager.close();
        if (success) {
            log.info("[complier] complier successfully.");
        } else {
            log.error("[complier] complier fail.");
            throw new RuntimeException("complier fail");
        }
        return this;
    }
    
    private void getFileList(File file, List<File> fileList) throws IOException {  
        if (file.isDirectory()) {  
            File[] files = file.listFiles();  
            for (int i = 0; i < files.length; i++) {  
                if (files[i].isDirectory()) {  
                    getFileList(files[i], fileList);  
                } else {  
                    fileList.add(files[i]);  
                }  
            }  
        }  
    }
    
    public void generateJar() throws FileNotFoundException, IOException {  
        log.info("[generateJar] begin generate Jar");
        File file = new File(targetPath);
        createTempJar(javaClassPath, file.getParentFile().getAbsolutePath(), file.getName());
        log.info("[generateJar] generate jar finish, file = {}", targetPath);
    }
    
    /**
     * @param rootPath    classć–‡ä»¶ć ąç›®ĺ˝•
     * @param targetPath  éś€č¦?ĺ°†jarĺ­?ć”ľçš„č·Żĺľ„
     * @param jarFileName jarć–‡ä»¶çš„ĺ?Ťç§°
     * @exception IOException IOĺĽ‚ĺ¸¸ć?…ĺ†µ
     */
    public static void createTempJar(String rootPath, String targetPath, String jarFileName) throws IOException {
        if (!new File(rootPath).exists()) {
            throw new IOException(String.format("%sč·Żĺľ„ä¸Ťĺ­?ĺś¨", rootPath));
        }
        if (StringUtils.isBlank(jarFileName)) {
            throw new NullPointerException("jarFileNameä¸şç©ş");
        }
        // ç”źć??META-INFć–‡ä»¶
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        // ĺ?›ĺ»şä¸´ć—¶jar
        File jarFile = File.createTempFile("edwin-", ".jar", new File(System.getProperty("java.io.tmpdir")));
        FileOutputStream fos = new FileOutputStream(jarFile);
        JarOutputStream out = new JarOutputStream(fos, manifest);
        createTempJarInner(out, new File(rootPath), "");
        out.flush();
        out.close();
        fos.close();
        // ç”źć??ç›®ć ‡č·Żĺľ„
        File targetJarFile = new File(targetPath + File.separator + jarFileName);
        if (targetJarFile.exists() && targetJarFile.isFile()) {
            targetJarFile.delete();
        }
        FileUtils.moveFile(jarFile, targetJarFile);
    }

    /**
     * @Description: ç”źć??jarć–‡ä»¶
     * @param out  ć–‡ä»¶čľ“ĺ‡şćµ?
     * @param f    ć–‡ä»¶ä¸´ć—¶File
     * @param base ć–‡ä»¶ĺźşçˇ€ĺŚ…ĺ?Ť
     * @return void
     * @Author zhangchengping
     * @Date 2019-06-07 00:02
     */
    private static void createTempJarInner(JarOutputStream out, File f, String base) throws IOException {

        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            if (base.length() > 0) {
                base = base + "/";
            }
            for (int i = 0; i < fl.length; i++) {
                createTempJarInner(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putNextEntry(new JarEntry(base));
            FileInputStream in = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            int n = in.read(buffer);
            while (n != -1) {
                out.write(buffer, 0, n);
                n = in.read(buffer);
            }
            in.close();
        }
    }
}
