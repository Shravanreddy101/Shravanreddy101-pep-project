package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;



import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import DAO.MessageDao;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */

     private static final ObjectMapper mapper = new ObjectMapper();
     private final AccountService accountservice;
     private final MessageService messageService;
     private final MessageDao messageDao = new MessageDao(); 


     public SocialMediaController(){
        this.accountservice =  new AccountService();
        this.messageService = new MessageService(accountservice, messageDao);
     
     }

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerNewUser);
        app.post("/login", this::userLogin);
        app.post("/messages", this::createNewMsg);
        app.get("/messages", this::retrieveAllMessages);
        app.get("/messages/{message_id}", this::getMessageById);
        app.delete("/messages/{message_id}", this::deleteMsgById);
        app.patch("/messages/{message_id}", this::updateMsgById);
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountId);

        return app;
    }

    
    private void registerNewUser(Context ctx) throws JsonProcessingException  {
        Account account = mapper.readValue(ctx.body(), Account.class);
        try {
            Account registeredAccount = accountservice.createAccount(account);
            ctx.json(registeredAccount);
        } catch(IllegalArgumentException e){
            ctx.status(400);
          }
    }

    private void userLogin(Context ctx) throws JsonProcessingException {
        Account account = mapper.readValue(ctx.body(), Account.class);
        try {
            Account loginAcct = accountservice.checkLogin(account);
                ctx.json(loginAcct);
        } catch (IllegalArgumentException e) {
            ctx.status(401);
        }
    }


    private void createNewMsg(Context ctx) throws JsonProcessingException {
       Message msg = mapper.readValue(ctx.body(), Message.class);
       try {
       Message message = messageService.createMessage(msg);
        ctx.status(200).json(message);
        return;
       } catch (IllegalArgumentException e) {
       ctx.status(400).result("");  
      return;
       } 
    }

    private void retrieveAllMessages(Context ctx) {
        List<Message> msg = messageService.getAllMessages();
        ctx.json(msg);
    }

    private void getMessageById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("message_id"));
            Message message = messageService.getMessageById(id);
                ctx.json(message);
             
        } catch (IllegalArgumentException e) {
            ctx.status(200); 
            ctx.result(""); 
        }
    }   


    private void deleteMsgById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("message_id"));
            Message message = messageService.getMessageById(id);
                messageService.deleteMessage(message);
                ctx.status(200);
                ctx.json(message);
             
        } catch(IllegalArgumentException e){
            ctx.status(200);
        }
    }


    private void updateMsgById(Context ctx) throws JsonProcessingException {
       
        Message mappedMessage = mapper.readValue(ctx.body(), Message.class);
        try {
            int id = Integer.parseInt(ctx.pathParam("message_id"));
            mappedMessage.setMessage_id(id);
            Message messageUpdated = messageService.updateMessage(mappedMessage);
            ctx.json(messageUpdated);
        } catch (IllegalArgumentException e) {
            ctx.status(400); 
        }
    }

    private void getMessagesByAccountId(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));
            List<Message> messages = messageService.getMessagesByAccountId(accountId);
            
                ctx.json(messages); 
                ctx.status(200);
            
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}