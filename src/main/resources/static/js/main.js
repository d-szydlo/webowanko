drawBoard(361);
$( document ).ready(function() {
    connect();
});

var stompClient = null;
var username = "";
var bgc = "#c29861";

function drawBoard(size) {
    for(i = 0; i<size; i++){
        $("#slots").append('<li class="dzban"id='+i+'></li>')
    }
}

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);
    stompClient.subscribe('/topic/color', gameOn);
    stompClient.send("/app/game.addUser", {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )
}

function onError(error) { alert("Something went wrong"); }

function sendMessage(slot) {
    if(slot && stompClient) {
        var gameMessage = {
            player: username,
            content: slot,
            type: 'MOVE'
        };
        stompClient.send("/app/game.sendMessage", {}, JSON.stringify(gameMessage));
    }
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    document.getElementById(message.content).style.background = message.color;
    //alert(message.content + " " + message.color);
}

function gameOn(payload) {
    var message = JSON.parse(payload.body);
    if(username === ""){
        username = message.player;
    }
}

function remove(payload) {
    var field = payload.body;
    document.getElementById(field).style.background = bgc;
}


$(".dzban").click(function(){
    var slot = $(this).attr('id');
    sendMessage(slot);
});