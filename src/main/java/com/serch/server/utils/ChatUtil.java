package com.serch.server.utils;

import com.serch.server.enums.chat.MessageType;

public class ChatUtil {
    private static String prepareRoomMessage(boolean isSentByCurrentUser, String file, String roommateName) {
        if(isSentByCurrentUser) {
            return "You: %s".formatted(file);
        } else {
            return "%s: %s".formatted(roommateName, file);
        }
    }

    public static String formatRoomMessage(String message, MessageType type, boolean sentByUser, String roommate) {
        return switch (type) {
            case IMAGE -> prepareRoomMessage(sentByUser, "📷 Photo", roommate);
            case VIDEO -> prepareRoomMessage(sentByUser, "🎥 Video", roommate);
            case FILE -> prepareRoomMessage(sentByUser, "🗒️ File", roommate);
            case VOICE -> prepareRoomMessage(sentByUser, "Voice", roommate);
            case AUDIO -> prepareRoomMessage(sentByUser, "🎵 Audio", roommate);
            default -> message;
        };
    }

    public static String formatRepliedMessage(String message, String duration, MessageType type) {
        return switch (type) {
            case IMAGE -> "📷 Photo";
            case VIDEO -> "🎥 Video " + duration;
            case FILE -> "🗒️ File";
            case VOICE -> "Voice " + duration;
            case AUDIO -> "🎵 Audio " + duration;
            default -> message;
        };
    }
}