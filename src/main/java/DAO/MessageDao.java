package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {

    
    public Message getByMessageID(int id) {
        String sql = "SELECT * FROM message WHERE message_id = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return traceRsBackToMsg(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  
            
        }
        return null;
    }

   
    public List<Message> obtainAllMessages() {
        String sql = "SELECT * FROM message";
        List<Message> messages = new ArrayList<>();
        Connection conn = ConnectionUtil.getConnection();
        try (
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                messages.add(traceRsBackToMsg(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return messages;
    }

    
    public List<Message> obtainMsgByAcctId(int accountId) {
        String sql = "SELECT * FROM message WHERE posted_by = ?";
        List<Message> messages = new ArrayList<>();
        Connection conn = ConnectionUtil.getConnection();
        try (
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(traceRsBackToMsg(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return messages;
    }

   
    public Message insertMsg(Message message) {
        String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        Connection conn = ConnectionUtil.getConnection();
        try (
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return new Message(generatedId, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  
        }
        return null;  
    }

   
    public Message updateMsg(Message message) {
        String sql = "UPDATE message SET message_text = ?, time_posted_epoch = ? WHERE message_id = ?";
        Connection conn = ConnectionUtil.getConnection();
        int rowsUpdated = 0;
        try (
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, message.getMessage_text());
            ps.setLong(2, message.getTime_posted_epoch());
            ps.setInt(3, message.getMessage_id());
            rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                return getByMessageID(message.getMessage_id());  
            }
        } catch (SQLException e) {
            e.printStackTrace();  
        }
       return null;
    }

   
    public boolean deleteMsg(Message message) {
        String sql = "DELETE FROM message WHERE message_id = ?";
        Connection conn = ConnectionUtil.getConnection();
        int rowsDeleted = 0;
        try (
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, message.getMessage_id());
            rowsDeleted = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();  
        }
        return rowsDeleted > 0;
    }

    
    private Message traceRsBackToMsg(ResultSet rs) throws SQLException {
        int messageId = rs.getInt("message_id");
        int postedBy = rs.getInt("posted_by");
        String messageText = rs.getString("message_text");
        long timePostedEpoch = rs.getLong("time_posted_epoch");
        return new Message(messageId, postedBy, messageText, timePostedEpoch);
    }
}
