package by.bondarik.drawandguess.model.network;

public enum MessageType {
    RECEIVE_PLAYER_NAME,
    LOGIN_SUCCESS,
    INVALID_DATA,
    NAME_IS_USING,
    GUESS,
    POINT,
    CHAT,
    GAME_STATE,
    CORRECT_GAME_STATE,
    SCORES,
    DISCONNECT
}
