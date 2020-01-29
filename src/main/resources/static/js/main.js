drawBoard(361);
$( document ).ready(function() {
    connect();
});

var stompClient = null;
var username = "";
var bgc = "#c2a972";

function drawBoard(size) {
    for(var i = 0; i<size; i++){
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
    stompClient.send("/app/game.addPlayer", {},
        JSON.stringify({player: username})
    )
}

function onError(error) { alert("Something went wrong"); }

function sendMove(slot) {
    if(slot && stompClient) {
        var gameMessage = {
            player: username,
            content: slot,
            type: 'MOVE'
        };
        stompClient.send("/app/game.sendMessage", {}, JSON.stringify(gameMessage));
    }
}

function sendButton(type) {
    if(type && stompClient) {
        var gameMessage = {
            player: username,
            type: type
        };
        stompClient.send("/app/game.sendMessage", {}, JSON.stringify(gameMessage));
    }
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    if (message.type === 'MOVE'){
        document.getElementById(message.content).style.background = message.player;
        document.getElementById(message.content).style.opacity = "1.0";

    } else if (message.type === 'ERROR' && message.player === username){
        alert(message.content);
    } else if (message.type === 'REMOVE'){
        document.getElementById(message.content).style.background = bgc;
    } else if (message.type === 'RESIGN'){
        if (message.player === username){
            alert("Twoje jest przegranko");
        } else {
            alert("Przeciwnik uznal, ze jestes za dobry/a i sie poddal")
        }
    } else if (message.type === 'PASS'){
        alert(message.content);
    }
}

function gameOn(payload) {
    var message = JSON.parse(payload.body);
    if(username === ""){
        username = message.player;
    }
}

$(".dzban").click(function(){
    var siup = $(this).attr('id');
    sendMove(siup);
});