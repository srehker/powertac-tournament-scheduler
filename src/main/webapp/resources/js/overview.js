/**
 * Created with IntelliJ IDEA.
 * User: govert
 * Date: 1/16/13
 * Time: 2:03 PM
 */

function toggleSample() {
    if ($("#toggleSampleButton").val() == "Hide sample") {
        $('#toggleSampleButton').val("Show sample");

        $("#formDatabrokers\\:databrokers").find('>tbody>tr').each(function () {
            var sp = $(this).find('td:nth-child(2) a')[0];
            var name = $(sp).text();

            if (name.toLowerCase().indexOf("sample") > -1) {
                $(this).css("display", "none");
            }
        });
    } else {
        $('#toggleSampleButton').val("Hide sample");

        $("#formDatabrokers\\:databrokers").find(">tbody>tr").each(function () {
            $(this).css("display", "");
        });
    }
}

function toggleActive() {
    var active_statuses = new Array('boot_in_progress', 'game_pending', 'game_ready', 'game_in_progress');

    if ($("#toggleActiveButton").val() == "Hide inactive") {
        $('#toggleActiveButton').val("Show inactive");

        $("#gamesForm\\:datagames").find('>tbody>tr').each(function () {
            var sp = $(this).find('td:nth-child(3) span')[0];
            var status = $(sp).text();

            if (!($.inArray(status, active_statuses) > -1)) {
                $(this).css("display", "none");
            }
        });
    } else {
        $('#toggleActiveButton').val("Hide inactive");

        $("#gamesForm\\:datagames").find(">tbody>tr").each(function () {
            $(this).css("display", "");
        });
    }
}

function updateBrokers(data) {
    $('#formDatabrokers\\:databrokers').find('>tbody>tr').each(function () {
        var sp = $(this).find('td:first-child span')[0];
        var orgRowNr = $(sp).attr("id").split(":")[2];

        if (data[$(sp).text()] != undefined) {
            $('#formDatabrokers\\:databrokers\\:' + orgRowNr + '\\:checkins').html(data[$(sp).text()]);
        } else {
            $('#formDatabrokers\\:databrokers\\:' + orgRowNr + '\\:checkins').html("");
        }
    });
}

function updateGames(data) {
    $('#gamesForm\\:datagames').find('>tbody>tr').each(function () {
        var sp = $(this).find('td:first-child span')[0];
        var orgRowNr = $(sp).attr("id").split(":")[2];

        if (data[$(sp).text()] != undefined) {
            $('#gamesForm\\:datagames\\:' + orgRowNr + '\\:heartbeat').html(data[$(sp).text()]);
        } else {
            $('#gamesForm\\:datagames\\:' + orgRowNr + '\\:heartbeat').html("");
        }
    });
}

function updateTables() {
    $.ajax({
        url: "Rest?type=brokers",
        success: updateBrokers
    });
    $.ajax({
        url: "Rest?type=games",
        success: updateGames
    });
}

function resizeTables() {
    $('[id$=databrokers]').dataTable({
        "bFilter": false,
        "bInfo": false,
        "sScrollY": Math.min(400, $("[id$=databrokers]").height()) + "px",
        "bPaginate": false,
        "aoColumnDefs": [
            { 'bSortable': false, 'aTargets': [4, 6] },
            { "sType": "natural", "aTargets": [0] }
        ]
    });
    $('[id$=datatourneys]').dataTable({
        "bFilter": false,
        "bInfo": false,
        "sScrollY": Math.min(400, $("[id$=datatourneys]").height()) + "px",
        "bPaginate": false,
        "aoColumnDefs": [
            { 'bSortable': false, 'aTargets': [5, 6, 7, 8] },
            { "sType": "natural", "aTargets": [0] }
        ]
    });
    $('[id$=datagames]').dataTable({
        "bFilter": false,
        "bInfo": false,
        "sScrollY": Math.min(400, $("[id$=datagames]").height()) + "px",
        "bPaginate": false,
        "aoColumnDefs": [
            { 'bSortable': false, 'aTargets': [3, 4, 5, 6] },
            { "sType": "natural", "aTargets": [0, 1] }
        ]
    });
}

$(document).ready(function () {
    resizeTables();
    toggleActive();
    updateTables();
    setInterval(updateTables, 3000);
});