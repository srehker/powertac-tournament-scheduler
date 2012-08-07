/**
 * Created with IntelliJ IDEA.
 * User: govert
 * Date: 8/3/12
 * Time: 4:10 PM
 * To change this template use File | Settings | File Templates.
 */

function showGamesCount() {
    var elems = saveTournament.elements;
    var maxBrokers = elems[2].value;
    var gameType1 = elems[4].value;
    var gameType2 = elems[5].value;
    var gameType3 = elems[6].value;

    setText("totalGames", "");
    setText("total1", "");
    setText("total2", "");
    setText("total3", "");

    var total1 = 0;
    var total2 = 0;
    var total3 = 0;
    if ((maxBrokers > 0) && (gameType1 > 0)) {
        if (gameType1 > maxBrokers) {
            elems[2].value = maxBrokers;
            gameType1 = maxBrokers;
        }
        total1 = calculateGames(maxBrokers, gameType1.value);
        setText("total1", "Games for this type : " + total1);
    }
    if ((maxBrokers > 0) && (gameType2 > 0)) {
        if (gameType2 > maxBrokers) {
            elems[3].value = maxBrokers;
            gameType2 = maxBrokers;
        }
        total2 = calculateGames(maxBrokers, gameType2);
        setText("total2", "Games for this type : " + total2);
    }
    if ((maxBrokers > 0) && (gameType3 > 0)) {
        if (gameType3 > maxBrokers) {
            elems[4].value = maxBrokers;
            gameType3 = maxBrokers;
        }
        total3 = calculateGames(maxBrokers, gameType3);
        setText("total3", "Games for this type : " + total3);
    }

    var total = total1 + total2 + total3;
    if (total > 0) {
        setText("totalGames", "Total games to be created : " + total);
    }
}

function setText(fieldId, newText) {
    var el = document.getElementById(fieldId);
    while(el.childNodes.length >= 1) {
        el.removeChild(el.firstChild);
    }
    el.appendChild(el.ownerDocument.createTextNode(newText));
}

function calculateGames(players, gametype) {
    if (players == gametype) {
        return 1;
    }
    if (gametype == 1) {
        return players;
    }
    return calculateGames(players-1, gametype) + calculateGames(players-1, gametype-1);
}