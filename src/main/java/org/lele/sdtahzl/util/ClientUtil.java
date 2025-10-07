package org.lele.sdtahzl.util;

import lombok.extern.slf4j.Slf4j;
import org.lele.sdtahzl.constant.ServerConstant;
import org.lele.sdtahzl.domain.ClientDTO;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ClientUtil {
    private volatile static ClientDTO clientDTO;
    private static final String PROCESS_NAME = "LeagueClientUx";
    private static final Pattern PORT_PATTERN = Pattern.compile("--app-port=([0-9]*)");
    private static final Pattern TOKEN_PATTERN = Pattern.compile("--remoting-auth-token=([\\w-]*)");

    private static void initClient() throws Exception {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;

            if (os.contains("win")) {
                // ✅ 改为 PowerShell 查询方式，兼容新版 Windows
                command =
                        "powershell -Command \"Get-CimInstance Win32_Process -Filter \\\"Name='"
                                + PROCESS_NAME + ".exe'\\\" | Select-Object -ExpandProperty CommandLine\"";
            } else if (os.contains("mac")) {
                command = "ps -A | grep " + PROCESS_NAME;
            } else {
                throw new Exception("Unsupported OS: " + os);
            }

            String output = executeCommand(command);
            if (output == null || output.trim().isEmpty()) {
                throw new Exception("LeagueClientUx.exe not found or command returned empty output");
            }

            log.info("Raw process output: {}", output);

            String port = extractInfo(output, PORT_PATTERN);
            String token = extractInfo(output, TOKEN_PATTERN);

            if (StringUtils.isEmpty(port) || StringUtils.isEmpty(token)) {
                throw new Exception("Failed to parse port/token from process output");
            }

            String preUrl = ServerConstant.LOCAL_HOST + ":" + port;
            clientDTO = new ClientDTO();
            clientDTO.setPort(port);
            clientDTO.setAuthToken(token);
            clientDTO.setPreUrl(preUrl);

            log.info("✅ Client initialized: port={}, token={}", port, token);

        } catch (Exception e) {
            log.error("❌ 获取客户端信息失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    private static String executeCommand(String command) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // ✅ PowerShell 已经是完整命令，不需要再嵌 cmd /c
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("/bin/sh", "-c", command);
        }
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }

    private static String extractInfo(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static ClientDTO getClient() throws Exception {
        if (clientDTO == null) {
            synchronized (ClientUtil.class) {
                if (clientDTO == null) {
                    initClient();
                }
            }
        }
        return clientDTO;
    }
}
