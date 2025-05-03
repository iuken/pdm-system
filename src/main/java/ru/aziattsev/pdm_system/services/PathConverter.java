package ru.aziattsev.pdm_system.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

public class PathConverter {
    private static String serverPrefix;
    private static String clientPrefix;

    public static void configure(String serverPrefix, String clientPrefix) {
        PathConverter.serverPrefix = serverPrefix;
        PathConverter.clientPrefix = clientPrefix;
    }

    public static String toServerPath(String clientPath) {
        if (clientPath == null) return null;
        return clientPath.replaceFirst(clientPrefix, serverPrefix);
    }

    public static String toClientPath(String serverPath) {
        if (serverPath == null) return null;
        return serverPath.replaceFirst(serverPrefix, clientPrefix);
    }
}