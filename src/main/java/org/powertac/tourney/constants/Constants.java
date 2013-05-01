package org.powertac.tourney.constants;

import org.powertac.tourney.beans.Tournament;
import org.powertac.tourney.beans.Game;
import org.powertac.tourney.beans.Round;

public class Constants
{
  public class Props
  {
    public static final String weatherServerURL =
        "server.weatherService.serverUrl = %s\n";
    public static final String weatherLocation =
        "server.weatherService.weatherLocation = %s\n";
    public static final String startTime =
        "common.competition.simulationBaseTime = %s\n";
    public static final String jms =
        "server.jmsManagementService.jmsBrokerUrl = %s\n";
    public static final String serverFirstTimeout =
        "server.competitionControlService.firstLoginTimeout = %s\n";
    public static final String serverTimeout =
        "server.competitionControlService.loginTimeout = %s\n";
    public static final String remote =
        "server.visualizerProxyService.remoteVisualizer = %s\n";
    public static final String vizQ =
        "server.visualizerProxyService.visualizerQueueName = %s\n";
    public static final String minTimeslot =
        "common.competition.minimumTimeslotCount = %s\n";
    public static final String expectedTimeslot =
        "common.competition.expectedTimeslotCount = %s\n";
    public static final String timezoneOffset =
        "common.competition.timezoneOffset = %s\n";
  }

  public class Rest
  {
    // Possible Rest Parameters for Broker Login
    public static final String REQ_PARAM_AUTH_TOKEN = "authToken";
    public static final String REQ_PARAM_JOIN = "requestJoin";
    public static final String REQ_PARAM_TYPE = "type";

    // Possible Rest Paramenters for Server Interface
    public static final String REQ_PARAM_STATUS = "status";
    public static final String REQ_PARAM_GAMEID = "gameId";
    public static final String REQ_PARAM_ACTION = "action";
    public static final String REQ_PARAM_FILENAME = "fileName";
    public static final String REQ_PARAM_MESSAGE = "message";
    public static final String REQ_PARAM_BOOT = "boot";
    public static final String REQ_PARAM_HEARTBEAT = "heartbeat";
    public static final String REQ_PARAM_GAMERESULTS = "gameresults";
    public static final String REQ_PARAM_GAMELENGTH = "gameLength";
    public static final String REQ_PARAM_STANDINGS = "standings";

    // Possible Rest Parameters for pom service
    public static final String REQ_PARAM_POM_ID = "pomId";
  }

  public static class HQL
  {
    public static final String GET_USERS =
        "FROM User AS user ";

    public static final String GET_USER_BY_NAME =
        "FROM User AS user "
            + "LEFT JOIN FETCH user.brokerMap AS brokerMap "
            + "LEFT JOIN FETCH brokerMap.roundMap AS roundMap "
            + "LEFT JOIN FETCH brokerMap.tournamentMap AS tournamentMap "
            + "LEFT JOIN FETCH brokerMap.agentMap AS agentMap "
            + "WHERE user.userName =:userName ";

    public static final String GET_POMS =
        "FROM Pom AS pom "
            + "LEFT JOIN FETCH pom.user "
            + "order by pom.pomId desc ";

    public static final String GET_LOCATIONS =
        "FROM Location AS location ";

    public static final String GET_LOCATION_BY_NAME =
        "FROM Location AS location WHERE location.location =:locationName ";

    public static final String GET_MACHINE_BY_MACHINENAME =
        "FROM Machine AS machine "
            + " WHERE machine.machineName =:machineName ";

    public static final String GET_MACHINES =
        "FROM Machine AS machine ";

    public static final String GET_TOURNAMENT_BY_ID =
        "FROM Tournament AS tournament "
            + "LEFT JOIN FETCH tournament.levelMap AS levelMap "
            + "LEFT JOIN FETCH levelMap.roundMap AS roundMap "
            + "LEFT JOIN FETCH roundMap.gameMap AS gameMap "
            + "LEFT JOIN FETCH gameMap.agentMap as agentMap "
            + "LEFT JOIN FETCH roundMap.brokerMap AS brokerMap "
            + "WHERE tournament.tournamentId =:tournamentId";

    public static final String GET_TOURNAMENT_BY_NAME =
        "FROM Tournament AS tournament "
            + "LEFT JOIN FETCH tournament.levelMap AS levelMap "
            + "LEFT JOIN FETCH levelMap.roundMap AS roundMap "
            + "WHERE tournament.tournamentName =:tournamentName";

    public static final String GET_TOURNAMENT_NOT_COMPLETE =
        "FROM Tournament AS tournament "
            + "LEFT JOIN FETCH tournament.levelMap AS levelMap "
            + "LEFT JOIN FETCH levelMap.roundMap AS roundMap "
            + "LEFT JOIN FETCH roundMap.gameMap as gameMap "
            + "LEFT JOIN FETCH gameMap.agentMap as agentMap "
            + "LEFT JOIN FETCH roundMap.brokerMap as brokerMap "
            + "WHERE NOT tournament.state='" + Tournament.getStateComplete() + "'";

    public static final String GET_ROUND_BY_ID =
        "FROM Round AS round "
            + "LEFT JOIN FETCH round.gameMap AS gameMap "
            + "LEFT JOIN FETCH gameMap.agentMap AS agentMap "
            + "LEFT JOIN FETCH round.brokerMap AS brokerMap "
            + "LEFT JOIN FETCH brokerMap.user "
            + "WHERE round.roundId =:roundId";

    public static final String GET_ROUNDS_NOT_COMPLETE =
        "FROM Round AS round "
            + "LEFT JOIN FETCH round.gameMap as gameMap "
            + "LEFT JOIN FETCH gameMap.agentMap AS agentMap "
            + "LEFT JOIN FETCH gameMap.machine "
            + "LEFT JOIN FETCH agentMap.broker "
            + "LEFT JOIN FETCH agentMap.game "
            + "LEFT JOIN FETCH round.brokerMap AS brokerMap "
            + "LEFT JOIN FETCH brokerMap.user "
            + "WHERE NOT round.state='" + Round.getStateComplete() + "'";

    public static final String GET_GAME_BY_ID =
        "FROM Game AS game "
            + "LEFT JOIN FETCH game.round "
            + "LEFT JOIN FETCH game.machine "
            + "LEFT JOIN FETCH game.agentMap as agentMap "
            + "LEFT JOIN FETCH agentMap.broker as broker "
            + "LEFT JOIN FETCH broker.user "
            + "WHERE game.gameId =:gameId";

    public static final String GET_GAMES_BOOT_PENDING =
        "FROM Game AS game "
            + "LEFT JOIN FETCH game.round AS round "
            + "LEFT JOIN FETCH game.machine "
            + "LEFT JOIN FETCH game.agentMap "
            + "WHERE game.state='" + Game.getStateBootPending() + "' "
            + "AND round.roundId =:roundId";

    public static final String GET_GAMES_BOOT_COMPLETE =
        "FROM Game AS game "
            + "LEFT JOIN FETCH game.round AS round "
            + "LEFT JOIN FETCH game.machine "
            + "LEFT JOIN FETCH game.agentMap AS agentMap "
            + "LEFT JOIN FETCH agentMap.broker AS broker "
            + "LEFT JOIN FETCH broker.user "
            + "LEFT JOIN FETCH broker.agentMap "

            + "WHERE game.state='" + Game.getStateBootComplete() + "' "
            + "AND game.startTime < :startTime "
            + "AND round.roundId =:roundId";

    public static final String GET_GAMES_NOT_COMPLETE =
        "FROM Game AS game "
            + "LEFT JOIN FETCH game.round "
            + "LEFT JOIN FETCH game.machine "
            + "LEFT JOIN FETCH game.agentMap agentMap "
            + "LEFT JOIN FETCH agentMap.broker as broker "
            + "LEFT JOIN FETCH broker.user "
            + "WHERE NOT game.state='" + Game.getStateGameComplete() + "' ";

    public static final String GET_GAMES_READY =
        "FROM Game AS game "
            + "LEFT JOIN FETCH game.round "
            + "LEFT JOIN FETCH game.machine "
            + "LEFT JOIN FETCH game.agentMap agentMap "
            + "LEFT JOIN FETCH agentMap.broker as broker "
            + "WHERE game.state='" + Game.getStateGameReady() + "' ";

    public static final String GET_GAMES_COMPLETE =
        "FROM Game AS game "
            + "LEFT JOIN FETCH game.round "
            + "LEFT JOIN FETCH game.machine "
            + "LEFT JOIN FETCH game.agentMap agentMap "
            + "LEFT JOIN FETCH agentMap.broker as broker "
            + "LEFT JOIN FETCH broker.user "
            + "WHERE game.state='" + Game.getStateGameComplete() + "' ";

    public static final String GET_BROKERS =
        "FROM Broker AS broker "
            + "LEFT JOIN FETCH broker.roundMap "
            + "LEFT JOIN FETCH broker.tournamentMap as tournamentMap "
            + "LEFT JOIN FETCH broker.user "
            + "LEFT JOIN FETCH broker.agentMap as agentMap "
            + "LEFT JOIN FETCH tournamentMap.levelMap as levelMap "
            + "LEFT JOIN FETCH levelMap.roundMap as roundMap ";

    public static final String GET_BROKER_BY_ID =
        "FROM Broker AS broker "
            + "LEFT JOIN FETCH broker.user "
            + "LEFT JOIN FETCH broker.roundMap "
            + "LEFT JOIN FETCH broker.agentMap as agentMap "
            + "LEFT JOIN FETCH agentMap.game "
            + "LEFT JOIN FETCH agentMap.broker as broker2 "
            + "LEFT JOIN FETCH broker2.user "
            + "WHERE broker.brokerId =:brokerId ";

    public static final String GET_BROKER_BY_NAME =
        "FROM Broker AS broker "
            + "LEFT JOIN FETCH broker.user "
            + "WHERE brokerName =:brokerName ";

    public static final String GET_BROKER_BY_BROKERAUTH =
        "FROM Broker AS broker "
            + "LEFT JOIN FETCH broker.user "
            + "LEFT JOIN FETCH broker.roundMap "
            + "LEFT JOIN FETCH broker.agentMap as agentMap "
            + "LEFT JOIN FETCH agentMap.game as game "
            + "LEFT JOIN FETCH game.round "
            + "LEFT JOIN FETCH game.machine "
            + "LEFT JOIN FETCH agentMap.broker as broker2 "
            + "LEFT JOIN FETCH broker2.user "
            + "WHERE broker.brokerAuth =:brokerAuth ";

    public static final String GET_CONFIG =
        "FROM Config AS config "
            + "WHERE config.configKey =:configKey ";
  }
}
