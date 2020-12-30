package org.miraicode.kolorek;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JoinLeave extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent ev) {
        Member m = ev.getMember();
        try {
            Main.openConnection();
            Statement stmt = Main.connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(String.format("SELECT id, roles FROM roles WHERE id = '%s'", m.getId()));
            while (resultSet.next()) {
                String[] roles = resultSet.getString("roles").split(",");
                for (String role : roles) {
                    if (role.equals("")) return;
                    Role r = ev.getGuild().getRoleById(role);
                    if (r != null) {
                        ev.getGuild().addRoleToMember(m.getId(), r).queue();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent ev) {
        Member m = ev.getMember();
        if (m != null) {
            List<String> rolelist = new ArrayList<String>();
            m.getRoles().forEach(role -> {
                rolelist.add(role.getId());
            });
            String rolestring = String.join(",", rolelist);
            Main.openConnection();
            try {
                Statement stmt = Main.connection.createStatement();
                stmt.executeUpdate(String.format("INSERT INTO roles (id, roles) VALUES ('%s', '%s') ON CONFLICT (id) DO UPDATE SET roles = '%s'", m.getId(), rolestring, rolestring));
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
