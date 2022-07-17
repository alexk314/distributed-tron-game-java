package userInterface.enums;


public enum RPCMethodName {
    // Methods for hosting/joining Games in order of occurrence:
    REQUESTGAMELIST, ADDGAMETOGAMELIST, JOINGAME, SETCONNECTIONTOHOST, STARTGAME,
    // Methods for Game Logic:
    REQUESTCURRENTDIRECTION, SENDCURRENTDIRECTION, SENDCURRENTCOORDINATES, SENDPLAYERLOST;
}
