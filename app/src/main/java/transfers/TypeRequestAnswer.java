package transfers;

public interface TypeRequestAnswer {
    String ERROR = "ERROR";
    String AUTHORIZATION_DONE = "AUTHORIZATION_DONE";
    String AUTHENTICATION_DONE = "AUTHENTICATION_DONE";
    String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";
    String USER_NOT_EXIST = "USER_NOT_EXIST";
    String WRONG_PASSWORD = "WRONG_PASSWORD";
    String REGISTRATION_DONE = "REGISTRATION_DONE";
    String USER_ALREADY_EXIST = "USER_ALREADY_EXIST";
    String BAD_LOGIN = "BAD_LOGIN";
    String BAD_PASSWORD = "BAD_PASSWORD";
    String UPDATE_TOKEN = "UPDATE_TOKEN";
    String NEED_LOGIN = "NEED_LOGIN";
    String LOGOUT = "LOGOUT";
    String CREATE_GROUP = "CREATE_GROUP";
    String GROUP_ALREADY_EXIST = "GROUP_ALREADY_EXIST";
    String GET_MY_GROUPS = "GET_MY_GROUPS";
    String GROUP_ADDED = "GROUP_ADDED";
    String LEAVE_GROUP = "LEAVE_GROUP";
    String UPDATE_INFO = "UPDATE_INFO";
    String YOU_ONLY_MODERATOR = "YOU_ONLY_MODERATOR";
    String GET_GROUP_PERSONS = "GET_GROUP_PERSONS";
    String NO_PERMISSION = "NO_PERMISSION";
    String CREATE_PERSON = "CREATE_PERSON";
    String UPDATE_PERSON = "UPDATE_PERSON";
    String INVITE_USER_TO_PERSON = "INVITE_USER_TO_PERSON";
    String ACCEPT_INVITE = "ACCEPT_INVITE";
    String REJECT_INVITE = "REJECT_INVITE";
    String UNTIE_USER = "UNTIE_USER";
    String GET_INVITES = "GET_INVITES";
    String GET_PERSON_INVITES = "GET_PERSON_INVITES";
    String GET_TIMETABLE = "GET_TIMETABLE";
    String DELETE_TIMETABLE = "DELETE_TIMETABLE";
    String DELETE_PERSON = "DELETE_PERSON";
    String DELETE_GROUP = "DELETE_GROUP";
}
