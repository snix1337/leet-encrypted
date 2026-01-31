package funny.leet.core.client;

import funny.leet.core.user.UserInfo;
import obf.uscate.classes.Profile;
import funny.leet.common.util.other.StringUtil;

import java.io.File;

public record ClientInfo(String clientName, String userName, String role, File clientDir, File filesDir, File configsDir) implements ClientInfoProvider {

    @Override
    public String getFullInfo() {
        return String.format("Welcome to leet! Client: %s Version: %s Role: %s", clientName, UserInfo.getUsername(), StringUtil.getUserRole());
    }
}