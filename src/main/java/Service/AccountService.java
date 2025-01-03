package Service;



import DAO.AccountDao;
import Model.Account;

public class AccountService {

   private AccountDao accountDao;

   public AccountService(){
   accountDao = new AccountDao();
   }

   public Account createAccount(Account account) {
      validateAccount(account);

      if (accountDao.checkForExistingUser(account.getUsername())) {
          throw new IllegalArgumentException("An account with that username already exists, please try again"); 
      }
      Account createdAccount = accountDao.addAcct(account);
      return createdAccount;
  }


  public Account checkLogin(Account account) {
   validateAccount(account);
   Account loginAcct = accountDao.makeSureLoginWorks(account.getUsername(), account.getPassword());
   if (loginAcct == null) {
       throw new IllegalArgumentException("Not a correct username or password");
   }
   return loginAcct;
}



public void validateAccount(Account account) {
   String username = account.getUsername();
   String password = account.getPassword();
   if (username.isBlank()) {
       throw new IllegalArgumentException("Username cannot be blank");
   }
  if (password.length() < 4) {
       throw new IllegalArgumentException("Password must be at least 4 characters long");
      }
   }


   public Account getAccountByID(int accountId){
      return accountDao.getAccountByID(accountId);
   }
}
