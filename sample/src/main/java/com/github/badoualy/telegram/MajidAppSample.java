package com.github.badoualy.telegram;

import com.github.badoualy.telegram.api.Kotlogram;
import com.github.badoualy.telegram.api.TelegramApp;
import com.github.badoualy.telegram.api.TelegramClient;
import com.github.badoualy.telegram.tl.api.*;
import com.github.badoualy.telegram.tl.api.auth.TLAuthorization;
import com.github.badoualy.telegram.tl.api.auth.TLSentCode;
import com.github.badoualy.telegram.tl.api.messages.TLAbsDialogs;
import com.github.badoualy.telegram.tl.exception.RpcErrorException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

@SuppressWarnings("ALL")
public class MajidAppSample {

    public static final int API_ID = 99999;
    public static final String API_HASH = "API_HASH";
    public static final String MODEL = "Model";
    public static final String SYSTEM_VERSION = "SysVer";
    public static final String APP_VERSION = "AppVer";
    public static final String LANG_CODE = "en";

    public static TelegramApp application = new TelegramApp(API_ID, API_HASH, MODEL, SYSTEM_VERSION, APP_VERSION, LANG_CODE);
    public static final String PHONE_NUMBER = "+989124150687"; // TODO: you need to fill this

    public static final File AUTH_KEY_FILE = new File("sample" + File.separator + "auth.key");
    public static final File NEAREST_DC_FILE = new File("sample" + File.separator + "dc.save");
    public static final File SALT_FILE = new File("sample" + File.separator + "salt.save");

    public static void main(String[] args) throws InterruptedException, IOException {
        Integer sleep = null;
        if (args.length > 0) sleep = Integer.parseInt(args[0]);

        Files.createDirectories(Paths.get("sample"));
        // Activate debug log or not, false by default
        // Kotlogram.setDebugLogEnabled(true);

        // Replace the following constants with your app's information
        // This informations are used in initConnection and sendCode rpc methods
        TelegramApp app = new TelegramApp(API_ID, API_HASH, MODEL, SYSTEM_VERSION, APP_VERSION, LANG_CODE);

        // This is a synchronous client, that will block until the response arrive (or until timeout)
        // A client which return an Observable<T> where T is the response type will be available soon
        final FileApiStorage storage = new FileApiStorage();
        TelegramClient client = Kotlogram.getDefaultClient(app, storage);

        // You can start making requests
        try {
            if (!storage.isCodeReceived()) {
                // Send code to account
                TLSentCode sentCode = client.authSendCode(false, PHONE_NUMBER, true);
                System.out.println("Authentication code: ");
                String code = new Scanner(System.in).nextLine();
                TLAuthorization authorization = client.authSignIn(PHONE_NUMBER, sentCode.getPhoneCodeHash(), code);
                storage.setCodeReceived();
                // Auth with the received code
                TLUser self = authorization.getUser().getAsUser();
                System.out.println("You are now signed in as " + self.getFirstName() + " " + self.getLastName());
            }

            // Start making cool stuff!
            // Get a list of 10 most recent conversations
            TLAbsDialogs dialogs = client.messagesGetDialogs(0, 0, new TLInputPeerEmpty(), 10);
            for (TLAbsMessage message : dialogs.getMessages()) {
                if (sleep != null)
                    Thread.sleep(sleep);
                if (message instanceof TLMessage) {
                    System.out.println("Found message " + ((TLMessage) message).getMessage());
                } else {
                    System.out.println("Found a service message or empty message");
                }
            }

            // Take the first user in the list, and send a message to him
            // It'll probably be Telegram bot, since he'll send you the code
            TLUser randomUser = dialogs.getUsers().get(0).getAsUser();
            TLInputPeerUser inputUser = new TLInputPeerUser(randomUser.getId(), randomUser.getAccessHash());
            int randomId = new Random(System.currentTimeMillis()).nextInt();
//            client.messagesSendMessage(inputUser, "Kotlogram is awesome!", randomId);
//            System.out.println("Message sent");
        } catch (RpcErrorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.close(); // Important, do not forget this, or your process won't finish
        }
        System.out.println("------------------------- GOOD BYE");
        System.exit(0);
    }
}
