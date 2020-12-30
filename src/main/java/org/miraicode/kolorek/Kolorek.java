package org.miraicode.kolorek;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

public class Kolorek extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent ev) {
        if (!ev.getMessage().getContentRaw().toLowerCase().startsWith("->kolorek")) return;
        String[] args = ev.getMessage().getContentRaw().trim().split(" +");
        if (args.length < 3) {
            ev.getChannel().sendMessage("Poprawne użycie komendy: `->kolorek Nazwa kolorku #RGBHEX`").queue();
            return;
        }
        Color color = Color.decode(args[args.length-1]);
        String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length-1));
        try {
            Main.openConnection();
            Statement stmt = Main.connection.createStatement();
            Member m = ev.getMember();
            if (m == null) return;
            ResultSet resultSet = stmt.executeQuery("SELECT id, colorrole FROM colors WHERE id='" + m.getId() + "'");
            String roleid = "";
            while (resultSet.next()) {
                roleid = resultSet.getString("colorrole");
            }
            Role r = ev.getGuild().getRoleById(roleid);
            if (r == null) {
                Role zewnetrzny = ev.getGuild().getRoleById(System.getenv("DEFAULT_ROLE"));
                r = ev.getGuild().createRole().setName(name).setColor(color).complete();
                if (zewnetrzny != null) {
                    ev.getGuild().modifyRolePositions().selectPosition(r).moveTo(zewnetrzny.getPosition()+1).queue();
                }
                stmt.executeUpdate(String.format("INSERT INTO colors (id, colorrole) VALUES ('%s', '%s') ON CONFLICT (id) DO UPDATE SET colorrole = '%s'", m.getId(), r.getId(), r.getId()));
            } else {
                r.getManager().setColor(color).setName(name).complete();
            }
            ev.getGuild().addRoleToMember(m.getId(), r).complete();
            ev.getChannel().sendMessage("Gotowe!").queue();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
