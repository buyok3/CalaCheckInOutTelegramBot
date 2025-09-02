package objects;

import java.util.ArrayList;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import types.CheckedIn;


public class Bot extends TelegramLongPollingBot {
    private static final String checkIn = "Check-in";
    private static final String checkOut = "Check-out";
    private static final String groupId = "-4958090717";
    private static final Double calaLatitude = 28.083023;
    private static final Double calaLongitude = -16.735304;

    private static ReplyKeyboardMarkup kb = initializeKeyboard();
    private static CheckedIn isCheckedIn = CheckedIn.CHECKED_OUT;
    private static String groupAnswer;
    private static String personalAnswer;
    private static Double longitude;
    private static Double latitude;

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
        String chatId = msg.getChatId().toString();

        if (!update.getMessage().isCommand() || update.getMessage().getText().equalsIgnoreCase("/start")) {
            String text = msg.getText();

            if(text != null) {
                if (!text.equalsIgnoreCase(checkIn) && !text.equalsIgnoreCase(checkOut)) {
                    personalAnswer = "Choose on of the options on the keyboard!";
                } else if (isCheckedIn == CheckedIn.CHECKED_OUT) {
                    kb = getCheckOutKeyboard();
                    personalAnswer = "Welcome!\nHave a nice day!";
                    swapCheckedInStatus();
                } else {
                    kb = getCheckInKeyboard();
                    personalAnswer = "Thank you for work!\nSee you tomorrow!";
                    swapCheckedInStatus();
                }
            }
            // If it's not text (location)
            else {
                Location userLocation = msg.getLocation();
                latitude = userLocation.getLatitude();
                longitude = userLocation.getLongitude();
                Double distance = calculateDistance(calaLatitude, calaLongitude, latitude, longitude) * 1000;
                // Check if near restaurant
                if (distance <= 50) {
                    if (isCheckedIn == CheckedIn.CHECKED_OUT) {
                        kb = getCheckOutKeyboard();
                        personalAnswer = "Welcome to CALA!\nHave a nice day!";
                        // check if person is on time
                        groupAnswer = "";
                        swapCheckedInStatus();
                    } else {
                        kb = getCheckInKeyboard();
                        personalAnswer = "Thank you for work!\nSee you tomorrow!";
                        swapCheckedInStatus();
                    }
                }
                else {
                    personalAnswer = "Get closer to restaurant to " + ((isCheckedIn) == CheckedIn.CHECKED_IN ? "check-in!" : "check-out!");
                }
                // If close -> check-in/out
                // Send message to group
                // If not -> send personal message to get closer
            }
        }

        // Send personal message
        try {
            SendMessage personalMessage = SendMessage.builder()
                    .chatId(chatId)
                    .replyMarkup(kb)
                    .text(personalAnswer).build();
            execute(personalMessage);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }

        // Send group message
        try {
            SendMessage groupMessage = SendMessage.builder()
                    .chatId(groupId)
                    .text("SEX").build();
            execute(groupMessage);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }

    double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double lon1Rad = Math.toRadians(lon1);
        double lon2Rad = Math.toRadians(lon2);

        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);
        double distance = Math.sqrt(x * x + y * y) * 6371;

        return distance;
    }

    private static void swapCheckedInStatus() {
        isCheckedIn = (isCheckedIn) == CheckedIn.CHECKED_IN ? CheckedIn.CHECKED_OUT : CheckedIn.CHECKED_IN;
    }

    private static ReplyKeyboardMarkup initializeKeyboard() {
        ReplyKeyboardMarkup kb = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        row.add(KeyboardButton.builder().requestLocation(true).text("Check-in").build());
        row.add(KeyboardButton.builder().requestLocation(true).text("Check-out").build());
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
        row.add(KeyboardButton.builder().requestLocation(true).text("Check-out").build());
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
        row.add(KeyboardButton.builder().requestLocation(true).text("Check-in").build());
        keyboard.add(row);
        kb.setKeyboard(keyboard);
        kb.setResizeKeyboard(Boolean.TRUE);
        kb.setOneTimeKeyboard(Boolean.FALSE);
        return kb;
    }
}