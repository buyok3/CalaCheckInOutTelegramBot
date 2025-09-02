package objects;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import types.CheckedIn;

import java.util.ArrayList;

public class Bot extends TelegramLongPollingBot {
    private static final String checkIn = "Check-in";
    private static final String checkOut = "Check-out";

    private static ReplyKeyboardMarkup kb = initializeKeyboard();
    private static CheckedIn isCheckedIn = CheckedIn.CHECKED_OUT;
    private static String groupMessage;

    @Override
    public String getBotUsername() {
        return "@calaINOUTbot";
    }

    @Override
    public String getBotToken() {
        return "7824797587:AAFSaZrn8TyG1dQeAME7h9MoFrPPgklvHg8";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        User user = msg.getFrom();
        User group = msg.getFrom();
        String text = msg.getText();
        String chatId = msg.getChatId().toString();

        String answer;
        if (!text.equalsIgnoreCase(checkIn) && !text.equalsIgnoreCase(checkOut)) {
            answer = "Choose on of the options on the keyboard!";
        } else if (isCheckedIn == CheckedIn.CHECKED_OUT) {
            kb = getCheckOutKeyboard();
            answer = "Welcome!\nHave a nice day!";
            swapCheckedInStatus();
        } else {
            kb = getCheckInKeyboard();
            answer = "Thank you for work!\nSee you tomorrow!";
            swapCheckedInStatus();
        }

        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .replyMarkup(kb)
                    .text(answer).build();
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }

    private static void swapCheckedInStatus() {
        isCheckedIn = (isCheckedIn) == CheckedIn.CHECKED_IN ? CheckedIn.CHECKED_OUT : CheckedIn.CHECKED_IN;
    }

    private static ReplyKeyboardMarkup initializeKeyboard() {
        ReplyKeyboardMarkup kb = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        row.add("Check-in");
        row.add("Check-out");
        keyboard.add(row);
        kb.setKeyboard(keyboard);
        kb.setResizeKeyboard(Boolean.TRUE);
        kb.setOneTimeKeyboard(Boolean.FALSE);
        kb.setInputFieldPlaceholder("Choose option");
        return kb;
    }

    private static ReplyKeyboardMarkup getCheckOutKeyboard() {
        ReplyKeyboardMarkup kb = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        row.add("Check-out");
        keyboard.add(row);
        kb.setKeyboard(keyboard);
        kb.setResizeKeyboard(Boolean.TRUE);
        kb.setOneTimeKeyboard(Boolean.FALSE);
        return kb;
    }

    private static ReplyKeyboardMarkup getCheckInKeyboard() {
        ReplyKeyboardMarkup kb = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        row.add("Check-in");
        keyboard.add(row);
        kb.setKeyboard(keyboard);
        kb.setResizeKeyboard(Boolean.TRUE);
        kb.setOneTimeKeyboard(Boolean.FALSE);
        return kb;
    }
}