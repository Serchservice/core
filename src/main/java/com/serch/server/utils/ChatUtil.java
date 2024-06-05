package com.serch.server.utils;

import com.serch.server.enums.chat.MessageType;

import java.util.regex.Pattern;

public class ChatUtil {
    private static String prepareRoomMessage(boolean isSentByCurrentUser, String file, String roommateName) {
        if(isSentByCurrentUser) {
            return "You: %s".formatted(file);
        } else {
            return "%s: %s".formatted(roommateName, file);
        }
    }

    private static String prepareRoomMessage(String message, MessageType type) {
        String[] lines = message.split("\n");
        if(type == MessageType.EMOJI) {
            return lines[0];
        } else {
            return lines[0] + (lines.length > 1 ? "..." : "");
        }
    }

    public static String formatRoomMessage(String message, MessageType type, boolean sentByUser, String roommate) {
        return switch (type) {
            case IMAGE -> prepareRoomMessage(sentByUser, "📷 Photo", roommate);
            case VIDEO -> prepareRoomMessage(sentByUser, "🎥 Video", roommate);
            case FILE -> prepareRoomMessage(sentByUser, "🗒️ File", roommate);
            case VOICE -> prepareRoomMessage(sentByUser, "Voice", roommate);
            case AUDIO -> prepareRoomMessage(sentByUser, "🎵 Audio", roommate);
            case TEXT -> prepareRoomMessage(message, MessageType.TEXT);
            case EMOJI -> prepareRoomMessage(message, MessageType.EMOJI);
        };
    }

    public static String formatRepliedMessage(String message, String duration, MessageType type) {
        return switch (type) {
            case IMAGE -> "📷 Photo";
            case VIDEO -> "🎥 Video " + duration;
            case FILE -> "🗒️ File";
            case VOICE -> "Voice " + duration;
            case AUDIO -> "🎵 Audio " + duration;
            case TEXT -> prepareRoomMessage(message, MessageType.TEXT);
            case EMOJI -> prepareRoomMessage(message, MessageType.EMOJI);
        };
    }

    private static int countEmojis(Pattern pattern, String text) {
        return (int) pattern.matcher(text).results().count();
    }

    public static boolean hasEmojis(String text) {
        Pattern pattern = Pattern.compile("[\\x{1F600}-\\x{1F64F}\\x{1F300}-\\x{1F5FF}\\x{1F680}-\\x{1F6FF}"
                        + "\\x{2600}-\\x{26FF}\\x{2700}-\\x{27BF}\\x{1F900}-\\x{1F9FF}\\x{1F1E0}-\\x{1F1FF}]+",
                Pattern.UNICODE_CHARACTER_CLASS);
        return pattern.matcher(text).find();
    }

    public static boolean containsOnlyEmojis(String text) {
        String emojiPattern = "[^\\x00-\\x7F]|(?:[.]{3})|[\\uD83C-\\uD83E][\\uDDE0-\\uDDFF]|[\\uD83C-\\uD83E]"
                + "[\\uDC00-\\uDFFF]|[\\uD83F-\\uD87F][\\uDC00-\\uDFFF]|[\\u2600-\\u26FF]|[\\u2700-\\u27BF]";
        return text.replaceAll(emojiPattern, "").isEmpty();
    }

    public static boolean containsOnlyOneEmoji(String text) {
        Pattern emojiPattern = Pattern.compile("[^\\x00-\\x7F]|(?:[.]{3})|[\\uD83C-\\uD83E][\\uDDE0-\\uDDFF]"
                + "|[\\uD83C-\\uD83E][\\uDC00-\\uDFFF]|[\\uD83F-\\uD87F][\\uDC00-\\uDFFF]|[\\u2600-\\u26FF]"
                + "|[\\u2700-\\u27BF]", Pattern.UNICODE_CHARACTER_CLASS);
        String textWithoutEmojis = text.replaceAll(emojiPattern.pattern(), "");
        return textWithoutEmojis.isEmpty() && countEmojis(emojiPattern, text) == 1;
    }
}