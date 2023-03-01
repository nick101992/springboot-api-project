package com.user.api.demo.controller;

import com.user.api.demo.enumerations.AccessResult;
import com.user.api.demo.model.*;
import com.user.api.demo.service.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class AuthController {

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    AccessAttemptService accessAttemptService;

    @Autowired
    TokenService tokenService;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${password.attempts}")
    private int passwordAttempts;

    // Login
    // http://localhost:8080/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserRoleRequest userRoleRequest) {

        // Generate a new transaction ID and put it in the MDC
        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);
        loggerService.logRequest(request, userRoleRequest);

        // Verify the presence of user and role in the database
        Optional<User> findUser = userService.findByUsername(userRoleRequest.getUsername());
        Optional<Roles> findRole = roleService.findByrolesName(userRoleRequest.getRoleName());


        if (findUser.isPresent() && findRole.isPresent()) {
            //Select user object from my Optional<User>
            User user = findUser.get();

            //New AccessAttempt Object with timestamp and user
            AccessAttempt accessAttempt = new AccessAttempt();
            accessAttempt.setTimestamp(LocalDateTime.now());
            accessAttempt.setUser(user);

            //Recupero numero tentativi e stato utente nel database
            int numAttempts = user.getNumAttempts();
            Boolean userActive = user.getUserActive();

            //Recupero id del ruolo e dell'utente trovato e verifico se c'è una relazione
            Integer user_id = user.getId();
            Integer role_id = findRole.get().getId();
            Optional<UserRole> userRolePresence = userRoleService.findByUserIdAndRoleId(user_id, role_id);

            //Recupero pass e salt key dell'utente trovato
            String pass = findUser.get().getPassword();
            String saltKey = user.getSaltKey();


            if(userActive){
                if (userRolePresence.isPresent()) {
					/*Confronto la password inserita nel body della request aggiungendogli la salt key con la pass
					 criptata presente nel database
					 */
                    if(BCrypt.checkpw(userRoleRequest.getPassword()+saltKey,pass)){
                        loggerService.logSecurity(userRoleRequest.getUsername(),"Tentativo di accesso riuscito");

                        //Setto il valore del risultato di accesso e lo salvo nel database
                        accessAttempt.setAccessResult(AccessResult.Right_Password);
                        accessAttemptService.save(accessAttempt);
                        applicationContext.publishEvent(new ActionGeneratedEvent
                                (this, user.getUsername(), "Success Login", true, "Login"));

                        //Resetto il numero dei tentativi a 0 e aggiorno il valore nel database
                        numAttempts = 0;
                        userService.updateUserAttempts(user,numAttempts);

                        String tokenValue = tokenService.generateToken();
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis() + 600000);

                        Token token = new Token();
                        token.setValue(tokenValue);
                        token.setUser(user);
                        token.setExpirationDate(timestamp);

                        tokenService.save(token);

                        return new ResponseEntity<>(new LoginResponse(tokenValue, null, user_id), HttpStatus.OK);

                    }else {
                        loggerService.logSecurity(userRoleRequest.getUsername(),"Tentativo di accesso non riuscito");
                        //Setto il valore del risultato di accesso e lo salvo nel database
                        accessAttempt.setAccessResult(AccessResult.Wrong_Password);
                        accessAttemptService.save(accessAttempt);

                        //Aggiungo un tentativo e aggiorno il valore nel database
                        numAttempts+=1;
                        userService.updateUserAttempts(user,numAttempts);

                        //Confronto il numero dei tentativi dell'utente con quello massimo
                        if (numAttempts == passwordAttempts) {
                            loggerService.logSecurity(userRoleRequest.getUsername(),"Utente Bloccato");

                            //Setto il valore del risultato di accesso e lo salvo nel database
                            accessAttempt.setAccessResult(AccessResult.Blocked_User);
                            accessAttemptService.save(accessAttempt);

                            //Disabilito l'utente e salvo lo stato nel database
                            userService.updateUserStatus(user, false);

                            MDC.remove("transactionId");
                            return new ResponseEntity<>(new LoginResponse(null, "Numero tentativi superati - Utente Bloccato", null),
                                    HttpStatus.OK);

                        }
                        int numRemainAttempts = passwordAttempts - numAttempts;
                        MDC.remove("transactionId");
                        return new ResponseEntity<>(new LoginResponse(null, "Credenziali sbagliate ti rimangono "+ numRemainAttempts +" tentativi", null),
                                HttpStatus.OK);
                    }
                }
                loggerService.logSecurity(userRoleRequest.getUsername(),"Questo utente non ha il ruolo per entrare");
                //Setto il valore del risultato di accesso e lo salvo nel database
                accessAttempt.setAccessResult(AccessResult.No_Role);
                accessAttemptService.save(accessAttempt);
                MDC.remove("transactionId");
                return new ResponseEntity<>(new LoginResponse(null, "Non hai il ruolo per entrare", null),
                        HttpStatus.OK);
            }
            loggerService.logSecurity(userRoleRequest.getUsername(),"Utente Disabilitato");
            //Setto il valore del risultato di accesso e lo salvo nel database
            accessAttempt.setAccessResult(AccessResult.User_Disabled);
            accessAttemptService.save(accessAttempt);
            MDC.remove("transactionId");
            return new ResponseEntity<>(new LoginResponse(null, "L'utente è disabilitato", null),
                    HttpStatus.OK);
        }
        MDC.remove("transactionId");
        return new ResponseEntity<>(new LoginResponse(null, "L'utente o il ruolo inserito non esistono", null),
                HttpStatus.OK);
    }


    @PostMapping("/check-token")
    public ResponseEntity<Map<String, Boolean>> checkToken(@RequestBody TokenRequest token) {
        String value = token.getValue();
        Integer user = token.getUser();
        Timestamp expirationDate = token.getExpirationDate();

        // Verifica che token e userId non siano null
        if (value == null || user == null) {
            Map<String, Boolean> errorResponse = new HashMap<>();
            errorResponse.put("isAuthenticated", false);
            errorResponse.put("error", true);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        User findUser = userService.findByTokenAndId(value, user);
        Token findToken = tokenService.findByValue(value);
        Timestamp findExpirationDate = findToken.getExpirationDate();
        Map<String, Boolean> response = new HashMap<>();

        System.out.println(findUser);
        System.out.println(findToken);
        System.out.println(expirationDate);
        System.out.println(findExpirationDate);


        if (findUser == null || findExpirationDate == null ) {
            response.put("isAuthenticated", false);
        }

        if (findExpirationDate.compareTo(expirationDate)>0){
            response.put("isAuthenticated", true);
        } else{
            response.put("isAuthenticated", false);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
