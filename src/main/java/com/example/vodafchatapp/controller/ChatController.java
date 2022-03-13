package com.example.vodafchatapp.controller;

import com.example.vodafchatapp.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class  ChatController {

    @GetMapping("/statistics/{user}")
    @ResponseBody
    public String getStats(@PathVariable String user) throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("db/swilam-statistics.txt"));
        return bufferedReader.lines().collect(Collectors.joining());
    }

    @MessageMapping("/chat.register")
    @SendTo("/topic/public")
    public ChatMessage register(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) throws IOException {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader("db/auth.txt"));
            String line;
            boolean usernameExists = false;
            while((line = bufferedReader.readLine()) != null) {
                if (line.split(":")[0].equals(chatMessage.getSender()) && line.split(":")[1].equals(getHash(chatMessage.getPassword()))) {
                    usernameExists = true;
                    break;
                }
            }
            if (usernameExists) {
                headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
                // create file here
                File myObj = new File("db/" + chatMessage.getSender() + "-chat.txt");
                myObj.createNewFile();
                chatMessage.setAuth(true);
            } else {
                headerAccessor.getSessionAttributes().clear();
                chatMessage.setAuth(false);
            }
        } catch (FileNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return chatMessage;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) throws IOException {
        writeToFile(chatMessage.getContent(), "db/" + chatMessage.getSender() + "-chat.txt");
        if (chatMessage.getContent().equalsIgnoreCase("bye bye")) {
            CountAndSaveWordsInUserFile(chatMessage.getSender());
            return endUserSession(chatMessage, headerAccessor);
        } else {
            chatMessage.setAuth(true);
            return chatMessage;
        }
    }

    /**
     * Ends User Session
     *
     * @param chatMessage
     * @param headerAccessor
     * @return
     */
    private ChatMessage endUserSession(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        chatMessage.setAuth(false);
        chatMessage.setMessageType(ChatMessage.MessageType.LEAVE);
        headerAccessor.getSessionAttributes().remove("username", chatMessage.getSender());
        return chatMessage;
    }

    /**
     * Write message to a speific text file
     *
     * @param message
     * @param filePath
     * @return boolean
     * @throws IOException
     */
    private boolean writeToFile(String message, String filePath) throws IOException {
        try {
            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(message);
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public String getHash(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(password.getBytes());
        return new String(messageDigest.digest());
    }

    public void CountAndSaveWordsInUserFile(String user) throws IOException {
        String line, word = "";
        int count = 0, maxCount = 0;
        ArrayList<String> words = new ArrayList<String>();

        //Opens file in read mode
        FileReader file = new FileReader("db/" + user + "-chat.txt");
        BufferedReader br = new BufferedReader(file);

        //Reads each line
        while((line = br.readLine()) != null) {
            String string[] = line.toLowerCase().split("([,.\\s]+) ");
            //Adding all words generated in previous step into words
            for(int i = 0; i < Arrays.asList(string).size(); i++) {
                words.addAll(Arrays.asList(Arrays.asList(string).get(i).split(" ")));
            }
        }

        File myObj = new File("db/" + user + "-statistics.txt");
        myObj.createNewFile();

        writeToFile("********** START STATS ***********", "db/" + user + "-statistics.txt");

        ArrayList<String> calculatedWordsList = new ArrayList<String>();

        // Determine the most repeated word in a file
        for(int i = 0; i < words.size(); i++) {
            count = 1;
            if (!calculatedWordsList.contains(words.get(i))) {
                calculatedWordsList.add(words.get(i));
                //Count each word in the file and store it in variable count
                for(int j = i + 1; j < words.size(); j++) {
                    if(words.get(i).equals(words.get(j))) {
                        count++;
                    }
                }
                writeToFile("=> Word " + words.get(i) + " repeated " + count + " time(s)", "db/" + user + "-statistics.txt");
            }
        }

        writeToFile("********** END STATS ***********", "db/" + user + "-statistics.txt");
        br.close();
    }
}
