package org.lele.sdtahzl.service;

import lombok.extern.slf4j.Slf4j;
import org.lele.sdtahzl.constant.ServerConstant;
import org.lele.sdtahzl.domain.ClientDTO;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ClientService {
    private volatile ClientDTO clientDTO;
    // 进程名称
    private static final String PROCESS_NAME = "LeagueClientUx";

    // 用于匹配端口的正则表达式
    private static final Pattern PORT_PATTERN = Pattern.compile("--app-port=([0-9]*)");

    // 用于匹配认证令牌的正则表达式
    private static final Pattern TOKEN_PATTERN = Pattern.compile("--remoting-auth-token=([\\w-]*)");

    private void initClient() throws Exception {
        try {
            // 获取操作系统类型
            String os = System.getProperty("os.name").toLowerCase();
            // 根据操作系统执行相应命令
            String command;
            if (os.contains("win")) {
                // Windows系统命令
                command = "wmic PROCESS WHERE name='" + PROCESS_NAME + ".exe' GET commandline";
            } else if (os.contains("mac")) {
                // macOS系统命令
                command = "ps -A | grep " + PROCESS_NAME;
            } else {
                throw new Exception("Unsupported OS: " + os);
            }

            // 执行命令并获取输出
            String output = executeCommand(command);
            if (output == null || output.trim().isEmpty()) {
                throw new Exception("Process command returned empty output");
            }

            // 解析端口号
            String port = extractInfo(output, PORT_PATTERN);
            // 解析认证令牌
            String token = extractInfo(output, TOKEN_PATTERN);
            String preUrl = ServerConstant.LOCAL_HOST + ":" + port;

            clientDTO = new ClientDTO();
            clientDTO.setPort(port);
            clientDTO.setAuthToken(token);
            clientDTO.setPreUrl(preUrl);
        } catch (Exception e) {
            log.error("获取服务失败{}", e.getMessage(), e);
            throw new Exception(e);
        }
    }

    /**
     * 执行系统命令并返回输出结果（包括标准输出和错误输出）
     */
    private static String executeCommand(String command) throws IOException {
        // 根据操作系统选择合适的shell执行命令，解决管道符等特殊语法问题
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("/bin/sh", "-c", command);
        }

        // 合并错误流到标准输出，确保所有输出都能被捕获
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // 读取命令输出（此时包含了标准输出和错误输出）
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            StringBuilder output = new StringBuilder();
            String line;

            // 使用非阻塞方式读取，避免缓冲区满导致进程挂起
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 等待命令执行完成并获取退出码
            int exitCode;
            try {
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 保留中断状态
                throw new IOException("命令执行被中断", e);
            }

            // 检查命令是否执行成功
            if (exitCode != 0) {
                throw new IOException("Process exited with code " + exitCode);
            }

            return output.toString();
        }
    }


    /**
     * 使用正则表达式从字符串中提取信息
     */
    private static String extractInfo(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    //可能为空
    public ClientDTO getClient() throws Exception {
        if (clientDTO == null) {
            synchronized (this) {
                if (clientDTO == null) {
                    initClient();
                }
            }
        }
        return clientDTO;
    }


}
