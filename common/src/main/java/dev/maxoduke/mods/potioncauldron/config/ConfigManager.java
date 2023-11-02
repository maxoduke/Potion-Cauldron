package dev.maxoduke.mods.potioncauldron.config;

public class ConfigManager
{
    private ServerConfig serverConfig;
    private ClientConfig clientConfig;

    public ServerConfig serverConfig() { return serverConfig; }

    public void setServerConfig(ServerConfig serverConfig) { this.serverConfig = serverConfig; }

    public boolean updateServerConfig(ServerConfig serverConfig)
    {
        if (this.serverConfig == null)
            return false;

        this.serverConfig = serverConfig;
        return true;
    }

    public IConfig clientOrServerConfig() { return clientConfig != null ? clientConfig : serverConfig; }

    public void setClientConfig(ClientConfig clientConfig) { this.clientConfig = clientConfig; }
}
