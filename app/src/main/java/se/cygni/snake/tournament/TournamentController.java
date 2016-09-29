package se.cygni.snake.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.cygni.snake.eventapi.model.TournamentInfo;

@RestController
public class TournamentController {

    private final TournamentManager tournamentManager;

    @Autowired
    public TournamentController(TournamentManager tournamentManager) {
        this.tournamentManager = tournamentManager;
    }

    @RequestMapping("/tournament/active")
    public ResponseEntity<TournamentInfo> getActiveTournament() {
        if (tournamentManager.isTournamentActive()) {
            return new ResponseEntity<TournamentInfo>(new TournamentInfo(
                    tournamentManager.getTournamentId(),
                    tournamentManager.getTournamentName(),
                    tournamentManager.getGameSettings(),
                    tournamentManager.getTournamentPlan()),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
