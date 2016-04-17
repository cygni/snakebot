package se.cygni.snake.game;

import se.cygni.snake.player.IPlayer;
import se.cygni.snake.player.RemotePlayer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerManager {

    private Set<IPlayer> players = new HashSet<>();
    final Object mutex;     // Object on which to synchronize

    public PlayerManager() {
        mutex = this;
    }

    public void clear() {
        synchronized (mutex) {
            players.clear();
        }
    }

    public void remove(IPlayer player) {
        synchronized (mutex) {
            players.remove(player);
        }
    }

    public void add(IPlayer player) {
        synchronized (mutex) {
            players.add(player);
        }
    }

    public void addAll(Collection<IPlayer> playerCollection) {
        synchronized (mutex) {
            players.addAll(playerCollection);
        }
    }

    public int size() {
        synchronized (mutex) {
            return players.size();
        }
    }

    public boolean contains(IPlayer player) {
        synchronized (mutex) {
            return players.contains(player);
        }
    }

    public boolean contains(String playerId) {
        return getPlayer(playerId) != null;
    }

    public boolean containsPlayerWithName(String name) {
        synchronized (mutex) {
            Optional<IPlayer> optionalPlayer = players.stream().filter(player -> player.getName().equals(name)).findFirst();
            if (optionalPlayer.isPresent()) {
                return true;
            }
            return false;
        }
    }

    public Set<IPlayer> getConnectedPlayersStillInTournament() {
        synchronized (mutex) {
            return players.stream()
                    .filter(IPlayer::isConnected)
                    .collect(Collectors.toSet());
        }
    }

    public Set<IPlayer> getLivePlayers() {
        synchronized (mutex) {
            return players.stream()
                    .filter(IPlayer::isAlive)
                    .collect(Collectors.toSet());
        }
    }

    public IPlayer getPlayer(String playerId) {
        synchronized (mutex) {
            Optional<IPlayer> optionalPlayer = players.stream().filter(player -> player.getPlayerId().equals(playerId)).findFirst();
            if (optionalPlayer.isPresent()) {
                return optionalPlayer.get();
            }
            return null;
        }
    }

    public Set<IPlayer> getLiveAndRemotePlayers() {
        synchronized (mutex) {
            return players.stream().filter(player ->
                    player.isAlive() && player instanceof RemotePlayer
            ).collect(Collectors.toSet());
        }
    }

    public Set<IPlayer> toSet() {
        synchronized (mutex) {
            Set<IPlayer> copy = new HashSet<>();
            copy.addAll(players);
            return copy;
        }
    }

    public String getPlayerName(String playerId) {
        synchronized (mutex) {
            IPlayer player = getPlayer(playerId);
            if (player != null) {
                return player.getName();
            }

            return null;
        }
    }
}
