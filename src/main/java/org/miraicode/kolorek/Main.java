package org.miraicode.kolorek;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;

import javax.security.auth.login.LoginException;
import java.sql.*;

public class Main extends ListenerAdapter {
    public static Connection connection;
    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault(System.getenv("DISCORD_TOKEN")).enableIntents(GatewayIntent.GUILD_MEMBERS).setChunkingFilter(ChunkingFilter.ALL).build();
        jda.getPresence().setActivity(Activity.listening("Java " + System.getProperty("java.version")));
        jda.addEventListener(new Main());
        jda.addEventListener(new Kolorek());
        jda.addEventListener(new JoinLeave());
    }
    @Override
    public void onReady(ReadyEvent ev) {
        openConnection();
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS roles (id TEXT PRIMARY KEY, roles TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS colors (id TEXT PRIMARY KEY, colorrole TEXT)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SelfUser selfUser = ev.getJDA().getSelfUser();
        System.out.printf("[K0L0R3K] Zalogowano jako %s[%s] \n", selfUser.getName(), selfUser.getId());
    }
    public static void openConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
