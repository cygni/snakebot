package se.cygni.snake.persistence.clientinfo;

import se.cygni.snake.api.request.ClientInfo;

public interface ClientInfoStorage {

    void storeClientInfo(ClientInfo clientInfo);

}
