package Service;


import DAO.MessageDao;

import Model.Message;


import java.util.List;

public class MessageService {
    private final MessageDao messageDao;
    private final AccountService accountService;;

    public MessageService(AccountService accountService, MessageDao messageDao) {
        this.accountService = accountService;
        this.messageDao = messageDao;
    }



    private boolean validAccount(int accountId){
       return accountService.getAccountByID(accountId) != null;
    }

    
    public List<Message> getAllMessages() {
        return messageDao.obtainAllMessages();
    }

    
    public Message getMessageById(int id) {
        Message msg = messageDao.getByMessageID(id);
        if(msg == null){
            throw new IllegalArgumentException("A message could not be found with that ID " + id);
        }
        return msg;
    }

    
    public List<Message> getMessagesByAccountId(int accountId) {
      return messageDao.obtainMsgByAcctId(accountId);
    }

    
    public Message createMessage(Message message) {
        checkMessageCriteria(message.getMessage_text());
        if(!validAccount(message.getPosted_by())){
            throw new IllegalArgumentException("We're sorry but that was an Invalid account ID " + message.getPosted_by());
        }
        return messageDao.insertMsg(message);
    }


    
    public Message updateMessage(Message message) {
        checkMessageCriteria(message.getMessage_text());
        Message existMessage = messageDao.getByMessageID(message.getMessage_id());
        if(existMessage == null){
            throw new IllegalArgumentException("Sorry but a message could not be found with that ID " + message.getMessage_id());
        }
        existMessage.setMessage_text(message.getMessage_text());
        return messageDao.updateMsg((existMessage));
    }

    
    public void deleteMessage(Message message) {
      boolean delete = messageDao.deleteMsg(message);
      System.out.println(delete);
    }

    
    private void checkMessageCriteria(String txt){
        if(txt.length() > 255 || txt.isEmpty()){
            throw new IllegalArgumentException("Message cannot be empty nor greater than 255 characters");
        }
    }
    
}
